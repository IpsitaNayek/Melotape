package com.melotape.player.controller

import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import com.melotape.data.datastore.SettingsDataStore
import com.melotape.di.ApplicationScope
import com.melotape.domain.model.Song
import com.melotape.player.connection.PlayerConnection
import com.melotape.player.model.PlaybackStateUi
import com.melotape.player.resolver.AudioSourceResolver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlayerControllerImpl @Inject constructor(
    private val playerConnection: PlayerConnection,
    private val audioSourceResolver: AudioSourceResolver,
    private val settingsDataStore: SettingsDataStore,
    @param:ApplicationScope private val applicationScope: CoroutineScope,
) : PlayerController {

    private val _playbackState = MutableStateFlow(PlaybackStateUi())
    override val playbackState: StateFlow<PlaybackStateUi> = _playbackState.asStateFlow()

    private var activeQueue: List<Song> = emptyList()
    private var positionPollingJob: Job? = null

    private val playerListener = object : Player.Listener {
        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            val songId = mediaItem?.mediaId
            val foundIndex = activeQueue.indexOfFirst { it.id == songId }
            val song = if (foundIndex >= 0) activeQueue[foundIndex] else null

            val ctrl = playerConnection.controller.value
            val duration = ctrl?.duration?.takeIf { it > 0 } ?: (song?.durationMs ?: 0L)

            _playbackState.update { current ->
                current.copy(
                    currentSong = song,
                    currentIndex = foundIndex,
                    durationMs = duration,
                    currentPositionMs = ctrl?.currentPosition ?: 0L,
                )
            }
        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            _playbackState.update { it.copy(isPlaying = isPlaying) }
            if (isPlaying) {
                startPositionPolling()
            } else {
                stopPositionPolling()
            }
        }

        override fun onPlaybackStateChanged(playbackState: Int) {
            val ctrl = playerConnection.controller.value
            val duration = ctrl?.duration?.takeIf { it > 0 } ?: _playbackState.value.durationMs
            val buffered = ctrl?.bufferedPosition ?: 0L

            _playbackState.update {
                it.copy(
                    durationMs = duration,
                    bufferedPositionMs = buffered,
                )
            }
        }

        override fun onRepeatModeChanged(repeatMode: Int) {
            _playbackState.update { it.copy(repeatMode = repeatMode) }
            applicationScope.launch {
                settingsDataStore.setRepeatMode(repeatMode)
            }
        }

        override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
            _playbackState.update { it.copy(shuffleEnabled = shuffleModeEnabled) }
            applicationScope.launch {
                settingsDataStore.setShuffleEnabled(shuffleModeEnabled)
            }
        }
    }

    init {
        applicationScope.launch {
            playerConnection.controller.collect { controller ->
                controller?.let { ctrl ->
                    ctrl.removeListener(playerListener)
                    ctrl.addListener(playerListener)
                    syncInitialState(ctrl)
                }
            }
        }
    }

    private fun syncInitialState(controller: MediaController) {
        val currentMediaId = controller.currentMediaItem?.mediaId
        val song = activeQueue.find { it.id == currentMediaId }
        val duration = controller.duration.takeIf { it > 0 } ?: (song?.durationMs ?: 0L)

        _playbackState.update {
            it.copy(
                currentSong = song,
                isPlaying = controller.isPlaying,
                currentPositionMs = controller.currentPosition,
                durationMs = duration,
                bufferedPositionMs = controller.bufferedPosition,
                repeatMode = controller.repeatMode,
                shuffleEnabled = controller.shuffleModeEnabled,
            )
        }

        if (controller.isPlaying) {
            startPositionPolling()
        }
    }

    private fun startPositionPolling() {
        positionPollingJob?.cancel()
        positionPollingJob = applicationScope.launch {
            while (isActive) {
                val ctrl = playerConnection.controller.value
                if (ctrl != null && ctrl.isPlaying) {
                    _playbackState.update {
                        it.copy(
                            currentPositionMs = ctrl.currentPosition,
                            bufferedPositionMs = ctrl.bufferedPosition,
                            durationMs = ctrl.duration.takeIf { d -> d > 0 } ?: it.durationMs,
                        )
                    }
                }
                delay(250L)
            }
        }
    }

    private fun stopPositionPolling() {
        positionPollingJob?.cancel()
        positionPollingJob = null
        val ctrl = playerConnection.controller.value
        if (ctrl != null) {
            _playbackState.update {
                it.copy(currentPositionMs = ctrl.currentPosition)
            }
        }
    }

    override fun setQueue(songs: List<Song>, startIndex: Int, playWhenReady: Boolean) {
        activeQueue = songs
        _playbackState.update { it.copy(queue = songs, currentIndex = startIndex) }

        val ctrl = playerConnection.controller.value ?: run {
            playerConnection.connect()
            return
        }

        val mediaItems = songs.map { audioSourceResolver.toMediaItem(it) }
        ctrl.setMediaItems(mediaItems, startIndex, 0L)
        ctrl.prepare()
        if (playWhenReady) {
            ctrl.play()
        }
    }

    override fun play() {
        val ctrl = playerConnection.controller.value ?: return
        ctrl.play()
    }

    override fun pause() {
        val ctrl = playerConnection.controller.value ?: return
        ctrl.pause()
    }

    override fun togglePlayPause() {
        val ctrl = playerConnection.controller.value ?: return
        if (ctrl.isPlaying) {
            ctrl.pause()
        } else {
            ctrl.play()
        }
    }

    override fun seekTo(positionMs: Long) {
        val ctrl = playerConnection.controller.value ?: return
        val clamped = positionMs.coerceIn(0L, ctrl.duration.coerceAtLeast(0L))
        ctrl.seekTo(clamped)
        _playbackState.update { it.copy(currentPositionMs = clamped) }
    }

    override fun seekRelative(offsetMs: Long) {
        val ctrl = playerConnection.controller.value ?: return
        val target = (ctrl.currentPosition + offsetMs).coerceIn(0L, ctrl.duration.coerceAtLeast(0L))
        ctrl.seekTo(target)
        _playbackState.update { it.copy(currentPositionMs = target) }
    }

    override fun next() {
        val ctrl = playerConnection.controller.value ?: return
        if (ctrl.hasNextMediaItem()) {
            ctrl.seekToNextMediaItem()
        }
    }

    override fun previous() {
        val ctrl = playerConnection.controller.value ?: return
        if (ctrl.currentPosition > 3000L || !ctrl.hasPreviousMediaItem()) {
            ctrl.seekTo(0L)
        } else {
            ctrl.seekToPreviousMediaItem()
        }
    }

    override fun setRepeatMode(mode: Int) {
        val ctrl = playerConnection.controller.value ?: return
        ctrl.repeatMode = mode
    }

    override fun toggleShuffle() {
        val ctrl = playerConnection.controller.value ?: return
        ctrl.shuffleModeEnabled = !ctrl.shuffleModeEnabled
    }

    override fun stop() {
        val ctrl = playerConnection.controller.value ?: return
        ctrl.stop()
        stopPositionPolling()
        _playbackState.update { it.copy(isPlaying = false, currentPositionMs = 0L) }
    }
}

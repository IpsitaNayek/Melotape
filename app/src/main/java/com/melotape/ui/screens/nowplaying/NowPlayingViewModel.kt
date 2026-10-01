package com.melotape.ui.screens.nowplaying

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.melotape.data.datastore.SettingsDataStore
import com.melotape.domain.model.Song
import com.melotape.domain.repository.MusicRepository
import com.melotape.player.controller.PlayerController
import com.melotape.player.model.PlaybackStateUi
import com.melotape.ui.mapper.toItemUi
import com.melotape.ui.model.AnalogSpecsUi
import com.melotape.ui.model.SongItemUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class NowPlayingUiState(
    val currentSong: SongItemUi? = null,
    val isPlaying: Boolean = false,
    val progress: Float = 0f,
    val elapsedText: String = "00:00",
    val remainingText: String = "-00:00",
    val speedText: String = "4.76 cm/s",
    val sideChip: String = "SIDE A • C-60",
    val specs: AnalogSpecsUi = AnalogSpecsUi(
        encoding = "FLAC 24",
        headroom = "+3.2 dB VU",
        warmth = "Type II Bias",
        dolbyActive = true,
    ),
    val volume: Float = 0.75f,
    val isShuffle: Boolean = false,
    val isRepeat: Boolean = false,
    val showDetailsSheet: Boolean = false,
)

@HiltViewModel
class NowPlayingViewModel @Inject constructor(
    private val playerController: PlayerController,
    private val musicRepository: MusicRepository,
    private val settingsDataStore: SettingsDataStore,
) : ViewModel() {

    private val showDetailsSheetState = MutableStateFlow(false)
    private val volumeState = MutableStateFlow(0.75f)

    val uiState: StateFlow<NowPlayingUiState> = combine(
        playerController.playbackState,
        settingsDataStore.isHissFilterActive,
        showDetailsSheetState,
        volumeState,
    ) { playback, hissFilterActive, showSheet, volume ->
        val songItem = playback.currentSong?.toItemUi()
        val durationMs = playback.durationMs.coerceAtLeast(1L)
        val posMs = playback.currentPositionMs.coerceIn(0L, durationMs)
        val progress = (posMs.toFloat() / durationMs.toFloat()).coerceIn(0f, 1f)

        val elapsedSec = (posMs / 1000L).toInt()
        val elapsedFormatted = String.format("%02d:%02d", elapsedSec / 60, elapsedSec % 60)

        val remainingSec = ((durationMs - posMs) / 1000L).toInt().coerceAtLeast(0)
        val remainingFormatted = String.format("-%02d:%02d", remainingSec / 60, remainingSec % 60)

        NowPlayingUiState(
            currentSong = songItem,
            isPlaying = playback.isPlaying,
            progress = progress,
            elapsedText = elapsedFormatted,
            remainingText = remainingFormatted,
            speedText = if (playback.isPlaying) "4.76 cm/s" else "PAUSED",
            sideChip = if (songItem != null) "SIDE A • C-60" else "DECK EMPTY",
            specs = AnalogSpecsUi(
                encoding = songItem?.formatLabel ?: "FLAC 24",
                headroom = if (playback.isPlaying) "+3.2 dB VU" else "0.0 dB VU",
                warmth = if (hissFilterActive) "Dolby B Tape NR" else "Type I Normal",
                dolbyActive = hissFilterActive,
            ),
            volume = volume,
            isShuffle = playback.shuffleEnabled,
            isRepeat = playback.repeatMode != 0,
            showDetailsSheet = showSheet,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = NowPlayingUiState(),
    )

    init {
        // If queue is completely empty upon initial launch, load seed or recent songs
        viewModelScope.launch {
            if (playerController.playbackState.value.currentSong == null) {
                musicRepository.getRecentlyPlayed().firstOrNull()?.let { songs ->
                    if (songs.isNotEmpty()) {
                        playerController.setQueue(songs = songs, startIndex = 0, playWhenReady = false)
                    }
                }
            }
        }
    }

    fun onPlayPause() {
        playerController.togglePlayPause()
    }

    fun onSeek(newProgress: Float) {
        val durationMs = playerController.playbackState.value.durationMs
        if (durationMs > 0) {
            val targetMs = (durationMs * newProgress).toLong()
            playerController.seekTo(targetMs)
        }
    }

    fun onSeekRelative(offsetMs: Long) {
        playerController.seekRelative(offsetMs)
    }

    fun onToggleLoved() {
        val song = playerController.playbackState.value.currentSong ?: return
        viewModelScope.launch {
            musicRepository.toggleLoved(song.id)
        }
    }

    fun onNext() {
        playerController.next()
    }

    fun onPrevious() {
        playerController.previous()
    }

    fun onToggleShuffle() {
        playerController.toggleShuffle()
    }

    fun onToggleRepeat() {
        val currentMode = playerController.playbackState.value.repeatMode
        val nextMode = when (currentMode) {
            0 -> 2 // Repeat All
            2 -> 1 // Repeat One
            else -> 0 // Off
        }
        playerController.setRepeatMode(nextMode)
    }

    fun onSetVolume(newVolume: Float) {
        volumeState.value = newVolume
    }

    fun onToggleDetailsSheet(show: Boolean) {
        showDetailsSheetState.value = show
    }

    fun playSong(song: Song, queue: List<Song> = emptyList()) {
        val fullQueue = if (queue.isNotEmpty()) queue else listOf(song)
        val index = fullQueue.indexOfFirst { it.id == song.id }.coerceAtLeast(0)
        playerController.setQueue(fullQueue, index, playWhenReady = true)
    }
}

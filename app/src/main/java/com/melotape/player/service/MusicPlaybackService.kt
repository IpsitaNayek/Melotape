package com.melotape.player.service

import android.content.Intent
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.melotape.data.history.HistoryRecorder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MusicPlaybackService : MediaSessionService() {

    @Inject
    lateinit var player: ExoPlayer

    @Inject
    lateinit var historyRecorder: HistoryRecorder

    private var session: MediaSession? = null
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private var currentHistoryId: Long = 0L
    private var lastPlayTimestamp: Long = 0L

    override fun onCreate() {
        super.onCreate()

        session = MediaSession.Builder(this, player).build()

        player.addListener(object : Player.Listener {
            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                commitCurrentSessionDuration()
                val songId = mediaItem?.mediaId
                if (!songId.isNullOrBlank()) {
                    serviceScope.launch {
                        currentHistoryId = historyRecorder.onTrackStarted(songId)
                        lastPlayTimestamp = System.currentTimeMillis()
                    }
                } else {
                    currentHistoryId = 0L
                    lastPlayTimestamp = 0L
                }
            }

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                if (isPlaying) {
                    lastPlayTimestamp = System.currentTimeMillis()
                } else {
                    commitCurrentSessionDuration()
                }
            }
        })
    }

    private fun commitCurrentSessionDuration() {
        if (currentHistoryId > 0 && lastPlayTimestamp > 0) {
            val elapsed = System.currentTimeMillis() - lastPlayTimestamp
            if (elapsed > 0) {
                val histId = currentHistoryId
                serviceScope.launch {
                    historyRecorder.onListenedDuration(histId, elapsed)
                }
            }
        }
        lastPlayTimestamp = 0L
    }

    override fun onGetSession(info: MediaSession.ControllerInfo): MediaSession? = session

    override fun onTaskRemoved(rootIntent: Intent?) {
        val p = session?.player
        if (p == null || !p.playWhenReady || p.mediaItemCount == 0) {
            stopSelf()
        }
    }

    override fun onDestroy() {
        commitCurrentSessionDuration()
        serviceScope.cancel()
        session?.run {
            player.release()
            release()
        }
        session = null
        super.onDestroy()
    }
}

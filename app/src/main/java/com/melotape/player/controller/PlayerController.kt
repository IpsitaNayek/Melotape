package com.melotape.player.controller

import com.melotape.domain.model.Song
import com.melotape.player.model.PlaybackStateUi
import kotlinx.coroutines.flow.StateFlow

interface PlayerController {
    val playbackState: StateFlow<PlaybackStateUi>

    fun setQueue(songs: List<Song>, startIndex: Int = 0, playWhenReady: Boolean = true)
    fun play()
    fun pause()
    fun togglePlayPause()
    fun seekTo(positionMs: Long)
    fun seekRelative(offsetMs: Long)
    fun next()
    fun previous()
    fun setRepeatMode(mode: Int)
    fun toggleShuffle()
    fun stop()
}

package com.melotape.player.model

import com.melotape.domain.model.Song

data class PlaybackStateUi(
    val currentSong: Song? = null,
    val isPlaying: Boolean = false,
    val currentPositionMs: Long = 0L,
    val durationMs: Long = 0L,
    val bufferedPositionMs: Long = 0L,
    val repeatMode: Int = 0, // 0 = OFF, 1 = ALL, 2 = ONE
    val shuffleEnabled: Boolean = false,
    val playbackSpeed: Float = 1.0f,
    val queue: List<Song> = emptyList(),
    val currentIndex: Int = -1,
)

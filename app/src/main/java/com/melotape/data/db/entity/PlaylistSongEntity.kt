package com.melotape.data.db.entity

import androidx.room.Entity
import androidx.room.Index

@Entity(
    tableName = "playlist_songs",
    primaryKeys = ["playlistId", "songId"],
    indices = [Index(value = ["playlistId", "position"])],
)
data class PlaylistSongEntity(
    val playlistId: String,
    val songId: String,
    val position: Int,
    val addedAt: Long = System.currentTimeMillis(),
)

package com.melotape.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.melotape.domain.model.Playlist

@Entity(tableName = "playlists")
data class PlaylistEntity(
    @PrimaryKey val id: String,
    val name: String,
    val description: String? = null,
    val coverArtUri: String? = null,
    val pinned: Boolean = false,
    val updatedAt: Long = System.currentTimeMillis(),
    val createdAt: Long = System.currentTimeMillis(),
    val cloudId: String? = null,
    val syncState: String = "LOCAL_ONLY",
    val deleted: Boolean = false,
)

fun PlaylistEntity.toDomain(songCount: Int = 0, totalDurationMs: Long = 0L): Playlist {
    return Playlist(
        id = id,
        name = name,
        description = description,
        coverArtUri = coverArtUri,
        songCount = songCount,
        totalDurationMs = totalDurationMs,
        isPinned = pinned,
        cloudId = cloudId,
        createdAt = createdAt,
        updatedAt = updatedAt,
    )
}

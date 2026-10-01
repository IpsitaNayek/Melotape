package com.melotape.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites")
data class FavoriteEntity(
    @PrimaryKey val songId: String, // Source-aware ID: LOCAL:..., JAMENDO:...
    val favoritedAt: Long = System.currentTimeMillis(),
)

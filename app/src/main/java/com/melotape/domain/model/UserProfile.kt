package com.melotape.domain.model

data class UserProfile(
    val id: String,
    val displayName: String,
    val handle: String?,
    val email: String?,
    val avatarUrl: Any?,
    val isProDeck: Boolean = true,
    val dolbyBActive: Boolean = true,
    val hissFilterActive: Boolean = false,
    val tapeBias: String = "Type II",
    val mixtapeCount: Int = 28,
    val hoursSpun: String = "342h",
    val downloadedTrackCount: Int = 42,
    val downloadedBytes: Long = 4_509_715_660L, // ~4.2 GB
    val totalStorageBytes: Long = 68_719_476_736L, // 64 GB
)

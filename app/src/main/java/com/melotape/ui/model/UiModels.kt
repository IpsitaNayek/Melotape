package com.melotape.ui.model

import androidx.compose.runtime.Immutable

/** Source badge displayed on song rows and detail sheets */
enum class SourceBadgeUi { Jamendo, Cloud, Device, Downloaded }

/** Download status for a song */
sealed interface DownloadStatus {
    data object NotDownloaded : DownloadStatus
    data object Queued : DownloadStatus
    data class Downloading(val progress: Float) : DownloadStatus  // 0f..1f
    data object Completed : DownloadStatus
    data object Failed : DownloadStatus
}

/**
 * Lightweight UI model for a song row / card.
 * Created in a ViewModel mapper — composables never see domain or data models.
 */
@Immutable
data class SongItemUi(
    val id: String,
    val title: String,
    val subtitle: String,          // "Artist • 3:52"
    val artwork: Any?,             // URL / content URI / null → placeholder
    val formatLabel: String?,      // "FLAC 24" | "MP3" | null
    val source: SourceBadgeUi?,
    val isLoved: Boolean = false,
    val download: DownloadStatus = DownloadStatus.NotDownloaded,
    val downloadAllowed: Boolean = false,
)

/**
 * UI model for a Sound Vault tile on the Search screen.
 */
@Immutable
data class VaultUi(
    val id: String,
    val title: String,
    val subtitle: String,
    val tag: String,               // Jamendo tag or "local"
    val isLocal: Boolean = false,
)

/**
 * UI model for the "Analog Specs & Master Chain" card in Now Playing.
 */
@Immutable
data class AnalogSpecsUi(
    val encoding: String,      // "FLAC 96/24" | "MP3 320" | etc.
    val headroom: String,      // "+3.2 dB VU" — real from format
    val warmth: String,        // tape bias label
    val dolbyActive: Boolean = false,
)

/**
 * UI model used in Profile header.
 */
@Immutable
data class UserProfileUi(
    val displayName: String,
    val handle: String?,
    val email: String?,
    val avatarUrl: Any?,
    val mixtapeCount: Int,
    val tapeBias: String,
    val hoursSpun: String,
    val downloadedTrackCount: Int,
    val downloadedSizeLabel: String,
    val totalStorageLabel: String,
    val storageUsedFraction: Float,
)

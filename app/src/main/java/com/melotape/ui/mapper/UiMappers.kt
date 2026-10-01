package com.melotape.ui.mapper

import com.melotape.domain.model.MusicSource
import com.melotape.domain.model.Song
import com.melotape.domain.model.UserProfile
import com.melotape.ui.model.DownloadStatus
import com.melotape.ui.model.SongItemUi
import com.melotape.ui.model.SourceBadgeUi
import com.melotape.ui.model.UserProfileUi
import java.util.Locale

fun Song.toItemUi(): SongItemUi {
    val durationMin = durationMs / 1000 / 60
    val durationSec = (durationMs / 1000) % 60
    val formattedDuration = String.format(Locale.US, "%d:%02d", durationMin, durationSec)

    val badge = when (source) {
        is MusicSource.Local -> SourceBadgeUi.Device
        is MusicSource.Jamendo -> SourceBadgeUi.Jamendo
        is MusicSource.Firebase -> SourceBadgeUi.Cloud
    }

    val format = when {
        mimeType?.contains("flac", ignoreCase = true) == true -> if ((bitrate ?: 0) > 900) "FLAC 24" else "FLAC"
        mimeType?.contains("wav", ignoreCase = true) == true -> "WAV"
        else -> "MP3"
    }

    return SongItemUi(
        id = id,
        title = title,
        subtitle = "$artist • $formattedDuration",
        artwork = artworkUri,
        formatLabel = format,
        source = badge,
        isLoved = isLoved,
        download = if (downloadAllowed) DownloadStatus.Completed else DownloadStatus.NotDownloaded,
        downloadAllowed = downloadAllowed,
    )
}

fun UserProfile.toProfileUi(): UserProfileUi {
    val downloadedGb = downloadedBytes.toFloat() / (1024 * 1024 * 1024)
    val totalGb = totalStorageBytes.toFloat() / (1024 * 1024 * 1024)
    val fraction = (downloadedBytes.toFloat() / totalStorageBytes.toFloat()).coerceIn(0f, 1f)

    return UserProfileUi(
        displayName = displayName,
        handle = handle,
        email = email,
        avatarUrl = avatarUrl,
        mixtapeCount = mixtapeCount,
        tapeBias = tapeBias,
        hoursSpun = hoursSpun,
        downloadedTrackCount = downloadedTrackCount,
        downloadedSizeLabel = String.format(Locale.US, "%.1f GB", downloadedGb),
        totalStorageLabel = String.format(Locale.US, "%.0f GB", totalGb),
        storageUsedFraction = fraction,
    )
}

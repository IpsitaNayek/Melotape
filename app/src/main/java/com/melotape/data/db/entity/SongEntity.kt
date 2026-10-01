package com.melotape.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.melotape.domain.model.MusicSource
import com.melotape.domain.model.Song

@Entity(tableName = "songs")
data class SongEntity(
    @PrimaryKey val id: String, // Source-aware ID: LOCAL:123, JAMENDO:track_456, FIREBASE:user_789
    val title: String,
    val artist: String,
    val album: String?,
    val artworkUri: String?,
    val durationMs: Long,
    val year: Int? = null,
    val genre: String? = null,
    val mimeType: String? = null,
    val fileSizeBytes: Long? = null,
    val bitrate: Int? = null,
    val sampleRate: Int? = null,
    val trackNumber: Int? = null,
    val downloadAllowed: Boolean = false,
    val sourceType: String, // "LOCAL", "JAMENDO", "FIREBASE"
    val sourceData: String, // contentUri, trackUrl, or storagePath
    val extra: String? = null, // downloadUrl or licenseUrl
    val cachedAt: Long = System.currentTimeMillis(),
)

fun SongEntity.toDomain(isLoved: Boolean = false): Song {
    val musicSource = when (sourceType) {
        "LOCAL" -> MusicSource.Local(sourceData)
        "JAMENDO" -> MusicSource.Jamendo(
            trackUrl = sourceData,
            downloadUrl = extra,
            audioDownloadAllowed = downloadAllowed,
            licenseUrl = null,
        )
        "FIREBASE" -> MusicSource.Firebase(sourceData)
        else -> MusicSource.Local(sourceData)
    }

    return Song(
        id = id,
        title = title,
        artist = artist,
        album = album,
        artworkUri = artworkUri,
        durationMs = durationMs,
        source = musicSource,
        year = year,
        genre = genre,
        mimeType = mimeType,
        fileSizeBytes = fileSizeBytes,
        bitrate = bitrate,
        sampleRate = sampleRate,
        trackNumber = trackNumber,
        isLoved = isLoved,
        downloadAllowed = downloadAllowed,
    )
}

fun Song.toEntity(): SongEntity {
    val (type, data, extraInfo) = when (val s = source) {
        is MusicSource.Local -> Triple("LOCAL", s.contentUri, null)
        is MusicSource.Jamendo -> Triple("JAMENDO", s.trackUrl, s.downloadUrl)
        is MusicSource.Firebase -> Triple("FIREBASE", s.storagePath, null)
    }

    return SongEntity(
        id = id,
        title = title,
        artist = artist,
        album = album,
        artworkUri = artworkUri?.toString(),
        durationMs = durationMs,
        year = year,
        genre = genre,
        mimeType = mimeType,
        fileSizeBytes = fileSizeBytes,
        bitrate = bitrate,
        sampleRate = sampleRate,
        trackNumber = trackNumber,
        downloadAllowed = downloadAllowed,
        sourceType = type,
        sourceData = data,
        extra = extraInfo,
    )
}

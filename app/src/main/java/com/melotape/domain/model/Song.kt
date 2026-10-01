package com.melotape.domain.model

/** Source-aware identifier prefix constants */
object SourcePrefix {
    const val LOCAL    = "LOCAL"
    const val JAMENDO  = "JAMENDO"
    const val FIREBASE = "FIREBASE"

    fun local(id: Long)    = "$LOCAL:$id"
    fun jamendo(id: String) = "$JAMENDO:$id"
    fun firebase(id: String) = "$FIREBASE:$id"
}

/** Where a song comes from */
sealed interface MusicSource {
    /** Audio file on the device via MediaStore */
    data class Local(val contentUri: String) : MusicSource

    /** Jamendo streaming / download */
    data class Jamendo(
        val trackUrl: String,
        val downloadUrl: String?,
        val audioDownloadAllowed: Boolean,
        val licenseUrl: String?,
    ) : MusicSource

    /** User's own upload on Firebase Storage */
    data class Firebase(val storagePath: String) : MusicSource
}

/** Core domain model for a song / track */
data class Song(
    val id: String,                  // "LOCAL:12345" | "JAMENDO:abc" | "FIREBASE:xyz"
    val title: String,
    val artist: String,
    val album: String?,
    val artworkUri: Any?,            // URL string, content URI, or null
    val durationMs: Long,
    val source: MusicSource,
    // Optional metadata
    val year: Int?      = null,
    val genre: String?  = null,
    val mimeType: String? = null,
    val fileSizeBytes: Long? = null,
    val bitrate: Int?   = null,
    val sampleRate: Int? = null,
    val trackNumber: Int? = null,
    val isLoved: Boolean = false,
    val downloadAllowed: Boolean = false,
)

/** A playlist (local or synced to cloud) */
data class Playlist(
    val id: String,
    val name: String,
    val description: String? = null,
    val coverArtUri: Any? = null,
    val songCount: Int = 0,
    val totalDurationMs: Long = 0L,
    val isPinned: Boolean = false,
    val cloudId: String? = null,
    val createdAt: Long = 0L,
    val updatedAt: Long = 0L,
)

package com.melotape.player.resolver

import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import com.melotape.domain.model.MusicSource
import com.melotape.domain.model.Song
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AudioSourceResolver @Inject constructor() {

    fun resolveUriString(song: Song): String {
        return when (val source = song.source) {
            is MusicSource.Local -> source.contentUri
            is MusicSource.Jamendo -> source.trackUrl
            is MusicSource.Firebase -> source.storagePath
        }
    }

    fun resolveUri(song: Song): Uri {
        return Uri.parse(resolveUriString(song))
    }

    fun toMediaItem(song: Song): MediaItem {
        val uri = resolveUri(song)
        val metadataBuilder = MediaMetadata.Builder()
            .setTitle(song.title)
            .setArtist(song.artist)
            .setAlbumTitle(song.album)

        val artString = song.artworkUri?.toString()
        if (!artString.isNullOrBlank()) {
            metadataBuilder.setArtworkUri(Uri.parse(artString))
        }

        return MediaItem.Builder()
            .setMediaId(song.id)
            .setUri(uri)
            .setMediaMetadata(metadataBuilder.build())
            .build()
    }
}

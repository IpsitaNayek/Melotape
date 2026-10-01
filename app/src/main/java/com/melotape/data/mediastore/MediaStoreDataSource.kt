package com.melotape.data.mediastore

import android.content.ContentUris
import android.content.Context
import android.database.ContentObserver
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import com.melotape.data.permission.MediaPermissionChecker
import com.melotape.di.IoDispatcher
import com.melotape.domain.model.MusicSource
import com.melotape.domain.model.Song
import com.melotape.domain.model.SourcePrefix
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MediaStoreDataSource @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val permissionChecker: MediaPermissionChecker,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) {

    private val rescanTrigger = MutableStateFlow(0L)

    private val projection = arrayOf(
        MediaStore.Audio.Media._ID,
        MediaStore.Audio.Media.TITLE,
        MediaStore.Audio.Media.ARTIST,
        MediaStore.Audio.Media.ALBUM,
        MediaStore.Audio.Media.ALBUM_ID,
        MediaStore.Audio.Media.DURATION,
        MediaStore.Audio.Media.SIZE,
        MediaStore.Audio.Media.DATE_ADDED,
        MediaStore.Audio.Media.DATE_MODIFIED,
        MediaStore.Audio.Media.MIME_TYPE,
        MediaStore.Audio.Media.YEAR,
        MediaStore.Audio.Media.TRACK,
        MediaStore.Audio.Media.DISPLAY_NAME,
    )

    /**
     * Reactive stream of local audio tracks.
     * Emits immediately, on ContentObserver changes, and on manual rescan.
     */
    val localSongsFlow: Flow<List<Song>> = callbackFlow {
        val observer = object : ContentObserver(Handler(Looper.getMainLooper())) {
            override fun onChange(selfChange: Boolean, uri: Uri?) {
                trySend(Unit)
            }
        }

        context.contentResolver.registerContentObserver(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            true,
            observer,
        )

        // Initial emission
        trySend(Unit)

        awaitClose {
            context.contentResolver.unregisterContentObserver(observer)
        }
    }
        .combine(rescanTrigger) { _, _ -> Unit }
        .map {
            withContext(ioDispatcher) {
                queryLocalAudio()
            }
        }
        .flowOn(ioDispatcher)

    fun forceRescan() {
        rescanTrigger.value = System.currentTimeMillis()
    }

    private fun queryLocalAudio(): List<Song> {
        if (!permissionChecker.hasPermission()) {
            return emptyList()
        }

        val songs = mutableListOf<Song>()
        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0 AND ${MediaStore.Audio.Media.DURATION} >= 30000"
        val sortOrder = "${MediaStore.Audio.Media.DATE_ADDED} DESC"

        try {
            val cursor: Cursor? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val queryArgs = Bundle().apply {
                    putString(android.content.ContentResolver.QUERY_ARG_SQL_SELECTION, selection)
                    putStringArray(android.content.ContentResolver.QUERY_ARG_SORT_COLUMNS, arrayOf(MediaStore.Audio.Media.DATE_ADDED))
                    putInt(android.content.ContentResolver.QUERY_ARG_SORT_DIRECTION, android.content.ContentResolver.QUERY_SORT_DIRECTION_DESCENDING)
                }
                context.contentResolver.query(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    projection,
                    queryArgs,
                    null,
                )
            } else {
                context.contentResolver.query(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    projection,
                    selection,
                    null,
                    sortOrder,
                )
            }

            cursor?.use {
                val idCol = it.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
                val titleCol = it.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
                val artistCol = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
                val albumCol = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
                val albumIdCol = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)
                val durationCol = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
                val sizeCol = it.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)
                val mimeCol = it.getColumnIndexOrThrow(MediaStore.Audio.Media.MIME_TYPE)
                val yearCol = it.getColumnIndexOrThrow(MediaStore.Audio.Media.YEAR)
                val trackCol = it.getColumnIndexOrThrow(MediaStore.Audio.Media.TRACK)

                while (it.moveToNext()) {
                    val id = it.getLong(idCol)
                    val title = it.getString(titleCol) ?: "Unknown Tape"
                    val artist = it.getString(artistCol) ?: "Unknown Artist"
                    val album = it.getString(albumCol)
                    val albumId = it.getLong(albumIdCol)
                    val durationMs = it.getLong(durationCol)
                    val fileSizeBytes = it.getLong(sizeCol)
                    val mimeType = it.getString(mimeCol)
                    val year = it.getInt(yearCol)
                    val trackNumber = it.getInt(trackCol)

                    val contentUri = ContentUris.withAppendedId(
                        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        id,
                    ).toString()

                    val artworkUri = ContentUris.withAppendedId(
                        Uri.parse("content://media/external/audio/albumart"),
                        albumId,
                    ).toString()

                    val bitrate = if (mimeType?.contains("flac", ignoreCase = true) == true) 960 else 320

                    songs.add(
                        Song(
                            id = SourcePrefix.local(id),
                            title = title,
                            artist = if (artist == "<unknown>") "Unknown Artist" else artist,
                            album = album,
                            artworkUri = artworkUri,
                            durationMs = durationMs,
                            source = MusicSource.Local(contentUri),
                            year = if (year > 0) year else null,
                            mimeType = mimeType,
                            fileSizeBytes = fileSizeBytes,
                            bitrate = bitrate,
                            sampleRate = if (bitrate > 900) 96000 else 44100,
                            trackNumber = if (trackNumber > 0) trackNumber else null,
                            isLoved = false,
                            downloadAllowed = true, // already local on device
                        )
                    )
                }
            }
        } catch (e: SecurityException) {
            // Permission revoked at runtime
            return emptyList()
        } catch (e: Exception) {
            // General query error fallback
            return emptyList()
        }

        return songs
    }
}

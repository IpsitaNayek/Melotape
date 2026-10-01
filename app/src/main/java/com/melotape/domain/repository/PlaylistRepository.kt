package com.melotape.domain.repository

import com.melotape.domain.model.Playlist
import com.melotape.domain.model.Song
import kotlinx.coroutines.flow.Flow

/**
 * Playlist repository for managing local, pinned, and cloud mixtapes.
 */
interface PlaylistRepository {
    fun getPlaylists(): Flow<List<Playlist>>
    fun getPlaylistById(id: String): Flow<Playlist?>
    fun getPlaylistSongs(playlistId: String): Flow<List<Song>>
    suspend fun createPlaylist(name: String, description: String?): String
    suspend fun deletePlaylist(playlistId: String)
    suspend fun addSongToPlaylist(playlistId: String, songId: String)
    suspend fun removeSongFromPlaylist(playlistId: String, songId: String)
    suspend fun reorderPlaylist(playlistId: String, songIdsInOrder: List<String>)
    suspend fun renamePlaylist(playlistId: String, name: String, description: String?)
}


package com.melotape.domain.repository

import com.melotape.domain.model.Song
import kotlinx.coroutines.flow.Flow

/**
 * Primary music repository interface for fetching songs across local, Jamendo, and cloud sources.
 */
interface MusicRepository {
    fun getRecentlyPlayed(): Flow<List<Song>>
    fun getDownloadedSongs(): Flow<List<Song>>
    fun getLocalSongs(): Flow<List<Song>>
    fun getTrendingSongs(): Flow<List<Song>>
    fun getSongById(id: String): Flow<Song?>
    fun searchSongs(query: String): Flow<List<Song>>
    fun getLovedSongs(): Flow<List<Song>>
    suspend fun toggleLoved(songId: String)
}

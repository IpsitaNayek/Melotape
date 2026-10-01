package com.melotape.data.db.dao

import androidx.room.*
import com.melotape.data.db.entity.SongEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SongDao {

    @Upsert
    suspend fun upsert(song: SongEntity)

    @Upsert
    suspend fun upsertAll(songs: List<SongEntity>)

    @Query("SELECT * FROM songs WHERE id = :id")
    fun getSongById(id: String): Flow<SongEntity?>

    @Query("SELECT * FROM songs WHERE id = :id")
    suspend fun getSongByIdSync(id: String): SongEntity?

    @Query("SELECT * FROM songs WHERE id IN (:ids)")
    fun getSongsByIds(ids: List<String>): Flow<List<SongEntity>>

    @Query("SELECT * FROM songs WHERE title LIKE '%' || :query || '%' OR artist LIKE '%' || :query || '%' OR album LIKE '%' || :query || '%'")
    fun searchSongs(query: String): Flow<List<SongEntity>>

    @Query("DELETE FROM songs WHERE id = :id")
    suspend fun deleteSong(id: String)

    @Query("DELETE FROM songs WHERE id NOT IN (SELECT songId FROM favorites) AND id NOT IN (SELECT songId FROM playlist_songs) AND id NOT IN (SELECT id FROM downloads)")
    suspend fun deleteOrphanSongs()
}

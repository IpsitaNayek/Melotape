package com.melotape.data.db.dao

import androidx.room.*
import com.melotape.data.db.entity.FavoriteEntity
import com.melotape.data.db.entity.SongEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {

    @Query("SELECT * FROM favorites ORDER BY favoritedAt DESC")
    fun getAllFavorites(): Flow<List<FavoriteEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE songId = :songId)")
    fun isFavorite(songId: String): Flow<Boolean>

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE songId = :songId)")
    suspend fun isFavoriteSync(songId: String): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(favorite: FavoriteEntity)

    @Query("DELETE FROM favorites WHERE songId = :songId")
    suspend fun delete(songId: String)

    @Transaction
    suspend fun toggleFavorite(songId: String) {
        if (isFavoriteSync(songId)) {
            delete(songId)
        } else {
            insert(FavoriteEntity(songId = songId))
        }
    }

    @Query("""
        SELECT s.* FROM songs s 
        INNER JOIN favorites f ON s.id = f.songId 
        ORDER BY f.favoritedAt DESC
    """)
    fun getFavoriteSongs(): Flow<List<SongEntity>>

    @Query("SELECT COUNT(*) FROM favorites")
    fun getFavoriteCount(): Flow<Int>
}

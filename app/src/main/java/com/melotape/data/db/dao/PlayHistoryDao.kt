package com.melotape.data.db.dao

import androidx.room.*
import com.melotape.data.db.entity.PlayHistoryEntity
import com.melotape.data.db.entity.SongEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlayHistoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(record: PlayHistoryEntity): Long

    @Query("UPDATE play_history SET listenedMs = listenedMs + :extraMs WHERE id = :id")
    suspend fun addListenedTime(id: Long, extraMs: Long)

    @Query("""
        SELECT s.* FROM songs s 
        INNER JOIN (
            SELECT songId, MAX(playedAt) as lastPlayed 
            FROM play_history 
            GROUP BY songId 
            ORDER BY lastPlayed DESC 
            LIMIT :limit
        ) h ON s.id = h.songId
        ORDER BY h.lastPlayed DESC
    """)
    fun getRecentlyPlayedSongs(limit: Int = 20): Flow<List<SongEntity>>

    @Query("SELECT COALESCE(SUM(listenedMs), 0) FROM play_history")
    fun getTotalListenedMs(): Flow<Long>

    @Query("SELECT COUNT(id) FROM play_history WHERE playedAt >= :sinceTimestamp")
    fun getPlayCountSince(sinceTimestamp: Long): Flow<Int>

    @Query("DELETE FROM play_history WHERE playedAt < :beforeTimestamp")
    suspend fun clearOldHistory(beforeTimestamp: Long)
}

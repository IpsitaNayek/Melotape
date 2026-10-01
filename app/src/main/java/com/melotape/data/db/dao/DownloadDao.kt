package com.melotape.data.db.dao

import androidx.room.*
import com.melotape.data.db.entity.DownloadEntity
import com.melotape.data.db.entity.SongEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DownloadDao {

    @Query("SELECT * FROM downloads ORDER BY downloadedAt DESC")
    fun getAllDownloads(): Flow<List<DownloadEntity>>

    @Query("SELECT * FROM downloads WHERE id = :songId")
    fun getDownloadById(songId: String): Flow<DownloadEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertDownload(download: DownloadEntity)

    @Query("UPDATE downloads SET progress = :progress, status = :status WHERE id = :songId")
    suspend fun updateProgress(songId: String, progress: Int, status: String)

    @Query("DELETE FROM downloads WHERE id = :songId")
    suspend fun deleteDownload(songId: String)

    @Query("""
        SELECT s.* FROM songs s 
        INNER JOIN downloads d ON s.id = d.id 
        WHERE d.status = 'COMPLETED' 
        ORDER BY d.downloadedAt DESC
    """)
    fun getCompletedDownloadedSongs(): Flow<List<SongEntity>>

    @Query("SELECT COALESCE(SUM(fileSize), 0) FROM downloads WHERE status = 'COMPLETED'")
    fun getTotalDownloadedBytes(): Flow<Long>

    @Query("SELECT COUNT(*) FROM downloads WHERE status = 'COMPLETED'")
    fun getCompletedDownloadCount(): Flow<Int>
}

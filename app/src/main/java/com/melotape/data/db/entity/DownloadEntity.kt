package com.melotape.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.melotape.ui.model.DownloadStatus

@Entity(tableName = "downloads")
data class DownloadEntity(
    @PrimaryKey val id: String, // songId
    val progress: Int = 0, // 0..100
    val status: String = "QUEUED", // "QUEUED", "DOWNLOADING", "COMPLETED", "FAILED", "PAUSED"
    val fileSize: Long = 0L,
    val localFilePath: String? = null,
    val mediaStoreId: Long? = null,
    val originSourceId: String, // original source prefix or URL
    val downloadedAt: Long? = null,
)

fun DownloadEntity.toUiStatus(): DownloadStatus {
    return when (status) {
        "COMPLETED" -> DownloadStatus.Completed
        "DOWNLOADING" -> DownloadStatus.Downloading(progress / 100f)
        "QUEUED" -> DownloadStatus.Queued
        "FAILED" -> DownloadStatus.Failed
        else -> DownloadStatus.NotDownloaded
    }
}

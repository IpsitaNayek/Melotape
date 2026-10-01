package com.melotape.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.melotape.data.db.dao.*
import com.melotape.data.db.entity.*

@Database(
    entities = [
        SongEntity::class,
        FavoriteEntity::class,
        PlayHistoryEntity::class,
        PlaylistEntity::class,
        PlaylistSongEntity::class,
        DownloadEntity::class,
    ],
    version = 1,
    exportSchema = true,
)
abstract class MelotapeDatabase : RoomDatabase() {

    abstract fun songDao(): SongDao
    abstract fun favoriteDao(): FavoriteDao
    abstract fun playHistoryDao(): PlayHistoryDao
    abstract fun playlistDao(): PlaylistDao
    abstract fun downloadDao(): DownloadDao

    companion object {
        const val DATABASE_NAME = "melotape_tape_vault.db"
    }
}

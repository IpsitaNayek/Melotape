package com.melotape.di

import android.content.Context
import androidx.room.Room
import com.melotape.data.db.MelotapeDatabase
import com.melotape.data.db.dao.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideMelotapeDatabase(
        @ApplicationContext context: Context,
    ): MelotapeDatabase {
        return Room.databaseBuilder(
            context,
            MelotapeDatabase::class.java,
            MelotapeDatabase.DATABASE_NAME
        )
            .fallbackToDestructiveMigration(true)
            .build()
    }

    @Provides
    fun provideSongDao(database: MelotapeDatabase): SongDao = database.songDao()

    @Provides
    fun provideFavoriteDao(database: MelotapeDatabase): FavoriteDao = database.favoriteDao()

    @Provides
    fun providePlayHistoryDao(database: MelotapeDatabase): PlayHistoryDao = database.playHistoryDao()

    @Provides
    fun providePlaylistDao(database: MelotapeDatabase): PlaylistDao = database.playlistDao()

    @Provides
    fun provideDownloadDao(database: MelotapeDatabase): DownloadDao = database.downloadDao()
}

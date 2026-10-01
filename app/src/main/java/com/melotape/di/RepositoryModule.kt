package com.melotape.di

import com.melotape.data.fake.FakeMusicRepository
import com.melotape.data.fake.FakePlaylistRepository
import com.melotape.data.fake.FakeUserRepository
import com.melotape.domain.repository.MusicRepository
import com.melotape.domain.repository.PlaylistRepository
import com.melotape.domain.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindMusicRepository(impl: com.melotape.data.repository.MusicRepositoryImpl): MusicRepository

    @Binds
    @Singleton
    abstract fun bindPlaylistRepository(impl: com.melotape.data.repository.PlaylistRepositoryImpl): PlaylistRepository

    @Binds
    @Singleton
    abstract fun bindUserRepository(impl: FakeUserRepository): UserRepository
}

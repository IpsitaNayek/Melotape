package com.melotape.di

import com.melotape.player.controller.PlayerController
import com.melotape.player.controller.PlayerControllerImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class PlayerModule {

    @Binds
    @Singleton
    abstract fun bindPlayerController(impl: PlayerControllerImpl): PlayerController
}

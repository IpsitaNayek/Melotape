package com.melotape.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    // Populated in Phase 8 (OkHttpClient, Retrofit, JamendoApi)
}

package com.melotape.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {
    // Populated in Phase 10 / 11 (FirebaseAuth, FirebaseFirestore, FirebaseStorage)
}

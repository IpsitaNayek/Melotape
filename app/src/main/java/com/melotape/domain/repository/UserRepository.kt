package com.melotape.domain.repository

import com.melotape.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun getUserProfile(): Flow<UserProfile>
    suspend fun updateDeckConfig(dolbyEnabled: Boolean, hissFilter: Boolean, tapeBias: String)
}

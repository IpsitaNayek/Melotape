package com.melotape.data.fake

import com.melotape.domain.model.UserProfile
import com.melotape.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FakeUserRepository @Inject constructor() : UserRepository {

    private val userProfileFlow = MutableStateFlow(
        UserProfile(
            id = "user_alex",
            displayName = "Alex",
            handle = "@alex_analog",
            email = "alex@melotape.audio",
            avatarUrl = null,
            isProDeck = true,
            dolbyBActive = true,
            hissFilterActive = false,
            tapeBias = "Type II • Chrome",
            mixtapeCount = 28,
            hoursSpun = "342h",
            downloadedTrackCount = 42,
            downloadedBytes = 4_509_715_660L,
            totalStorageBytes = 68_719_476_736L,
        )
    )

    override fun getUserProfile(): Flow<UserProfile> = userProfileFlow

    override suspend fun updateDeckConfig(dolbyEnabled: Boolean, hissFilter: Boolean, tapeBias: String) {
        val current = userProfileFlow.value
        userProfileFlow.value = current.copy(
            dolbyBActive = dolbyEnabled,
            hissFilterActive = hissFilter,
            tapeBias = tapeBias,
        )
    }
}

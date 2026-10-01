package com.melotape.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.melotape.domain.repository.UserRepository
import com.melotape.ui.mapper.toProfileUi
import com.melotape.ui.model.UserProfileUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileUiState(
    val user: UserProfileUi = UserProfileUi(
        displayName = "Alex",
        handle = "@alex_analog",
        email = "alex@melotape.audio",
        avatarUrl = null,
        mixtapeCount = 28,
        tapeBias = "Type II",
        hoursSpun = "342h",
        downloadedTrackCount = 42,
        downloadedSizeLabel = "4.2 GB",
        totalStorageLabel = "64 GB",
        storageUsedFraction = 0.065f,
    ),
    val dolbyBEnabled: Boolean = true,
    val hissFilterEnabled: Boolean = false,
    val audioQuality: String = "Master FLAC (24-bit/96kHz)",
    val tapeBiasPreset: String = "Type II • Chrome Bias",
    val deckAesthetic: String = "Warm Retro Slate",
    val notificationsEnabled: Boolean = true,
    val versionName: String = "Melotape v2.4.0 (Firmware Rev. 42)",
    val messageBanner: String? = null,
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    private fun loadProfile() {
        viewModelScope.launch {
            userRepository.getUserProfile().collect { domainUser ->
                _uiState.update { current ->
                    current.copy(
                        user = domainUser.toProfileUi(),
                        dolbyBEnabled = domainUser.dolbyBActive,
                        hissFilterEnabled = domainUser.hissFilterActive,
                    )
                }
            }
        }
    }

    fun onToggleDolby(enabled: Boolean) {
        _uiState.update { it.copy(dolbyBEnabled = enabled) }
        viewModelScope.launch {
            userRepository.updateDeckConfig(
                dolbyEnabled = enabled,
                hissFilter = _uiState.value.hissFilterEnabled,
                tapeBias = _uiState.value.user.tapeBias,
            )
        }
    }

    fun onToggleHissFilter(enabled: Boolean) {
        _uiState.update { it.copy(hissFilterEnabled = enabled) }
        viewModelScope.launch {
            userRepository.updateDeckConfig(
                dolbyEnabled = _uiState.value.dolbyBEnabled,
                hissFilter = enabled,
                tapeBias = _uiState.value.user.tapeBias,
            )
        }
    }

    fun onToggleNotifications(enabled: Boolean) {
        _uiState.update { it.copy(notificationsEnabled = enabled) }
    }

    fun onClearCache() {
        _uiState.update { it.copy(messageBanner = "Cassette buffer & image cache cleared.") }
    }

    fun onDismissBanner() {
        _uiState.update { it.copy(messageBanner = null) }
    }
}

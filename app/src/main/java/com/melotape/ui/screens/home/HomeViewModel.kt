package com.melotape.ui.screens.home

import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.melotape.data.permission.MediaPermissionChecker
import com.melotape.data.repository.MusicRepositoryImpl
import com.melotape.domain.model.Playlist
import com.melotape.domain.repository.MusicRepository
import com.melotape.domain.repository.PlaylistRepository
import com.melotape.domain.repository.UserRepository
import com.melotape.ui.mapper.toItemUi
import com.melotape.ui.model.SongItemUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val greeting: String = "Good evening, Alex",
    val statusLine: String = "● SPINNING SIDE A • DECK CALIBRATED",
    val tapeCounter: String = "042",
    val featuredMixtape: Playlist? = null,
    val recentlyPlayed: List<SongItemUi> = emptyList(),
    val downloadedSongs: List<SongItemUi> = emptyList(),
    val pocketMixtape: List<SongItemUi> = emptyList(),
    val isPlaying: Boolean = true,
    val hasAudioPermission: Boolean = false,
    val isLoading: Boolean = false,
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val musicRepository: MusicRepository,
    private val playlistRepository: PlaylistRepository,
    private val userRepository: UserRepository,
    private val permissionChecker: MediaPermissionChecker,
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        HomeUiState(
            hasAudioPermission = permissionChecker.hasPermission(),
            isLoading = true,
        )
    )
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    val requiredPermission: String
        get() = permissionChecker.requiredPermission

    init {
        loadHomeData()
    }

    fun onPermissionResult(isGranted: Boolean) {
        _uiState.update { it.copy(hasAudioPermission = isGranted) }
        if (isGranted) {
            onRescanLocal()
        }
    }

    fun createAppSettingsIntent(): Intent {
        return permissionChecker.createAppSettingsIntent()
    }

    private fun loadHomeData() {
        viewModelScope.launch {
            combine(
                musicRepository.getRecentlyPlayed(),
                musicRepository.getDownloadedSongs(),
                musicRepository.getLocalSongs(),
                playlistRepository.getPlaylists(),
            ) { recent, downloaded, local, playlists ->
                HomeUiState(
                    greeting = "Good evening, Alex",
                    statusLine = "● SPINNING SIDE A • DECK CALIBRATED",
                    tapeCounter = "042",
                    featuredMixtape = playlists.find { it.isPinned } ?: playlists.firstOrNull(),
                    recentlyPlayed = recent.map { it.toItemUi() },
                    downloadedSongs = downloaded.map { it.toItemUi() },
                    pocketMixtape = local.map { it.toItemUi() },
                    isPlaying = true,
                    hasAudioPermission = permissionChecker.hasPermission(),
                    isLoading = false,
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    fun onPlayPause() {
        _uiState.update { it.copy(isPlaying = !it.isPlaying) }
    }

    fun onResetTapeCounter() {
        _uiState.update { it.copy(tapeCounter = "000") }
    }

    fun onToggleLoved(songId: String) {
        viewModelScope.launch {
            musicRepository.toggleLoved(songId)
        }
    }

    fun onRescanLocal() {
        _uiState.update { it.copy(statusLine = "● RESCANNING LOCAL TRACKS...") }
        (musicRepository as? MusicRepositoryImpl)?.forceRescanLocal()
        viewModelScope.launch {
            kotlinx.coroutines.delay(800)
            _uiState.update { it.copy(statusLine = "● LOCAL TRACKS INDEXED • DECK READY") }
        }
    }
}

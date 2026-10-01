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
import com.melotape.player.controller.PlayerController
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
    val isPlaying: Boolean = false,
    val hasAudioPermission: Boolean = false,
    val isLoading: Boolean = false,
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val musicRepository: MusicRepository,
    private val playlistRepository: PlaylistRepository,
    private val userRepository: UserRepository,
    private val permissionChecker: MediaPermissionChecker,
    private val playerController: PlayerController,
) : ViewModel() {

    private val tapeCounterState = MutableStateFlow("042")

    private data class RepositorySnapshot(
        val recent: List<SongItemUi> = emptyList(),
        val downloaded: List<SongItemUi> = emptyList(),
        val local: List<SongItemUi> = emptyList(),
        val featuredPlaylist: Playlist? = null,
    )

    private val repositoryDataFlow: Flow<RepositorySnapshot> = combine(
        musicRepository.getRecentlyPlayed(),
        musicRepository.getDownloadedSongs(),
        musicRepository.getLocalSongs(),
        playlistRepository.getPlaylists(),
    ) { recent, downloaded, local, playlists ->
        RepositorySnapshot(
            recent = recent.map { it.toItemUi() },
            downloaded = downloaded.map { it.toItemUi() },
            local = local.map { it.toItemUi() },
            featuredPlaylist = playlists.find { it.isPinned } ?: playlists.firstOrNull(),
        )
    }

    val uiState: StateFlow<HomeUiState> = combine(
        repositoryDataFlow,
        playerController.playbackState,
        tapeCounterState,
    ) { repo, playback, counter ->
        val isPlaying = playback.isPlaying
        val status = if (isPlaying) {
            "● SPINNING SIDE A • DECK CALIBRATED"
        } else if (playback.currentSong != null) {
            "○ DECK IDLE • TAPE LOADED"
        } else {
            "○ DECK STANDBY • CALIBRATED"
        }

        HomeUiState(
            greeting = "Good evening, Alex",
            statusLine = status,
            tapeCounter = counter,
            featuredMixtape = repo.featuredPlaylist,
            recentlyPlayed = repo.recent,
            downloadedSongs = repo.downloaded,
            pocketMixtape = repo.local,
            isPlaying = isPlaying,
            hasAudioPermission = permissionChecker.hasPermission(),
            isLoading = false,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState(
            hasAudioPermission = permissionChecker.hasPermission(),
            isLoading = true,
        ),
    )

    val requiredPermission: String
        get() = permissionChecker.requiredPermission

    fun onPermissionResult(isGranted: Boolean) {
        if (isGranted) {
            onRescanLocal()
        }
    }

    fun createAppSettingsIntent(): Intent {
        return permissionChecker.createAppSettingsIntent()
    }

    fun onPlayPause() {
        playerController.togglePlayPause()
    }

    fun onRewind() {
        playerController.seekRelative(-10_000L)
    }

    fun onFastForward() {
        playerController.seekRelative(10_000L)
    }

    fun onStop() {
        playerController.stop()
    }

    fun onResetTapeCounter() {
        tapeCounterState.value = "000"
    }

    fun onToggleLoved(songId: String) {
        viewModelScope.launch {
            musicRepository.toggleLoved(songId)
        }
    }

    fun onPlaySong(songItem: SongItemUi) {
        viewModelScope.launch {
            musicRepository.getSongById(songItem.id).firstOrNull()?.let { song ->
                playerController.setQueue(listOf(song), startIndex = 0, playWhenReady = true)
            }
        }
    }

    fun onPlayFeatured() {
        val featured = uiState.value.featuredMixtape ?: return
        viewModelScope.launch {
            playlistRepository.getPlaylistSongs(featured.id).firstOrNull()?.let { songs ->
                if (songs.isNotEmpty()) {
                    playerController.setQueue(songs, startIndex = 0, playWhenReady = true)
                }
            }
        }
    }


    fun onRescanLocal() {
        (musicRepository as? MusicRepositoryImpl)?.forceRescanLocal()
    }
}

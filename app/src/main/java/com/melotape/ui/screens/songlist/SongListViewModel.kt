package com.melotape.ui.screens.songlist

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.melotape.domain.repository.MusicRepository
import com.melotape.domain.repository.PlaylistRepository
import com.melotape.ui.mapper.toItemUi
import com.melotape.ui.model.SongItemUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SongListUiState(
    val title: String = "Vintage Masters C-90",
    val sideLabel: String = "SIDE A",
    val tapeType: String = "TYPE I • NORMAL BIAS",
    val trackCountInfo: String = "18 Tracks • 1h 14m",
    val progressText: String = "03:52 / 44:20",
    val songs: List<SongItemUi> = emptyList(),
    val currentSongId: String? = null,
    val isPlaying: Boolean = true,
    val isFlippedToSideB: Boolean = false,
    val isLoading: Boolean = false,
)

@HiltViewModel
class SongListViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val musicRepository: MusicRepository,
    private val playlistRepository: PlaylistRepository,
) : ViewModel() {

    private val sourceType: String = savedStateHandle.get<String>("sourceType") ?: "recent"
    private val sourceId: String = savedStateHandle.get<String>("sourceId") ?: ""

    private val _uiState = MutableStateFlow(SongListUiState(isLoading = true))
    val uiState: StateFlow<SongListUiState> = _uiState.asStateFlow()

    init {
        loadSongs()
    }

    private fun loadSongs() {
        viewModelScope.launch {
            val songsFlow = when (sourceType) {
                "local" -> musicRepository.getLocalSongs()
                "downloaded" -> musicRepository.getDownloadedSongs()
                "trending" -> musicRepository.getTrendingSongs()
                else -> musicRepository.getRecentlyPlayed()
            }

            songsFlow.collect { list ->
                val uiSongs = list.map { it.toItemUi() }
                _uiState.update { current ->
                    current.copy(
                        title = when (sourceType) {
                            "local" -> "Pocket Mixtape (On Device)"
                            "downloaded" -> "Downloaded Cassettes"
                            "trending" -> "Trending Tape Reissues"
                            else -> "Vintage Masters C-90"
                        },
                        songs = uiSongs,
                        currentSongId = uiSongs.firstOrNull()?.id,
                        isLoading = false,
                    )
                }
            }
        }
    }

    fun onPlayAll() {
        _uiState.update { it.copy(isPlaying = true) }
    }

    fun onShuffle() {
        _uiState.update { it.copy(songs = it.songs.shuffled(), isPlaying = true) }
    }

    fun onSongClick(song: SongItemUi) {
        _uiState.update {
            it.copy(
                currentSongId = song.id,
                isPlaying = if (it.currentSongId == song.id) !it.isPlaying else true,
            )
        }
    }

    fun onFlipSide() {
        _uiState.update {
            val nextSideB = !it.isFlippedToSideB
            it.copy(
                isFlippedToSideB = nextSideB,
                sideLabel = if (nextSideB) "SIDE B" else "SIDE A",
                progressText = if (nextSideB) "00:00 / 45:40" else "03:52 / 44:20",
            )
        }
    }

    fun onToggleLoved(songId: String) {
        viewModelScope.launch {
            musicRepository.toggleLoved(songId)
        }
    }
}

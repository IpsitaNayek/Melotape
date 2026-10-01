package com.melotape.ui.screens.loved

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.melotape.domain.model.Playlist
import com.melotape.domain.repository.MusicRepository
import com.melotape.domain.repository.PlaylistRepository
import com.melotape.ui.mapper.toItemUi
import com.melotape.ui.model.SongItemUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LovedUiState(
    val lovedSongs: List<SongItemUi> = emptyList(),
    val playlists: List<Playlist> = emptyList(),
    val showCreateSheet: Boolean = false,
    val isLoading: Boolean = false,
)

@HiltViewModel
class LovedViewModel @Inject constructor(
    private val musicRepository: MusicRepository,
    private val playlistRepository: PlaylistRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(LovedUiState(isLoading = true))
    val uiState: StateFlow<LovedUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            combine(
                musicRepository.getLovedSongs(),
                playlistRepository.getPlaylists(),
            ) { loved, playlists ->
                LovedUiState(
                    lovedSongs = loved.map { it.toItemUi() },
                    playlists = playlists,
                    isLoading = false,
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    fun onToggleLoved(songId: String) {
        viewModelScope.launch {
            musicRepository.toggleLoved(songId)
        }
    }

    fun onToggleCreateSheet(show: Boolean) {
        _uiState.update { it.copy(showCreateSheet = show) }
    }

    fun onCreatePlaylist(name: String, description: String?) {
        viewModelScope.launch {
            playlistRepository.createPlaylist(name, description)
        }
    }
}

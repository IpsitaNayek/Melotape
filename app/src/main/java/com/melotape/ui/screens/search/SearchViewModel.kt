package com.melotape.ui.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.melotape.domain.repository.MusicRepository
import com.melotape.ui.mapper.toItemUi
import com.melotape.ui.model.SongItemUi
import com.melotape.ui.model.VaultUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SearchUiState(
    val query: String = "",
    val recents: List<String> = listOf("City Pop 1984", "Otari MX50", "Ambient Lo-Fi", "Beach House", "MGMT"),
    val soundVaults: List<VaultUi> = listOf(
        VaultUi("v1", "Lo-Fi Tapes", "Otari MX50 saturation", "lofi"),
        VaultUi("v2", "Synthwave 80s", "Type II High Bias", "synthwave"),
        VaultUi("v3", "City Pop", "Tokyo 1984 masters", "citypop"),
        VaultUi("v4", "Analog Ambient", "Slow tape loop echoes", "ambient"),
        VaultUi("v5", "Vintage Masters", "C-90 Stereo Archives", "vintage"),
        VaultUi("v6", "Local Tapes", "On Device Flash", "local", isLocal = true),
    ),
    val trendingSongs: List<SongItemUi> = emptyList(),
    val searchResults: List<SongItemUi> = emptyList(),
    val isSearching: Boolean = false,
)

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val musicRepository: MusicRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    init {
        loadTrending()
    }

    private fun loadTrending() {
        viewModelScope.launch {
            musicRepository.getTrendingSongs().collect { songs ->
                _uiState.update { it.copy(trendingSongs = songs.map { song -> song.toItemUi() }) }
            }
        }
    }

    fun onQueryChange(newQuery: String) {
        _uiState.update { it.copy(query = newQuery, isSearching = newQuery.isNotBlank()) }
        if (newQuery.isNotBlank()) {
            viewModelScope.launch {
                musicRepository.searchSongs(newQuery).collect { results ->
                    _uiState.update { it.copy(searchResults = results.map { it.toItemUi() }) }
                }
            }
        } else {
            _uiState.update { it.copy(searchResults = emptyList()) }
        }
    }

    fun onClearQuery() {
        onQueryChange("")
    }

    fun onClearRecents() {
        _uiState.update { it.copy(recents = emptyList()) }
    }

    fun onToggleLoved(songId: String) {
        viewModelScope.launch {
            musicRepository.toggleLoved(songId)
        }
    }
}

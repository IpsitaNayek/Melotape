package com.melotape.ui.screens.nowplaying

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.melotape.domain.repository.MusicRepository
import com.melotape.ui.mapper.toItemUi
import com.melotape.ui.model.AnalogSpecsUi
import com.melotape.ui.model.SongItemUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class NowPlayingUiState(
    val currentSong: SongItemUi? = null,
    val isPlaying: Boolean = true,
    val progress: Float = 0.42f,
    val elapsedText: String = "01:42",
    val remainingText: String = "-02:10",
    val speedText: String = "4.76 cm/s",
    val sideChip: String = "SIDE A • C-60",
    val specs: AnalogSpecsUi = AnalogSpecsUi(
        encoding = "FLAC 96/24",
        headroom = "+3.2 dB VU",
        warmth = "Type II Bias",
        dolbyActive = true,
    ),
    val volume: Float = 0.75f,
    val isShuffle: Boolean = false,
    val isRepeat: Boolean = false,
    val showDetailsSheet: Boolean = false,
)

@HiltViewModel
class NowPlayingViewModel @Inject constructor(
    private val musicRepository: MusicRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(NowPlayingUiState())
    val uiState: StateFlow<NowPlayingUiState> = _uiState.asStateFlow()

    init {
        loadCurrentSong()
    }

    private fun loadCurrentSong() {
        viewModelScope.launch {
            musicRepository.getRecentlyPlayed().collect { songs ->
                val first = songs.firstOrNull()?.toItemUi()
                _uiState.update { current ->
                    current.copy(
                        currentSong = first,
                        specs = AnalogSpecsUi(
                            encoding = first?.formatLabel ?: "FLAC 24",
                            headroom = "+3.2 dB VU",
                            warmth = "Type II Bias",
                            dolbyActive = true,
                        ),
                    )
                }
            }
        }
    }

    fun onPlayPause() {
        _uiState.update { it.copy(isPlaying = !it.isPlaying) }
    }

    fun onSeek(newProgress: Float) {
        val totalSec = 232 // fake duration in sec
        val elapsed = (totalSec * newProgress).toInt()
        val remaining = totalSec - elapsed
        _uiState.update {
            it.copy(
                progress = newProgress,
                elapsedText = String.format("%02d:%02d", elapsed / 60, elapsed % 60),
                remainingText = String.format("-%02d:%02d", remaining / 60, remaining % 60),
            )
        }
    }

    fun onToggleLoved() {
        val song = _uiState.value.currentSong ?: return
        viewModelScope.launch {
            musicRepository.toggleLoved(song.id)
            _uiState.update {
                it.copy(currentSong = it.currentSong?.copy(isLoved = !song.isLoved))
            }
        }
    }

    fun onNext() {
        // Next song in queue
        onSeek(0f)
    }

    fun onPrevious() {
        onSeek(0f)
    }

    fun onToggleShuffle() {
        _uiState.update { it.copy(isShuffle = !it.isShuffle) }
    }

    fun onToggleRepeat() {
        _uiState.update { it.copy(isRepeat = !it.isRepeat) }
    }

    fun onSetVolume(newVolume: Float) {
        _uiState.update { it.copy(volume = newVolume) }
    }

    fun onToggleDetailsSheet(show: Boolean) {
        _uiState.update { it.copy(showDetailsSheet = show) }
    }
}

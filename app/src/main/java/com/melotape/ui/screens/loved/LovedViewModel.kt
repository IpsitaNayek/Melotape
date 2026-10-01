package com.melotape.ui.screens.loved

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.melotape.domain.model.Playlist
import com.melotape.domain.model.Song
import com.melotape.domain.repository.MusicRepository
import com.melotape.domain.repository.PlaylistRepository
import com.melotape.domain.usecase.*
import com.melotape.player.controller.PlayerController
import com.melotape.ui.mapper.toItemUi
import com.melotape.ui.mapper.toUi
import com.melotape.ui.model.PlaylistUi
import com.melotape.ui.model.SongItemUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LovedUiState(
    val lovedSongs: List<SongItemUi> = emptyList(),
    val rawLovedSongs: List<Song> = emptyList(),
    val playlists: List<PlaylistUi> = emptyList(),
    val lovedDeckTotalDuration: String = "0m",
    val showCreateSheet: Boolean = false,
    val playlistToRename: PlaylistUi? = null,
    val playlistToDelete: PlaylistUi? = null,
    val isPlayingLovedDeck: Boolean = false,
    val isLoading: Boolean = false,
)

@HiltViewModel
class LovedViewModel @Inject constructor(
    private val musicRepository: MusicRepository,
    private val playlistRepository: PlaylistRepository,
    private val playerController: PlayerController,
    private val createPlaylistUseCase: CreatePlaylistUseCase,
    private val renamePlaylistUseCase: RenamePlaylistUseCase,
    private val deletePlaylistUseCase: DeletePlaylistUseCase,
    private val toggleLovedUseCase: ToggleLovedUseCase,
) : ViewModel() {

    private val showCreateSheetState = MutableStateFlow(false)
    private val playlistToRenameState = MutableStateFlow<PlaylistUi?>(null)
    private val playlistToDeleteState = MutableStateFlow<PlaylistUi?>(null)

    // Combine the three dialog-state flows into one triple to stay within
    // the 5-flow typed combine overload (6-flow uses Array<*> and loses types).
    private data class DialogState(
        val showCreate: Boolean,
        val renameTarget: PlaylistUi?,
        val deleteTarget: PlaylistUi?,
    )

    private val dialogState: Flow<DialogState> = combine(
        showCreateSheetState,
        playlistToRenameState,
        playlistToDeleteState,
    ) { showCreate, renamePl, deletePl ->
        DialogState(showCreate, renamePl, deletePl)
    }

    val uiState: StateFlow<LovedUiState> = combine(
        musicRepository.getLovedSongs(),
        playlistRepository.getPlaylists(),
        playerController.playbackState,
        dialogState,
    ) { loved, playlists, playback, dlg ->
        val totalMs = loved.sumOf { it.durationMs }
        val totalMin = totalMs / 1000 / 60
        val hrs = totalMin / 60
        val mins = totalMin % 60
        val formattedDur = if (hrs > 0) "${hrs}h ${mins}m" else "${mins}m"

        val isPlayingLoved = playback.isPlaying && loved.any { it.id == playback.currentSong?.id }

        LovedUiState(
            lovedSongs = loved.map { it.toItemUi() },
            rawLovedSongs = loved,
            playlists = playlists.map { it.toUi() },
            lovedDeckTotalDuration = formattedDur,
            showCreateSheet = dlg.showCreate,
            playlistToRename = dlg.renameTarget,
            playlistToDelete = dlg.deleteTarget,
            isPlayingLovedDeck = isPlayingLoved,
            isLoading = false,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = LovedUiState(isLoading = true),
    )

    fun onPlayLovedDeck() {
        val songs = uiState.value.rawLovedSongs
        if (songs.isNotEmpty()) {
            playerController.setQueue(songs, startIndex = 0, playWhenReady = true)
        }
    }

    fun onPlaySong(song: SongItemUi) {
        val songs = uiState.value.rawLovedSongs
        val index = songs.indexOfFirst { it.id == song.id }
        if (index != -1) {
            playerController.setQueue(songs, startIndex = index, playWhenReady = true)
        } else {
            viewModelScope.launch {
                musicRepository.getSongById(song.id).firstOrNull()?.let {
                    playerController.setQueue(listOf(it), startIndex = 0, playWhenReady = true)
                }
            }
        }
    }

    fun onPlayPlaylist(playlistId: String) {
        viewModelScope.launch {
            playlistRepository.getPlaylistSongs(playlistId).firstOrNull()?.let { songs ->
                if (songs.isNotEmpty()) {
                    playerController.setQueue(songs, startIndex = 0, playWhenReady = true)
                }
            }
        }
    }

    fun onToggleLoved(songId: String) {
        viewModelScope.launch {
            toggleLovedUseCase(songId)
        }
    }

    fun onToggleCreateSheet(show: Boolean) {
        showCreateSheetState.value = show
    }

    fun onCreatePlaylist(name: String, description: String?) {
        viewModelScope.launch {
            createPlaylistUseCase(name, description)
            showCreateSheetState.value = false
        }
    }

    fun onStartRenamePlaylist(playlist: PlaylistUi) {
        playlistToRenameState.value = playlist
    }

    fun onDismissRename() {
        playlistToRenameState.value = null
    }

    fun onRenamePlaylist(name: String, description: String?) {
        val target = playlistToRenameState.value ?: return
        viewModelScope.launch {
            renamePlaylistUseCase(target.id, name, description)
            playlistToRenameState.value = null
        }
    }

    fun onStartDeletePlaylist(playlist: PlaylistUi) {
        playlistToDeleteState.value = playlist
    }

    fun onDismissDelete() {
        playlistToDeleteState.value = null
    }

    fun onConfirmDeletePlaylist() {
        val target = playlistToDeleteState.value ?: return
        viewModelScope.launch {
            deletePlaylistUseCase(target.id)
            playlistToDeleteState.value = null
        }
    }
}

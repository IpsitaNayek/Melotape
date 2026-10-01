package com.melotape.ui.screens.songlist

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.melotape.domain.logic.SideSplitter
import com.melotape.domain.model.Playlist
import com.melotape.domain.model.Song
import com.melotape.domain.repository.MusicRepository
import com.melotape.domain.repository.PlaylistRepository
import com.melotape.domain.usecase.*
import com.melotape.player.controller.PlayerController
import com.melotape.player.model.PlaybackStateUi
import com.melotape.ui.mapper.toItemUi
import com.melotape.ui.mapper.toUi
import com.melotape.ui.model.PlaylistUi
import com.melotape.ui.model.SongItemUi
import com.melotape.ui.model.SongSortOrder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SongListUiState(
    val title: String = "Vintage Masters C-90",
    val playlistDescription: String? = null,
    val isPlaylist: Boolean = false,
    val playlistId: String? = null,
    val sideLabel: String = "SIDE A",
    val tapeType: String = "TYPE I • NORMAL BIAS",
    val trackCountInfo: String = "18 Tracks • 1h 14m",
    val tapeLengthLabel: String = "C-90 STEREO",
    val yearLabel: String? = "REC. 1994",
    val progressText: String = "03:52 / 44:20",
    val reelLabel: String = "REEL 01",
    val sideTracksLabel: String = "Side A Tracks",
    val songs: List<SongItemUi> = emptyList(),
    val allSongs: List<Song> = emptyList(),
    val currentSongId: String? = null,
    val isPlaying: Boolean = false,
    val currentSideIndex: Int = 0,
    val isFlippedToSideB: Boolean = false,
    val remainingSideTracksCount: Int = 0,
    val remainingSideDurationText: String = "00:00",
    val currentSortOrder: SongSortOrder = SongSortOrder.ORIGINAL,
    val showSortMenu: Boolean = false,
    val showEditPlaylistSheet: Boolean = false,
    val showDeleteConfirmDialog: Boolean = false,
    val showAddToPlaylistSheet: Boolean = false,
    val selectedSongForAdd: SongItemUi? = null,
    val availablePlaylists: List<PlaylistUi> = emptyList(),
    val containingPlaylistIds: Set<String> = emptySet(),
    val isLoading: Boolean = false,
)

@HiltViewModel
class SongListViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val musicRepository: MusicRepository,
    private val playlistRepository: PlaylistRepository,
    private val playerController: PlayerController,
    private val sideSplitter: SideSplitter,
    private val toggleLovedUseCase: ToggleLovedUseCase,
    private val addToPlaylistUseCase: AddToPlaylistUseCase,
    private val removeFromPlaylistUseCase: RemoveFromPlaylistUseCase,
    private val reorderPlaylistUseCase: ReorderPlaylistUseCase,
    private val renamePlaylistUseCase: RenamePlaylistUseCase,
    private val deletePlaylistUseCase: DeletePlaylistUseCase,
) : ViewModel() {

    val sourceType: String = savedStateHandle.get<String>("sourceType") ?: "recent"
    val sourceId: String = savedStateHandle.get<String>("sourceId") ?: ""

    private val sideIndexState = MutableStateFlow(0)
    private val sortOrderState = MutableStateFlow(SongSortOrder.ORIGINAL)
    private val showSortMenuState = MutableStateFlow(false)
    private val showEditSheetState = MutableStateFlow(false)
    private val showDeleteDialogState = MutableStateFlow(false)
    private val showAddSheetState = MutableStateFlow(false)
    private val selectedSongForAddState = MutableStateFlow<SongItemUi?>(null)

    private val playlistInfoFlow: Flow<Playlist?> = if (sourceType == "playlist" && sourceId.isNotBlank()) {
        playlistRepository.getPlaylistById(sourceId)
    } else {
        flowOf(null)
    }

    private val rawSongsFlow: Flow<List<Song>> = when (sourceType) {
        "playlist" -> playlistRepository.getPlaylistSongs(sourceId)
        "loved" -> musicRepository.getLovedSongs()
        "local" -> musicRepository.getLocalSongs()
        "downloaded" -> musicRepository.getDownloadedSongs()
        "trending" -> musicRepository.getTrendingSongs()
        "vault" -> musicRepository.searchSongs(sourceId)
        else -> musicRepository.getRecentlyPlayed()
    }

    // Group side/sort/playlists into one flow so the outer combine stays ≤5 flows
    // (Kotlin only has typed combine overloads up to 5; 6+ falls back to Array<*>)
    private data class ListingsState(
        val sideIndex: Int,
        val sortOrder: SongSortOrder,
        val allPlaylists: List<Playlist>,
    )

    private data class DialogStates(
        val showSortMenu: Boolean,
        val showEditSheet: Boolean,
        val showDeleteDialog: Boolean,
        val showAddSheet: Boolean,
        val selectedSong: SongItemUi?,
    )

    private val listingsState: Flow<ListingsState> = combine(
        sideIndexState,
        sortOrderState,
        playlistRepository.getPlaylists(),
    ) { sideIndex, sortOrder, allPlaylists ->
        ListingsState(sideIndex, sortOrder, allPlaylists)
    }

    private val dialogStatesFlow: Flow<DialogStates> = combine(
        showSortMenuState,
        showEditSheetState,
        showDeleteDialogState,
        showAddSheetState,
        selectedSongForAddState,
    ) { sortMenu, editSheet, deleteDialog, addSheet, selectedSong ->
        DialogStates(sortMenu, editSheet, deleteDialog, addSheet, selectedSong)
    }

    val uiState: StateFlow<SongListUiState> = combine(
        rawSongsFlow,
        playlistInfoFlow,
        playerController.playbackState,
        listingsState,
    ) { songs: List<Song>, playlist: Playlist?, playback: PlaybackStateUi, listings: ListingsState ->
        val sideIndex = listings.sideIndex
        val sortOrder = listings.sortOrder
        val allPlaylists = listings.allPlaylists

        // 1. Sort songs
        val sortedSongs = when (sortOrder) {
            SongSortOrder.ORIGINAL -> songs
            SongSortOrder.TITLE -> songs.sortedBy { it.title }
            SongSortOrder.ARTIST -> songs.sortedBy { it.artist }
            SongSortOrder.DURATION -> songs.sortedByDescending { it.durationMs }
            SongSortOrder.RECENTLY_ADDED -> songs.reversed()
        }

        // 2. Split into cassette sides
        val sidesResult = sideSplitter.splitIntoSides(sortedSongs)
        val safeSideIndex = sideIndex.coerceIn(0, (sidesResult.sides.size - 1).coerceAtLeast(0))
        val activeSide = sidesResult.sides.getOrElse(safeSideIndex) { sidesResult.sideA }
        val otherSideIndex = if (safeSideIndex == 0) 1 else 0
        val otherSide = sidesResult.sides.getOrNull(otherSideIndex)

        // 3. Compute side progress
        val progressText = sideSplitter.calculateSideProgress(
            side = activeSide,
            currentSongId = playback.currentSong?.id,
            currentPositionMs = playback.currentPositionMs,
        )

        // 4. Titles and format metadata
        val title = when {
            playlist != null -> playlist.name
            sourceType == "loved" -> "Loved Tracks & Masters"
            sourceType == "local" -> "Pocket Mixtape (On Device)"
            sourceType == "downloaded" -> "Downloaded Cassettes"
            sourceType == "trending" -> "Trending Tape Reissues"
            sourceType == "vault" -> "Sound Vault • $sourceId"
            else -> "Vintage Masters C-90"
        }

        val trackCountInfo = SideSplitter.formatTrackCountInfo(
            trackCount = sortedSongs.size,
            durationMs = sidesResult.totalDurationMs,
        )

        val sideLetter = activeSide.sideLetter
        val isSideB = safeSideIndex > 0

        SongListUiState(
            title = title,
            playlistDescription = playlist?.description,
            isPlaylist = sourceType == "playlist",
            playlistId = if (sourceType == "playlist") sourceId else null,
            sideLabel = "SIDE $sideLetter",
            tapeType = if (isSideB) "TYPE II • HIGH BIAS" else "TYPE I • NORMAL BIAS",
            trackCountInfo = trackCountInfo,
            tapeLengthLabel = sidesResult.tapeLengthLabel,
            yearLabel = if (playlist != null) "REC. 1994" else "STEREO",
            progressText = progressText,
            reelLabel = activeSide.reelLabel,
            sideTracksLabel = "Side $sideLetter Tracks",
            songs = activeSide.songs.map { it.toItemUi() },
            allSongs = sortedSongs,
            currentSongId = playback.currentSong?.id,
            isPlaying = playback.isPlaying,
            currentSideIndex = safeSideIndex,
            isFlippedToSideB = isSideB,
            remainingSideTracksCount = otherSide?.trackCount ?: 0,
            remainingSideDurationText = SideSplitter.formatDuration(otherSide?.totalDurationMs ?: 0L),
            currentSortOrder = sortOrder,
            availablePlaylists = allPlaylists.map { it.toUi() },
            isLoading = false,
        )
    }.combine(dialogStatesFlow) { baseState, dialogs ->
        baseState.copy(
            showSortMenu = dialogs.showSortMenu,
            showEditPlaylistSheet = dialogs.showEditSheet,
            showDeleteConfirmDialog = dialogs.showDeleteDialog,
            showAddToPlaylistSheet = dialogs.showAddSheet,
            selectedSongForAdd = dialogs.selectedSong,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SongListUiState(isLoading = true),
    )


    fun onPlayAll() {
        val songs = uiState.value.allSongs
        if (songs.isNotEmpty()) {
            playerController.setQueue(songs, startIndex = 0, playWhenReady = true)
        }
    }

    fun onShuffle() {
        val songs = uiState.value.allSongs
        if (songs.isNotEmpty()) {
            playerController.setQueue(songs, startIndex = 0, playWhenReady = true)
            playerController.toggleShuffle()
        }
    }

    fun onSongClick(song: SongItemUi) {
        val songs = uiState.value.allSongs
        val index = songs.indexOfFirst { it.id == song.id }
        if (index != -1) {
            playerController.setQueue(songs, startIndex = index, playWhenReady = true)
        }
    }

    fun onFlipSide() {
        sideIndexState.update { if (it == 0) 1 else 0 }
    }

    fun onSortOrderSelect(order: SongSortOrder) {
        sortOrderState.value = order
    }

    fun onToggleSortMenu(show: Boolean) {
        showSortMenuState.value = show
    }

    fun onToggleLoved(songId: String) {
        viewModelScope.launch {
            toggleLovedUseCase(songId)
        }
    }

    fun onOpenAddToPlaylist(song: SongItemUi) {
        selectedSongForAddState.value = song
        showAddSheetState.value = true
    }

    fun onCloseAddToPlaylist() {
        showAddSheetState.value = false
        selectedSongForAddState.value = null
    }

    fun onTogglePlaylistMembership(playlistId: String, isAdded: Boolean) {
        val song = selectedSongForAddState.value ?: return
        viewModelScope.launch {
            if (isAdded) {
                addToPlaylistUseCase(playlistId, song.id)
            } else {
                removeFromPlaylistUseCase(playlistId, song.id)
            }
        }
    }

    fun onCreatePlaylistAndAdd(name: String, description: String?) {
        val song = selectedSongForAddState.value
        viewModelScope.launch {
            val newId = playlistRepository.createPlaylist(name, description)
            if (song != null) {
                addToPlaylistUseCase(newId, song.id)
            }
        }
    }

    fun onToggleEditSheet(show: Boolean) {
        showEditSheetState.value = show
    }

    fun onRenamePlaylist(name: String, description: String?) {
        if (sourceType == "playlist" && sourceId.isNotBlank()) {
            viewModelScope.launch {
                renamePlaylistUseCase(sourceId, name, description)
            }
        }
    }

    fun onToggleDeleteDialog(show: Boolean) {
        showDeleteDialogState.value = show
    }

    fun onDeletePlaylist(onDeleted: () -> Unit) {
        if (sourceType == "playlist" && sourceId.isNotBlank()) {
            viewModelScope.launch {
                deletePlaylistUseCase(sourceId)
                onDeleted()
            }
        }
    }

    fun onReorderSongs(fromIndex: Int, toIndex: Int) {
        if (sourceType != "playlist" || sourceId.isBlank()) return
        val currentSongs = uiState.value.allSongs.toMutableList()
        if (fromIndex in currentSongs.indices && toIndex in currentSongs.indices) {
            val item = currentSongs.removeAt(fromIndex)
            currentSongs.add(toIndex, item)
            viewModelScope.launch {
                reorderPlaylistUseCase(sourceId, currentSongs.map { it.id })
            }
        }
    }
}

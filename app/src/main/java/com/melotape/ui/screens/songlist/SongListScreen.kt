package com.melotape.ui.screens.songlist

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.melotape.ui.components.*
import com.melotape.ui.model.SongItemUi
import com.melotape.ui.model.SongSortOrder
import com.melotape.ui.theme.*

/**
 * Route (stateful) — connects Hilt ViewModel to the stateless SongListScreen.
 */
@Composable
fun SongListRoute(
    onBack: () -> Unit,
    onNavigateToNowPlaying: () -> Unit,
    viewModel: SongListViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    SongListScreen(
        state = state,
        onBack = onBack,
        onSongClick = { song ->
            viewModel.onSongClick(song)
            onNavigateToNowPlaying()
        },
        onPlayAll = {
            viewModel.onPlayAll()
            onNavigateToNowPlaying()
        },
        onShuffle = viewModel::onShuffle,
        onFlipSide = viewModel::onFlipSide,
        onToggleLoved = viewModel::onToggleLoved,
        onOpenSortMenu = { viewModel.onToggleSortMenu(true) },
        onOpenAddToPlaylist = viewModel::onOpenAddToPlaylist,
        onEditPlaylist = { viewModel.onToggleEditSheet(true) },
        onDeletePlaylist = { viewModel.onToggleDeleteDialog(true) },
    )

    // Sequence / Sort Bottom Sheet
    if (state.showSortMenu) {
        SortOrderBottomSheet(
            currentOrder = state.currentSortOrder,
            onSelectOrder = viewModel::onSortOrderSelect,
            onDismiss = { viewModel.onToggleSortMenu(false) },
        )
    }

    // Add to Playlist Bottom Sheet
    if (state.showAddToPlaylistSheet && state.selectedSongForAdd != null) {
        AddToPlaylistBottomSheet(
            songTitle = state.selectedSongForAdd!!.title,
            playlists = state.availablePlaylists,
            containingPlaylistIds = state.containingPlaylistIds,
            onTogglePlaylist = viewModel::onTogglePlaylistMembership,
            onCreateNewPlaylist = {
                viewModel.onCloseAddToPlaylist()
                viewModel.onToggleEditSheet(true)
            },
            onDismiss = viewModel::onCloseAddToPlaylist,
        )
    }

    // Edit / Rename Playlist Bottom Sheet
    if (state.showEditPlaylistSheet) {
        PlaylistEditorSheet(
            sheetTitle = if (state.isPlaylist) "EDIT MIXTAPE" else "CREATE NEW MIXTAPE",
            confirmText = if (state.isPlaylist) "Save Changes" else "Create Tape",
            initialName = if (state.isPlaylist) state.title else "",
            initialDescription = state.playlistDescription,
            onConfirm = { name, desc ->
                if (state.isPlaylist) {
                    viewModel.onRenamePlaylist(name, desc)
                } else {
                    viewModel.onCreatePlaylistAndAdd(name, desc)
                }
            },
            onDismiss = { viewModel.onToggleEditSheet(false) },
        )
    }

    // Delete Playlist Confirmation Dialog
    if (state.showDeleteConfirmDialog) {
        ConfirmDialog(
            title = "Delete Mixtape?",
            message = "This cassette reel will be removed from your collection. Music files will remain on your device.",
            confirmText = "Delete Tape",
            onConfirm = {
                viewModel.onDeletePlaylist(onDeleted = onBack)
            },
            onDismiss = { viewModel.onToggleDeleteDialog(false) },
        )
    }
}

/**
 * Screen (stateless) — Cassette Album Detail / Song List screen.
 */
@Composable
fun SongListScreen(
    state: SongListUiState,
    onBack: () -> Unit,
    onSongClick: (SongItemUi) -> Unit,
    onPlayAll: () -> Unit,
    onShuffle: () -> Unit,
    onFlipSide: () -> Unit,
    onToggleLoved: (String) -> Unit,
    onOpenSortMenu: () -> Unit,
    onOpenAddToPlaylist: (SongItemUi) -> Unit,
    onEditPlaylist: () -> Unit,
    onDeletePlaylist: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var showOptionsMenu by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Background),
    ) {
        // Top Bar with Back Arrow and optional Playlist Edit/Delete actions
        MelotapeTopBar(
            title = "Cassette Album Detail",
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = TextPrimary,
                    )
                }
            },
            actions = {
                if (state.isPlaylist) {
                    Box {
                        IconButton(onClick = { showOptionsMenu = true }) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "Mixtape Options",
                                tint = TextSecondary,
                            )
                        }
                        DropdownMenu(
                            expanded = showOptionsMenu,
                            onDismissRequest = { showOptionsMenu = false },
                            containerColor = SurfaceElevated,
                        ) {
                            DropdownMenuItem(
                                text = { Text("Edit Mixtape", color = TextPrimary) },
                                onClick = {
                                    showOptionsMenu = false
                                    onEditPlaylist()
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.Edit, contentDescription = null, tint = Amber)
                                },
                            )
                            DropdownMenuItem(
                                text = { Text("Delete Mixtape", color = Burgundy) },
                                onClick = {
                                    showOptionsMenu = false
                                    onDeletePlaylist()
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.Delete, contentDescription = null, tint = Burgundy)
                                },
                            )
                        }
                    }
                }
            },
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 32.dp),
        ) {
            // 1. Cassette Tape Header
            item {
                CassetteHeader(
                    title = state.title,
                    sideLabel = state.sideLabel,
                    tapeType = state.tapeType,
                    trackCountInfo = state.trackCountInfo,
                    tapeLengthLabel = state.tapeLengthLabel,
                    yearLabel = state.yearLabel,
                    progressText = state.progressText,
                    reelLabel = state.reelLabel,
                    sideTracksLabel = state.sideTracksLabel,
                    sortLabel = state.currentSortOrder.displayName,
                    isSpinning = state.isPlaying,
                    onPlayAll = onPlayAll,
                    onShuffle = onShuffle,
                    onSort = onOpenSortMenu,
                )
            }

            if (state.songs.isEmpty()) {
                item {
                    EmptyState(
                        title = "No Tracks on ${state.sideLabel}",
                        message = "This side of the tape has no audio tracks recorded.",
                        icon = Icons.Default.MusicOff,
                    )
                }
            } else {
                // 2. Tracklist Rows matching Cassette Detail design
                itemsIndexed(state.songs, key = { _, song -> song.id }) { index, song ->
                    val isCurrent = song.id == state.currentSongId

                    if (isCurrent) {
                        // Highlighted Active Track Row (Orange / Salmon card as in screenshot)
                        ActiveTrackRow(
                            index = index + 1,
                            song = song,
                            isPlaying = state.isPlaying,
                            onClick = { onSongClick(song) },
                            onToggleLoved = { onToggleLoved(song.id) },
                            onMoreOptions = { onOpenAddToPlaylist(song) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 3.dp),
                        )
                    } else {
                        // Standard Dark Card Track Row
                        StandardTrackRow(
                            index = index + 1,
                            song = song,
                            onClick = { onSongClick(song) },
                            onToggleLoved = { onToggleLoved(song.id) },
                            onMoreOptions = { onOpenAddToPlaylist(song) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 3.dp),
                        )
                    }
                }
            }

            // 3. Flip to Side B (or Side A) Card
            item {
                Spacer(Modifier.height(16.dp))
                FlipSideCard(
                    isSideB = state.isFlippedToSideB,
                    remainingTracksCount = state.remainingSideTracksCount,
                    remainingDurationText = state.remainingSideDurationText,
                    onFlip = onFlipSide,
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
            }
        }
    }
}

/**
 * Active / Currently playing track row with distinctive PrimaryAccent (salmon) background.
 */
@Composable
private fun ActiveTrackRow(
    index: Int,
    song: SongItemUi,
    isPlaying: Boolean,
    onClick: () -> Unit,
    onToggleLoved: () -> Unit,
    onMoreOptions: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(PrimaryAccent)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 10.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Equalizer animated icon
                Icon(
                    imageVector = Icons.Default.Equalizer,
                    contentDescription = "Playing",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp),
                )

                Spacer(Modifier.width(10.dp))

                // Tape reel hub icon
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF8F3924)),
                    contentAlignment = Alignment.Center,
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(Color.White),
                    )
                }

                Spacer(Modifier.width(12.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = song.title,
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                            maxLines = 1,
                        )
                        Spacer(Modifier.width(6.dp))
                        TapeChip(
                            text = "NOW",
                            tone = ChipTone.Burgundy,
                        )
                    }
                    Text(
                        text = song.subtitle + (song.formatLabel?.let { " • $it" } ?: ""),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.85f),
                        maxLines = 1,
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onToggleLoved,
                    modifier = Modifier.size(36.dp),
                ) {
                    Icon(
                        imageVector = if (song.isLoved) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Loved",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp),
                    )
                }

                // Circular play/pause button (White background with PrimaryAccent icon)
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .clickable(onClick = onClick),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = "Play/Pause",
                        tint = PrimaryAccent,
                        modifier = Modifier.size(22.dp),
                    )
                }
            }
        }
    }
}

/**
 * Standard inactive track row with sleek dark card aesthetic.
 */
@Composable
private fun StandardTrackRow(
    index: Int,
    song: SongItemUi,
    onClick: () -> Unit,
    onToggleLoved: () -> Unit,
    onMoreOptions: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Surface)
            .border(1.dp, SurfaceElevated.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 10.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = String.format("%02d", index),
                    style = MaterialTheme.typography.labelMedium,
                    color = TextSecondary,
                    modifier = Modifier.width(24.dp),
                )

                // Cassette reel hub thumbnail
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(SurfaceElevated),
                    contentAlignment = Alignment.Center,
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF0F1B29)),
                    )
                }

                Spacer(Modifier.width(12.dp))

                Column {
                    Text(
                        text = song.title,
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary,
                        maxLines = 1,
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = song.subtitle,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                            maxLines = 1,
                        )
                        song.formatLabel?.let { format ->
                            Spacer(Modifier.width(6.dp))
                            TapeChip(
                                text = format,
                                tone = ChipTone.Surface,
                            )
                        }
                    }
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onToggleLoved,
                    modifier = Modifier.size(34.dp),
                ) {
                    Icon(
                        imageVector = if (song.isLoved) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Loved",
                        tint = if (song.isLoved) PrimaryAccent else TextSecondary,
                        modifier = Modifier.size(18.dp),
                    )
                }

                IconButton(
                    onClick = onClick,
                    modifier = Modifier.size(34.dp),
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Play",
                        tint = TextSecondary,
                        modifier = Modifier.size(20.dp),
                    )
                }

                IconButton(
                    onClick = onMoreOptions,
                    modifier = Modifier.size(30.dp),
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "More",
                        tint = TextSecondary,
                        modifier = Modifier.size(16.dp),
                    )
                }
            }
        }
    }
}

/**
 * Flip to Side B / Reverse interactive card.
 */
@Composable
private fun FlipSideCard(
    isSideB: Boolean,
    remainingTracksCount: Int,
    remainingDurationText: String,
    onFlip: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Surface)
            .border(1.dp, SurfaceElevated, RoundedCornerShape(14.dp))
            .padding(16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Cassette reel icon
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF241512)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Default.Album,
                        contentDescription = null,
                        tint = PrimaryAccent,
                        modifier = Modifier.size(22.dp),
                    )
                }

                Spacer(Modifier.width(12.dp))

                Column {
                    Text(
                        text = if (isSideB) "Flip to Side A" else "Flip to Side B",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary,
                    )
                    Text(
                        text = "$remainingTracksCount Remaining Tracks • $remainingDurationText left",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                    )
                }
            }

            Button(
                onClick = onFlip,
                colors = ButtonDefaults.buttonColors(containerColor = SurfaceElevated),
                border = androidx.compose.foundation.BorderStroke(1.dp, Amber),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
            ) {
                Text(
                    text = "Reverse",
                    style = MaterialTheme.typography.labelMedium,
                    color = Amber,
                )
                Spacer(Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Default.SwapHoriz,
                    contentDescription = null,
                    tint = Amber,
                    modifier = Modifier.size(16.dp),
                )
            }
        }
    }
}

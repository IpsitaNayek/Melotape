package com.melotape.ui.screens.loved

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import com.melotape.ui.model.PlaylistUi
import com.melotape.ui.model.SongItemUi
import com.melotape.ui.theme.*

/**
 * Route (stateful) — connects Hilt ViewModel to the LovedScreen.
 */
@Composable
fun LovedRoute(
    onNavigateToNowPlaying: () -> Unit,
    onNavigateToSongList: (sourceType: String, sourceId: String) -> Unit,
    viewModel: LovedViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LovedScreen(
        state = state,
        onSongClick = { song ->
            viewModel.onPlaySong(song)
            onNavigateToNowPlaying()
        },
        onPlayLovedDeck = {
            viewModel.onPlayLovedDeck()
            onNavigateToNowPlaying()
        },
        onOpenLovedSongsList = {
            onNavigateToSongList("loved", "")
        },
        onPlaylistClick = { playlist ->
            onNavigateToSongList("playlist", playlist.id)
        },
        onPlayPlaylist = { playlist ->
            viewModel.onPlayPlaylist(playlist.id)
            onNavigateToNowPlaying()
        },
        onOpenCreateSheet = { viewModel.onToggleCreateSheet(true) },
        onStartRename = viewModel::onStartRenamePlaylist,
        onStartDelete = viewModel::onStartDeletePlaylist,
        onToggleLoved = viewModel::onToggleLoved,
    )

    // Create New Mixtape Sheet
    if (state.showCreateSheet) {
        PlaylistEditorSheet(
            sheetTitle = "CREATE NEW MIXTAPE",
            confirmText = "Create Tape",
            onConfirm = viewModel::onCreatePlaylist,
            onDismiss = { viewModel.onToggleCreateSheet(false) },
        )
    }

    // Rename Mixtape Sheet
    state.playlistToRename?.let { target ->
        PlaylistEditorSheet(
            sheetTitle = "RENAME MIXTAPE",
            confirmText = "Save Changes",
            initialName = target.name,
            initialDescription = target.description,
            onConfirm = { name, desc ->
                viewModel.onRenamePlaylist(name, desc)
            },
            onDismiss = viewModel::onDismissRename,
        )
    }

    // Delete Mixtape Confirmation Dialog
    state.playlistToDelete?.let { target ->
        ConfirmDialog(
            title = "Delete \"${target.name}\"?",
            message = "This tape reel will be erased from your deck. Songs on your device will not be deleted.",
            confirmText = "Delete Tape",
            onConfirm = viewModel::onConfirmDeletePlaylist,
            onDismiss = viewModel::onDismissDelete,
        )
    }
}

/**
 * Screen (stateless) — Loved Songs & Mixtapes tab.
 */
@Composable
fun LovedScreen(
    state: LovedUiState,
    onSongClick: (SongItemUi) -> Unit,
    onPlayLovedDeck: () -> Unit,
    onOpenLovedSongsList: () -> Unit,
    onPlaylistClick: (PlaylistUi) -> Unit,
    onPlayPlaylist: (PlaylistUi) -> Unit,
    onOpenCreateSheet: () -> Unit,
    onStartRename: (PlaylistUi) -> Unit,
    onStartDelete: (PlaylistUi) -> Unit,
    onToggleLoved: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Background),
    ) {
        MelotapeTopBar(
            title = "Loved & Mixtapes",
            actions = {
                IconButton(onClick = onOpenCreateSheet) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "New Mixtape",
                        tint = Amber,
                    )
                }
            },
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 32.dp),
        ) {
            // 1. Pinned Loved Songs Cassette Deck Card
            item {
                LovedSongsDeckCard(
                    songCount = state.lovedSongs.size,
                    durationText = state.lovedDeckTotalDuration,
                    isPlaying = state.isPlayingLovedDeck,
                    onPlay = onPlayLovedDeck,
                    onClick = onOpenLovedSongsList,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                )
                Spacer(Modifier.height(12.dp))
            }

            // 2. My Mixtapes Section
            item {
                SectionHeader(
                    title = "My Mixtapes",
                    actionText = "+ New Tape",
                    onAction = onOpenCreateSheet,
                )
                if (state.playlists.isEmpty()) {
                    Text(
                        text = "No custom tapes created yet. Tap '+ New Tape' to start dubbing!",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    )
                } else {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        items(state.playlists, key = { it.id }) { playlist ->
                            MixtapeCardWithOptions(
                                playlist = playlist,
                                onClick = { onPlaylistClick(playlist) },
                                onPlay = { onPlayPlaylist(playlist) },
                                onRename = { onStartRename(playlist) },
                                onDelete = { onStartDelete(playlist) },
                            )
                        }
                    }
                }

                Spacer(Modifier.height(20.dp))
            }

            // 3. Loved Tracks Section
            item {
                SectionHeader(
                    title = "Loved Tracks",
                    actionText = "${state.lovedSongs.size} tracks",
                    onAction = onOpenLovedSongsList,
                )
                Spacer(Modifier.height(4.dp))
            }

            if (state.lovedSongs.isEmpty()) {
                item {
                    EmptyState(
                        title = "No Loved Tapes Yet",
                        message = "Tap the heart icon on any tape or track to save it to your personal loved deck.",
                        icon = Icons.Default.FavoriteBorder,
                    )
                }
            } else {
                items(state.lovedSongs, key = { it.id }) { song ->
                    Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 2.dp)) {
                        SongRow(
                            song = song,
                            onClick = { onSongClick(song) },
                            trailing = {
                                HeartButton(
                                    isLoved = song.isLoved,
                                    onToggle = { onToggleLoved(song.id) },
                                )
                                PlayPauseButton(
                                    isPlaying = false,
                                    onClick = { onSongClick(song) },
                                    size = PlayButtonSize.Small,
                                )
                            },
                        )
                    }
                }
            }
        }
    }
}

/**
 * Pinned Loved Songs cassette deck card at the top of Loved screen.
 */
@Composable
private fun LovedSongsDeckCard(
    songCount: Int,
    durationText: String,
    isPlaying: Boolean,
    onPlay: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF162536))
            .border(1.5.dp, PrimaryAccent.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
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
                // Heart Cassette Reel Icon
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(PrimaryAccent.copy(alpha = 0.2f))
                        .border(1.dp, PrimaryAccent, CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "Loved Songs",
                        tint = PrimaryAccent,
                        modifier = Modifier.size(26.dp),
                    )
                }

                Spacer(Modifier.width(14.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Loved Songs Deck",
                            style = MaterialTheme.typography.titleLarge,
                            color = TextPrimary,
                        )
                        Spacer(Modifier.width(8.dp))
                        TapeChip(
                            text = "PINNED",
                            tone = ChipTone.Amber,
                        )
                    }
                    Text(
                        text = "$songCount Favorite Masters • $durationText Total",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                    )
                }
            }

            // Play Deck Button
            IconButton(
                onClick = onPlay,
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(PrimaryAccent),
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = "Play Loved Deck",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp),
                )
            }
        }
    }
}

/**
 * Mixtape CassetteCard with overflow menu (Rename, Delete).
 */
@Composable
private fun MixtapeCardWithOptions(
    playlist: PlaylistUi,
    onClick: () -> Unit,
    onPlay: () -> Unit,
    onRename: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var showMenu by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        CassetteCard(
            title = playlist.name,
            subtitle = "${playlist.songCount} Tracks • C-60",
            artwork = playlist.coverArtUri,
            topLabel = if (playlist.isPinned) "PINNED" else "MIXTAPE",
            tag = "SIDE A",
            onClick = onClick,
            accentColor = PrimaryAccent,
        )

        // Three-dot options menu icon in top right of card
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp),
        ) {
            IconButton(
                onClick = { showMenu = true },
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(SurfaceElevated.copy(alpha = 0.8f)),
            ) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "Mixtape Options",
                    tint = TextSecondary,
                    modifier = Modifier.size(16.dp),
                )
            }

            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false },
                containerColor = SurfaceElevated,
            ) {
                DropdownMenuItem(
                    text = { Text("Play Mixtape", color = TextPrimary) },
                    onClick = {
                        showMenu = false
                        onPlay()
                    },
                    leadingIcon = {
                        Icon(Icons.Default.PlayArrow, contentDescription = null, tint = PrimaryAccent)
                    },
                )
                DropdownMenuItem(
                    text = { Text("Rename Mixtape", color = TextPrimary) },
                    onClick = {
                        showMenu = false
                        onRename()
                    },
                    leadingIcon = {
                        Icon(Icons.Default.Edit, contentDescription = null, tint = Amber)
                    },
                )
                DropdownMenuItem(
                    text = { Text("Delete Mixtape", color = Burgundy) },
                    onClick = {
                        showMenu = false
                        onDelete()
                    },
                    leadingIcon = {
                        Icon(Icons.Default.Delete, contentDescription = null, tint = Burgundy)
                    },
                )
            }
        }
    }
}

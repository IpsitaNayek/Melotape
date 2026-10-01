package com.melotape.ui.screens.loved

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.melotape.ui.components.*
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
        onSongClick = { onNavigateToNowPlaying() },
        onPlaylistClick = { playlist -> onNavigateToSongList("playlist", playlist.id) },
        onOpenCreateSheet = { viewModel.onToggleCreateSheet(true) },
        onToggleLoved = viewModel::onToggleLoved,
    )

    if (state.showCreateSheet) {
        CreatePlaylistBottomSheet(
            onDismiss = { viewModel.onToggleCreateSheet(false) },
            onCreate = viewModel::onCreatePlaylist,
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
    onPlaylistClick: (com.melotape.domain.model.Playlist) -> Unit,
    onOpenCreateSheet: () -> Unit,
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
            // 1. My Mixtapes Carousel
            item {
                SectionHeader(
                    title = "My Mixtapes",
                    actionText = "+ New Tape",
                    onAction = onOpenCreateSheet,
                )
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(state.playlists, key = { it.id }) { playlist ->
                        CassetteCard(
                            title = playlist.name,
                            subtitle = "${playlist.songCount} Tracks • C-60",
                            artwork = playlist.coverArtUri,
                            topLabel = if (playlist.isPinned) "PINNED" else "MIXTAPE",
                            tag = "SIDE A",
                            onClick = { onPlaylistClick(playlist) },
                            accentColor = PrimaryAccent,
                        )
                    }
                }

                Spacer(Modifier.height(20.dp))
            }

            // 2. Loved Tracks Section
            item {
                SectionHeader(
                    title = "Loved Tracks",
                    actionText = "${state.lovedSongs.size} tracks",
                    onAction = {},
                )
                Spacer(Modifier.height(4.dp))
            }

            if (state.lovedSongs.isEmpty()) {
                item {
                    EmptyState(
                        title = "No Loved Tapes Yet",
                        message = "Tap the heart icon on any tape or track to save it to your personal loved deck.",
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

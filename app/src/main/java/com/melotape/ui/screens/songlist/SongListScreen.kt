package com.melotape.ui.screens.songlist

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material.icons.filled.SwapHoriz
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
    )
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
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Background),
    ) {
        // Top Bar with Back Arrow
        MelotapeTopBar(
            title = state.title,
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = TextPrimary,
                    )
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
                    progressText = state.progressText,
                    isSpinning = state.isPlaying,
                    onPlayAll = onPlayAll,
                    onShuffle = onShuffle,
                    onSort = {},
                )
            }

            // 2. Tracklist Rows
            itemsIndexed(state.songs, key = { _, song -> song.id }) { index, song ->
                val isCurrent = song.id == state.currentSongId

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 2.dp)
                        .then(
                            if (isCurrent) {
                                Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(SurfaceElevated)
                                    .border(1.dp, PrimaryAccent.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                            } else Modifier
                        ),
                ) {
                    SongRow(
                        song = song,
                        onClick = { onSongClick(song) },
                        isCurrent = isCurrent,
                        leading = {
                            if (isCurrent && state.isPlaying) {
                                Icon(
                                    imageVector = Icons.Default.Equalizer,
                                    contentDescription = "Playing",
                                    tint = PrimaryAccent,
                                    modifier = Modifier.size(16.dp),
                                )
                            } else {
                                Text(
                                    text = String.format("%02d", index + 1),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (isCurrent) PrimaryAccent else TextSecondary,
                                )
                            }
                        },
                        meta = {
                            if (isCurrent) {
                                TapeChip(
                                    text = "NOW",
                                    tone = ChipTone.Burgundy,
                                )
                                Spacer(Modifier.width(6.dp))
                            }
                        },
                        trailing = {
                            HeartButton(
                                isLoved = song.isLoved,
                                onToggle = { onToggleLoved(song.id) },
                            )
                            PlayPauseButton(
                                isPlaying = isCurrent && state.isPlaying,
                                onClick = { onSongClick(song) },
                                size = PlayButtonSize.Small,
                            )
                        },
                    )
                }
            }

            // 3. Flip to Side B Card
            item {
                Spacer(Modifier.height(16.dp))
                FlipSideCard(
                    isSideB = state.isFlippedToSideB,
                    onFlip = onFlipSide,
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
            }
        }
    }
}

/**
 * Flip to Side B interactive card with Reverse button.
 */
@Composable
private fun FlipSideCard(
    isSideB: Boolean,
    onFlip: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Surface)
            .border(1.dp, SurfaceElevated, RoundedCornerShape(12.dp))
            .padding(16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column {
                Text(
                    text = if (isSideB) "Flip to Side A" else "Flip to Side B",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                )
                Text(
                    text = if (isSideB) "Return to Side A • 44:20 Total" else "10 Additional Vintage Masters • 45:40 Total",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                )
            }

            Button(
                onClick = onFlip,
                colors = ButtonDefaults.buttonColors(containerColor = SurfaceElevated),
                border = androidx.compose.foundation.BorderStroke(1.dp, Amber),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
            ) {
                Icon(
                    imageVector = Icons.Default.SwapHoriz,
                    contentDescription = null,
                    tint = Amber,
                    modifier = Modifier.size(16.dp),
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = "Reverse",
                    style = MaterialTheme.typography.labelSmall,
                    color = Amber,
                )
            }
        }
    }
}

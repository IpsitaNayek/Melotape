package com.melotape.ui.screens.nowplaying

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.melotape.ui.components.*
import com.melotape.ui.theme.*

/**
 * Route (stateful) — connects Hilt ViewModel to the NowPlayingScreen.
 */
@Composable
fun NowPlayingRoute(
    onBack: () -> Unit,
    viewModel: NowPlayingViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    NowPlayingScreen(
        state = state,
        onBack = onBack,
        onPlayPause = viewModel::onPlayPause,
        onSeek = viewModel::onSeek,
        onNext = viewModel::onNext,
        onPrevious = viewModel::onPrevious,
        onToggleLoved = viewModel::onToggleLoved,
        onToggleShuffle = viewModel::onToggleShuffle,
        onToggleRepeat = viewModel::onToggleRepeat,
        onSetVolume = viewModel::onSetVolume,
        onOpenDetails = { viewModel.onToggleDetailsSheet(true) },
    )

    if (state.showDetailsSheet) {
        SongDetailsBottomSheet(
            song = state.currentSong,
            onDismiss = { viewModel.onToggleDetailsSheet(false) },
        )
    }
}

/**
 * Screen (stateless) — renders the Now Playing screen matching the screenshot.
 */
@Composable
fun NowPlayingScreen(
    state: NowPlayingUiState,
    onBack: () -> Unit,
    onPlayPause: () -> Unit,
    onSeek: (Float) -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onToggleLoved: () -> Unit,
    onToggleShuffle: () -> Unit,
    onToggleRepeat: () -> Unit,
    onSetVolume: (Float) -> Unit,
    onOpenDetails: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Background)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // 1. Top Bar
        MelotapeTopBar(
            title = "Now Playing",
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
                IconButton(onClick = onOpenDetails) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Track Specs",
                        tint = TextSecondary,
                    )
                }
            },
        )

        Spacer(Modifier.height(12.dp))

        // 2. Vinyl Turntable Platter with Tonearm
        VinylPlatter(
            artwork = state.currentSong?.artwork,
            isPlaying = state.isPlaying,
            progress = state.progress,
            size = 270.dp,
        )

        Spacer(Modifier.height(20.dp))

        // 3. Song Info: Title, Artist, Side Chip, Heart
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = state.currentSong?.title ?: "Electric Feel (Analog Remaster)",
                        style = MaterialTheme.typography.headlineMedium,
                        color = TextPrimary,
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = state.currentSong?.subtitle ?: "MGMT • Oracular Spectacular Tape Edition",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                    )
                }

                HeartButton(
                    isLoved = state.currentSong?.isLoved ?: false,
                    onToggle = onToggleLoved,
                )
            }

            Spacer(Modifier.height(8.dp))

            TapeChip(
                text = state.sideChip,
                tone = ChipTone.Amber,
                leadingDot = true,
            )
        }

        Spacer(Modifier.height(16.dp))

        // 4. Quick Action Bar: Tape Bias, Lyrics, Share
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            OutlinedButton(
                onClick = onOpenDetails,
                shape = RoundedCornerShape(8.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceElevated),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
            ) {
                Icon(Icons.Default.Tune, contentDescription = null, tint = Amber, modifier = Modifier.size(14.dp))
                Spacer(Modifier.width(4.dp))
                Text("Tape Bias", style = MaterialTheme.typography.labelSmall, color = TextPrimary)
            }

            OutlinedButton(
                onClick = {},
                shape = RoundedCornerShape(8.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceElevated),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
            ) {
                Icon(Icons.Default.Article, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(14.dp))
                Spacer(Modifier.width(4.dp))
                Text("Lyrics", style = MaterialTheme.typography.labelSmall, color = TextPrimary)
            }

            OutlinedButton(
                onClick = onOpenDetails,
                shape = RoundedCornerShape(8.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceElevated),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
            ) {
                Icon(Icons.Default.Share, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(14.dp))
                Spacer(Modifier.width(4.dp))
                Text("Share", style = MaterialTheme.typography.labelSmall, color = TextPrimary)
            }
        }

        Spacer(Modifier.height(16.dp))

        // 5. Seek Bar with Elapsed, Standard Cassette Speed (4.76 cm/s), Remaining
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
        ) {
            Slider(
                value = state.progress,
                onValueChange = onSeek,
                colors = SliderDefaults.colors(
                    thumbColor = PrimaryAccent,
                    activeTrackColor = PrimaryAccent,
                    inactiveTrackColor = SurfaceElevated,
                ),
                modifier = Modifier.fillMaxWidth(),
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = state.elapsedText,
                    style = MaterialTheme.typography.labelSmall,
                    fontFamily = FontFamily.Monospace,
                    color = TextSecondary,
                )

                // 4.76 cm/s standard cassette tape playback speed
                Text(
                    text = state.speedText,
                    style = MaterialTheme.typography.labelSmall,
                    fontFamily = FontFamily.Monospace,
                    color = Amber,
                )

                Text(
                    text = state.remainingText,
                    style = MaterialTheme.typography.labelSmall,
                    fontFamily = FontFamily.Monospace,
                    color = TextSecondary,
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        // 6. Transport Controls: Shuffle, Prev, Play/Pause, Next, Repeat
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onToggleShuffle) {
                Icon(
                    imageVector = Icons.Default.Shuffle,
                    contentDescription = "Shuffle",
                    tint = if (state.isShuffle) PrimaryAccent else TextSecondary,
                )
            }

            IconButton(onClick = onPrevious) {
                Icon(
                    imageVector = Icons.Default.SkipPrevious,
                    contentDescription = "Previous",
                    tint = TextPrimary,
                    modifier = Modifier.size(32.dp),
                )
            }

            // Big Salmon Play / Pause Button
            IconButton(
                onClick = onPlayPause,
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(PrimaryAccent),
            ) {
                Icon(
                    imageVector = if (state.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (state.isPlaying) "Pause" else "Play",
                    tint = Color.White,
                    modifier = Modifier.size(36.dp),
                )
            }

            IconButton(onClick = onNext) {
                Icon(
                    imageVector = Icons.Default.SkipNext,
                    contentDescription = "Next",
                    tint = TextPrimary,
                    modifier = Modifier.size(32.dp),
                )
            }

            IconButton(onClick = onToggleRepeat) {
                Icon(
                    imageVector = Icons.Default.Repeat,
                    contentDescription = "Repeat",
                    tint = if (state.isRepeat) PrimaryAccent else TextSecondary,
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        // 7. Volume Slider
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 28.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                contentDescription = "Volume",
                tint = TextSecondary,
                modifier = Modifier.size(20.dp),
            )
            Spacer(Modifier.width(12.dp))
            Slider(
                value = state.volume,
                onValueChange = onSetVolume,
                colors = SliderDefaults.colors(
                    thumbColor = TextPrimary,
                    activeTrackColor = TextPrimary,
                    inactiveTrackColor = SurfaceElevated,
                ),
                modifier = Modifier.weight(1f),
            )
        }

        Spacer(Modifier.height(20.dp))

        // 8. Analog Specs & Master Chain Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Surface)
                .border(1.dp, SurfaceElevated, RoundedCornerShape(12.dp))
                .clickable { onOpenDetails() }
                .padding(16.dp),
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "ANALOG SPECS & MASTER CHAIN",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary,
                    )
                    Text(
                        text = "DOLBY B-NR ACTIVE",
                        style = MaterialTheme.typography.labelSmall,
                        color = Amber,
                    )
                }

                Spacer(Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    StatTile(
                        label = "ENCODING",
                        value = state.specs.encoding,
                        modifier = Modifier.weight(1f),
                        emphasis = true,
                    )
                    StatTile(
                        label = "HEADROOM",
                        value = state.specs.headroom,
                        modifier = Modifier.weight(1f),
                    )
                    StatTile(
                        label = "WARMTH",
                        value = state.specs.warmth,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }

        Spacer(Modifier.height(32.dp))
    }
}

package com.melotape.ui.screens.home

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.QueueMusic
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.melotape.domain.model.Playlist
import com.melotape.ui.components.*
import com.melotape.ui.model.SongItemUi
import com.melotape.ui.theme.*

/**
 * Route (stateful) — connects Hilt ViewModel to the stateless HomeScreen.
 */
@Composable
fun HomeRoute(
    onNavigateToNowPlaying: () -> Unit,
    onNavigateToSongList: (sourceType: String, sourceId: String) -> Unit,
    onNavigateToProfile: () -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            viewModel.onPermissionResult(isGranted)
        },
    )

    HomeScreen(
        state = state,
        onSongClick = { songItem ->
            viewModel.onPlaySong(songItem)
            onNavigateToNowPlaying()
        },
        onSeeAllClick = { sourceType, sourceId -> onNavigateToSongList(sourceType, sourceId) },
        onPlayFeatured = {
            viewModel.onPlayFeatured()
            onNavigateToNowPlaying()
        },
        onPlayPause = viewModel::onPlayPause,
        onRewind = viewModel::onRewind,
        onFastForward = viewModel::onFastForward,
        onStop = viewModel::onStop,
        onResetCounter = viewModel::onResetTapeCounter,
        onRescanLocal = viewModel::onRescanLocal,
        onToggleLoved = viewModel::onToggleLoved,
        onProfileClick = onNavigateToProfile,
        onRequestPermission = {
            permissionLauncher.launch(viewModel.requiredPermission)
        },
        onOpenSettings = {
            context.startActivity(viewModel.createAppSettingsIntent())
        },
    )
}

/**
 * Screen (stateless) — renders Home design matching the reference screenshot.
 */
@Composable
fun HomeScreen(
    state: HomeUiState,
    onSongClick: (SongItemUi) -> Unit,
    onSeeAllClick: (sourceType: String, sourceId: String) -> Unit,
    onPlayFeatured: () -> Unit,
    onPlayPause: () -> Unit,
    onRewind: () -> Unit = {},
    onFastForward: () -> Unit = {},
    onStop: () -> Unit = {},
    onResetCounter: () -> Unit,
    onRescanLocal: () -> Unit,
    onToggleLoved: (String) -> Unit,
    onProfileClick: () -> Unit,
    onRequestPermission: () -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Background)
            .verticalScroll(rememberScrollState()),
    ) {
        // 1. Top Bar
        MelotapeTopBar(
            title = "Melotape",
            onProfileClick = onProfileClick,
        )

        Spacer(Modifier.height(8.dp))

        // 2. Greeting & Tape Counter
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column {
                Text(
                    text = state.greeting,
                    style = MaterialTheme.typography.headlineLarge,
                    color = TextPrimary,
                )
                Text(
                    text = state.statusLine,
                    style = MaterialTheme.typography.labelMedium,
                    color = if (state.isPlaying) Color(0xFF4ADE80) else TextSecondary,
                )
            }

            // Skeuomorphic mechanical tape counter (e.g., 042 / RESET)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(Surface)
                    .border(1.dp, SurfaceElevated, RoundedCornerShape(6.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp),
            ) {
                Text(
                    text = state.tapeCounter,
                    style = MaterialTheme.typography.titleMedium,
                    fontFamily = FontFamily.Monospace,
                    color = Amber,
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = "RESET",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary,
                    modifier = Modifier.clickable { onResetCounter() },
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        // 3. Featured Mixtape Cassette Card (Cream / Salmon styling)
        state.featuredMixtape?.let { featured ->
            FeaturedMixtapeCard(
                playlist = featured,
                onPlay = onPlayFeatured,
                onQueue = { onSeeAllClick("playlist", featured.id) },
                onClick = { onSeeAllClick("playlist", featured.id) },
                modifier = Modifier.padding(horizontal = 16.dp),
            )
        }

        Spacer(Modifier.height(20.dp))

        // 4. Recently Played Carousel
        SectionHeader(
            title = "Recently Played",
            actionText = "See all",
            onAction = { onSeeAllClick("recent", "") },
        )

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(state.recentlyPlayed.take(5), key = { it.id }) { song ->
                CassetteCard(
                    title = song.title,
                    subtitle = song.subtitle,
                    artwork = song.artwork,
                    topLabel = song.formatLabel ?: "TAPE #01",
                    tag = "SIDE A",
                    onClick = { onSongClick(song) },
                    accentColor = Amber,
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        // 5. Downloaded for Offline
        SectionHeader(
            title = "Downloaded for Offline",
            actionText = "See all",
            onAction = { onSeeAllClick("downloaded", "") },
        )
        Text(
            text = "Side A/B Complete • 182 MB",
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondary,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 2.dp),
        )
        Spacer(Modifier.height(4.dp))
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            state.downloadedSongs.take(3).forEach { song ->
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

        Spacer(Modifier.height(16.dp))

        // 6. Pocket Mixtape (On Device)
        SectionHeader(
            title = "Pocket Mixtape (On Device)",
            actionText = "See all",
            onAction = { onSeeAllClick("local", "") },
        )


        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            if (!state.hasAudioPermission) {
                // Permission Rationale Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Surface)
                        .border(1.dp, Amber.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                        .padding(16.dp),
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.FolderOpen,
                                contentDescription = null,
                                tint = Amber,
                                modifier = Modifier.size(24.dp),
                            )
                            Spacer(Modifier.width(10.dp))
                            Text(
                                text = "CALIBRATE LOCAL CASSETTE DECK",
                                style = MaterialTheme.typography.titleMedium,
                                color = TextPrimary,
                            )
                        }

                        Spacer(Modifier.height(6.dp))

                        Text(
                            text = "Grant Melotape audio permission to read and index master audio tracks stored on this device.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                        )

                        Spacer(Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            Button(
                                onClick = onRequestPermission,
                                colors = ButtonDefaults.buttonColors(containerColor = Amber),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f).height(38.dp),
                            ) {
                                Text("Grant Permission", style = MaterialTheme.typography.labelMedium, color = Color(0xFF1E293B))
                            }

                            OutlinedButton(
                                onClick = onOpenSettings,
                                shape = RoundedCornerShape(8.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceElevated),
                                modifier = Modifier.weight(1f).height(38.dp),
                            ) {
                                Icon(Icons.Default.Settings, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(4.dp))
                                Text("App Settings", style = MaterialTheme.typography.labelMedium, color = TextPrimary)
                            }
                        }
                    }
                }
            } else {
                // MediaStore tracks
                state.pocketMixtape.take(3).forEach { song ->
                    SongRow(
                        song = song,
                        onClick = { onSongClick(song) },
                        trailing = {
                            TapeChip(
                                text = song.formatLabel ?: "FLAC",
                                tone = ChipTone.Surface,
                            )
                            Spacer(Modifier.width(8.dp))
                            PlayPauseButton(
                                isPlaying = false,
                                onClick = { onSongClick(song) },
                                size = PlayButtonSize.Small,
                            )
                        },
                    )
                }

                Spacer(Modifier.height(8.dp))

                // Re-scan Local Audio Tracks Button
                OutlinedButton(
                    onClick = onRescanLocal,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(42.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Amber),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2C3E55)),
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = null,
                        tint = Amber,
                        modifier = Modifier.size(16.dp),
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = "Re-scan Local Audio Tracks",
                        style = MaterialTheme.typography.labelMedium,
                        color = Amber,
                    )
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        // 7. Deck Master Calibration Transport Controls
        DeckTransport(
            isPlaying = state.isPlaying,
            onRewind = onRewind,
            onStop = onStop,
            onPlayPause = onPlayPause,
            onFastForward = onFastForward,
            onRecord = {},
            modifier = Modifier.padding(horizontal = 16.dp),
        )

        Spacer(Modifier.height(32.dp))
    }
}

/**
 * Featured Mixtape Card matching the top featured tape in screenshot.
 */
@Composable
private fun FeaturedMixtapeCard(
    playlist: Playlist,
    onPlay: () -> Unit,
    onQueue: () -> Unit,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Cream)
            .border(1.dp, Color(0xFFE2D6C0), RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(16.dp),
    ) {

        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                TapeChip(
                    text = "TYPE I • NORMAL BIAS",
                    tone = ChipTone.Burgundy,
                    leadingDot = true,
                )
                TapeChip(
                    text = "FEATURED MIXTAPE",
                    tone = ChipTone.Amber,
                    leadingDot = false,
                )
            }

            Spacer(Modifier.height(12.dp))

            Text(
                text = playlist.name,
                style = MaterialTheme.typography.headlineMedium,
                color = Color(0xFF1E293B),
            )

            Text(
                text = playlist.description ?: "Warm analog saturation, Otari tape heads, 12 vintage masters",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF475569),
            )

            Spacer(Modifier.height(6.dp))

            Text(
                text = "48 min • 12 vintage masters",
                style = MaterialTheme.typography.labelSmall,
                fontFamily = FontFamily.Monospace,
                color = Burgundy,
            )

            Spacer(Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Button(
                    onClick = onPlay,
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryAccent),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(40.dp),
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp),
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = "Play Deck",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                    )
                }

                OutlinedButton(
                    onClick = onQueue,
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFC4B89D)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF1E293B)),
                    modifier = Modifier.height(40.dp),
                ) {
                    Icon(
                        imageVector = Icons.Default.QueueMusic,
                        contentDescription = null,
                        tint = Color(0xFF1E293B),
                        modifier = Modifier.size(18.dp),
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = "Queue",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color(0xFF1E293B),
                    )
                }
            }
        }
    }
}

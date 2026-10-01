package com.melotape.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.melotape.ui.theme.*

/**
 * Route (stateful) — connects Hilt ViewModel to the ProfileScreen.
 */
@Composable
fun ProfileRoute(
    onNavigateToDownloads: () -> Unit,
    onNavigateToUpload: () -> Unit,
    onNavigateToSignIn: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    ProfileScreen(
        state = state,
        onToggleDolby = viewModel::onToggleDolby,
        onToggleHiss = viewModel::onToggleHissFilter,
        onToggleNotifications = viewModel::onToggleNotifications,
        onClearCache = viewModel::onClearCache,
        onDismissBanner = viewModel::onDismissBanner,
        onManageDownloads = onNavigateToDownloads,
        onSelectAudioFile = onNavigateToUpload,
        onSignOut = onNavigateToSignIn,
    )
}

/**
 * Screen (stateless) — renders Profile matching the design screenshot.
 */
@Composable
fun ProfileScreen(
    state: ProfileUiState,
    onToggleDolby: (Boolean) -> Unit,
    onToggleHiss: (Boolean) -> Unit,
    onToggleNotifications: (Boolean) -> Unit,
    onClearCache: () -> Unit,
    onDismissBanner: () -> Unit,
    onManageDownloads: () -> Unit,
    onSelectAudioFile: () -> Unit,
    onSignOut: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Background)
            .verticalScroll(rememberScrollState()),
    ) {
        MelotapeTopBar(title = "Profile")

        Spacer(Modifier.height(8.dp))

        // Message banner if cache cleared
        state.messageBanner?.let { message ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(SurfaceElevated)
                    .padding(12.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(text = message, style = MaterialTheme.typography.bodySmall, color = Amber)
                    IconButton(onClick = onDismissBanner, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Dismiss", tint = TextSecondary, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }

        // 1. User Profile Header Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Surface)
                .border(1.dp, SurfaceElevated, RoundedCornerShape(16.dp))
                .padding(16.dp),
        ) {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    // Avatar with tape rim
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF243449))
                            .border(2.dp, Amber, CircleShape),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "User Avatar",
                            tint = Cream,
                            modifier = Modifier.size(36.dp),
                        )
                    }

                    Spacer(Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = state.user.displayName,
                                style = MaterialTheme.typography.headlineMedium,
                                color = TextPrimary,
                            )
                            Spacer(Modifier.width(8.dp))
                            TapeChip(
                                text = "PRO DECK",
                                tone = ChipTone.Amber,
                            )
                        }

                        Text(
                            text = state.user.handle ?: "@alex_analog",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary,
                        )

                        Text(
                            text = state.user.email ?: "alex@melotape.audio",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                        )

                        Spacer(Modifier.height(4.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(if (state.dolbyBEnabled) Color(0xFF4ADE80) else TextSecondary),
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                text = if (state.dolbyBEnabled) "Dolby B active" else "Dolby bypassed",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (state.dolbyBEnabled) Color(0xFF4ADE80) else TextSecondary,
                            )
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                // Stats Row: MIXTAPES / TAPE BIAS / HOURS SPUN
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    StatTile(
                        label = "MIXTAPES",
                        value = "${state.user.mixtapeCount}",
                        modifier = Modifier.weight(1f),
                    )
                    StatTile(
                        label = "TAPE BIAS",
                        value = state.user.tapeBias,
                        modifier = Modifier.weight(1f),
                        emphasis = true,
                    )
                    StatTile(
                        label = "HOURS SPUN",
                        value = state.user.hoursSpun,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // 2. Downloaded Music Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(Surface)
                .border(1.dp, SurfaceElevated, RoundedCornerShape(14.dp))
                .padding(16.dp),
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Downloaded Music",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextPrimary,
                        )
                        Spacer(Modifier.width(8.dp))
                        TapeChip(
                            text = "${state.user.downloadedTrackCount} Tracks",
                            tone = ChipTone.Surface,
                        )
                    }

                    TapeChip(
                        text = "FLAC 96k",
                        tone = ChipTone.Amber,
                    )
                }

                Spacer(Modifier.height(12.dp))

                // Cassette storage bar (e.g. 4.2 GB / 64 GB)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = "Cassette Storage",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary,
                    )
                    Text(
                        text = "${state.user.downloadedSizeLabel} / ${state.user.totalStorageLabel}",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextPrimary,
                    )
                }

                Spacer(Modifier.height(6.dp))

                LinearProgressIndicator(
                    progress = { state.user.storageUsedFraction },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = PrimaryAccent,
                    trackColor = SurfaceElevated,
                )

                Spacer(Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Button(
                        onClick = onManageDownloads,
                        modifier = Modifier.weight(1f).height(38.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SurfaceElevated),
                        shape = RoundedCornerShape(8.dp),
                    ) {
                        Text("Manage Downloads", style = MaterialTheme.typography.labelMedium, color = TextPrimary)
                    }

                    OutlinedButton(
                        onClick = onClearCache,
                        modifier = Modifier.weight(1f).height(38.dp),
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceElevated),
                    ) {
                        Text("Clear Cache", style = MaterialTheme.typography.labelMedium, color = Amber)
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // 3. Upload & Tape Rip Card with Dashed Drop Zone
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(Surface)
                .border(1.dp, SurfaceElevated, RoundedCornerShape(14.dp))
                .padding(16.dp),
        ) {
            Column {
                Text(
                    text = "Upload & Tape Rip",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                )
                Text(
                    text = "Import master audio files into your cloud cassette deck",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                )

                Spacer(Modifier.height(12.dp))

                DashedDropZone(
                    title = "Drag & Drop Audio Files",
                    subtitle = "Supports FLAC, MP3, WAV • Otari MX50 Tape Engine",
                    buttonText = "Select Audio File",
                    onSelectFile = onSelectAudioFile,
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        // 4. Deck Configuration List
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(Surface)
                .border(1.dp, SurfaceElevated, RoundedCornerShape(14.dp))
                .padding(vertical = 8.dp),
        ) {
            PaddingValues(horizontal = 16.dp).let {
                Text(
                    text = "DECK CONFIGURATION",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                )
            }

            SettingsRow(
                title = "Audio Quality",
                subtitle = state.audioQuality,
                onClick = {},
            )

            SettingsRow(
                title = "Analog Tape EQ",
                subtitle = "5-Band Otari Calibration • Flat Bias",
                onClick = {},
            )

            SettingsRow(
                title = "Deck Aesthetic",
                subtitle = state.deckAesthetic,
                onClick = {},
            )

            SettingsToggleRow(
                title = "Dolby B-Type NR",
                subtitle = "Active hiss reduction on high frequencies",
                checked = state.dolbyBEnabled,
                onCheckedChange = onToggleDolby,
            )

            SettingsToggleRow(
                title = "Hiss Filter",
                subtitle = "Tape deck high-shelf analog cut",
                checked = state.hissFilterEnabled,
                onCheckedChange = onToggleHiss,
            )

            SettingsToggleRow(
                title = "Notifications",
                subtitle = "Mixtape rip completions & weekly reissues",
                checked = state.notificationsEnabled,
                onCheckedChange = onToggleNotifications,
            )

            SettingsRow(
                title = "About Melotape",
                subtitle = state.versionName,
                onClick = {},
            )
        }

        Spacer(Modifier.height(20.dp))

        // 5. Sign Out and Switch Profile Actions
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Button(
                onClick = onSignOut,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Burgundy),
                shape = RoundedCornerShape(10.dp),
            ) {
                Icon(Icons.Default.ExitToApp, contentDescription = null, tint = Cream, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Sign Out", style = MaterialTheme.typography.titleMedium, color = Cream)
            }

            Spacer(Modifier.height(8.dp))

            TextButton(
                onClick = onSignOut,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Switch Profile", style = MaterialTheme.typography.labelMedium, color = TextSecondary)
            }
        }

        Spacer(Modifier.height(32.dp))
    }
}

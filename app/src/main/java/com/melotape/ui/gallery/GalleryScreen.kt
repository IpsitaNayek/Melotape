package com.melotape.ui.gallery

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.melotape.ui.components.*
import com.melotape.ui.model.SongItemUi
import com.melotape.ui.model.SourceBadgeUi
import com.melotape.ui.theme.*

/**
 * Debug gallery — shows all Phase 1 components for visual testing.
 * Only accessible via Screen.Gallery in debug builds.
 */
@Composable
fun GalleryScreen() {
    val fakeSong = SongItemUi(
        id          = "g1",
        title       = "The Youth",
        subtitle    = "MGMT • 3:52",
        artwork     = null,
        formatLabel = "FLAC 24",
        source      = SourceBadgeUi.Jamendo,
        isLoved     = false,
    )
    val fakeSongLoved = fakeSong.copy(id = "g2", title = "Space Song", isLoved = true, source = SourceBadgeUi.Device)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Background),
        contentPadding = PaddingValues(bottom = 32.dp),
    ) {
        item {
            MelotapeTopBar(title = "Component Gallery")
        }

        sectionItem("Top Bar")
        item {
            MelotapeTopBar(title = "Secondary Screen")
        }

        sectionItem("Buttons")
        item {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                PrimaryButton("▶ Play Deck", onClick = {})
                SecondaryButton("⇄ Shuffle", onClick = {})
                DangerButton("Clear Cache", onClick = {})
                PrimaryButton("Disabled", onClick = {}, enabled = false)
            }
        }

        sectionItem("Tape Chips")
        item {
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.padding(16.dp),
            ) {
                TapeChip("TYPE I • NORMAL BIAS")
                TapeChip("FLAC 24")
                TapeChip("NOW", style = ChipStyle.Accent)
                TapeChip("SIDE A", leadingDot = true)
                TapeChip("OFFLINE", style = ChipStyle.Amber)
                TapeChip("FEATURED", style = ChipStyle.Burgundy)
            }
        }

        sectionItem("Play/Pause & Heart Buttons")
        item {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment     = androidx.compose.ui.Alignment.CenterVertically,
                modifier              = Modifier.padding(16.dp),
            ) {
                PlayPauseButton(false, {}, "Play", size = PlayButtonSize.Small)
                PlayPauseButton(true,  {}, "Pause", size = PlayButtonSize.Medium)
                PlayPauseButton(false, {}, "Play", size = PlayButtonSize.Large)
                HeartButton(isLoved = false, onToggle = {})
                HeartButton(isLoved = true, onToggle = {})
            }
        }

        sectionItem("Progress Lines")
        item {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                ProgressLine(0f)
                ProgressLine(0.35f)
                ProgressLine(0.7f)
                ProgressLine(1f, fillColor = MaterialTheme.colorScheme.secondary, height = 6.dp)
            }
        }

        sectionItem("Artwork Image")
        item {
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.padding(16.dp),
            ) {
                ArtworkImage(null, "placeholder", size = 40.dp)
                ArtworkImage(null, "placeholder", size = 56.dp)
                ArtworkImage(null, "placeholder", size = 80.dp)
            }
        }

        sectionItem("Song Row")
        item {
            Column(
                Modifier.padding(horizontal = 8.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                SongRow(
                    song     = fakeSong,
                    onClick  = {},
                    trailing = {
                        HeartButton(isLoved = false, onToggle = {})
                        PlayPauseButton(false, {}, "Play", size = PlayButtonSize.Small)
                    },
                )
                SongRow(
                    song       = fakeSongLoved,
                    onClick    = {},
                    isCurrent  = true,
                    leading    = { RankNumber(1) },
                    trailing   = {
                        HeartButton(isLoved = true, onToggle = {})
                        PlayPauseButton(true, {}, "Pause", size = PlayButtonSize.Small)
                    },
                )
            }
        }

        sectionItem("CassetteCard")
        item {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(16.dp),
            ) {
                CassetteCard(
                    title = "Cassette Lo-Fi", subtitle = "Warm tape hiss",
                    artwork = null, topLabel = "TAPE #01", onClick = {},
                    accentColor = VaultRed,
                )
                CassetteCard(
                    title = "City Pop", subtitle = "Tokyo 1984",
                    artwork = null, topLabel = "33 RPM", tag = "SIDE B", onClick = {},
                    accentColor = Amber,
                )
            }
        }

        sectionItem("Stat Tiles")
        item {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(16.dp),
            ) {
                StatTile("MIXTAPES", "28", modifier = Modifier.weight(1f))
                StatTile("TAPE BIAS", "Type II", modifier = Modifier.weight(1f), emphasis = true)
                StatTile("HOURS", "342h", modifier = Modifier.weight(1f))
            }
        }

        sectionItem("Settings Rows")
        item {
            Column(Modifier.padding(vertical = 4.dp)) {
                SettingsRow(title = "Audio Quality", subtitle = "Master FLAC", onClick = {})
                SettingsToggleRow("Dolby B NR", checked = true, onCheckedChange = {})
                SettingsToggleRow("HX Pro", checked = false, onCheckedChange = {})
            }
        }

        sectionItem("States")
        item {
            Column {
                LoadingState(message = "Loading library…")
                EmptyState(title = "No songs yet", subtitle = "Tap + to add music")
                ErrorState(message = "Couldn't connect", onRetry = {})
                ErrorBanner(message = "Jamendo offline", onRetry = {})
            }
        }

        sectionItem("Mini Player")
        item {
            MiniPlayerPlaceholder(
                onTap     = {},
                song      = fakeSong,
                isPlaying = true,
                progress  = 0.42f,
            )
        }
    }
}

// Helper extension for gallery section dividers
private fun androidx.compose.foundation.lazy.LazyListScope.sectionItem(title: String) {
    item {
        HorizontalDivider(
            color = SurfaceElevated,
            modifier = Modifier.padding(vertical = 8.dp),
        )
        Text(
            text     = title.uppercase(),
            style    = MaterialTheme.typography.labelLarge,
            color    = TextSecondary,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
        )
    }
}



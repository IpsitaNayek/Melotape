package com.melotape.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.melotape.ui.model.SongItemUi
import com.melotape.ui.model.SourceBadgeUi
import com.melotape.ui.theme.*

/**
 * Mini player — sits above the bottom nav bar, visible whenever a song is loaded.
 * Shows album art, title, artist, heart, play/pause, and a thin progress line.
 *
 * Phase 6 will wire this to real PlayerViewModel state.
 * For Phase 1, this is a placeholder stub showing the layout.
 */
@Composable
fun MiniPlayerPlaceholder(
    onTap: () -> Unit,
    modifier: Modifier = Modifier,
    song: SongItemUi? = null,
    isPlaying: Boolean = false,
    progress: Float = 0f,
    onPlayPause: () -> Unit = {},
    onToggleLoved: () -> Unit = {},
) {
    if (song == null) return   // hidden when nothing is playing

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Surface)
            .clickable(onClick = onTap),
    ) {
        // Progress line — 3dp thin at top edge
        ProgressLine(
            progress  = progress,
            fillColor = PrimaryAccent,
            height    = 2.dp,
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp)
                .padding(horizontal = 12.dp),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            // Small spinning reel / artwork
            ArtworkImage(
                model              = song.artwork,
                contentDescription = "Now playing: ${song.title}",
                size               = 46.dp,
            )

            // Title + artist
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Text(
                    text     = song.title,
                    style    = MaterialTheme.typography.titleSmall,
                    color    = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text     = song.subtitle,
                    style    = MaterialTheme.typography.bodySmall,
                    color    = TextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            // Heart button
            HeartButton(
                isLoved  = song.isLoved,
                onToggle = onToggleLoved,
                size     = 22.dp,
            )

            // Play/Pause
            PlayPauseButton(
                isPlaying          = isPlaying,
                onClick            = onPlayPause,
                contentDescription = if (isPlaying) "Pause" else "Play",
                size               = PlayButtonSize.Medium,
            )
        }
    }
}

@Preview(name = "MiniPlayer — playing")
@Composable
private fun PreviewMiniPlayer() {
    MelotapeTheme {
        MiniPlayerPlaceholder(
            onTap = {},
            song  = SongItemUi(
                id       = "1",
                title    = "The Youth",
                subtitle = "MGMT • Side A",
                artwork  = null,
                formatLabel = null,
                source   = SourceBadgeUi.Jamendo,
                isLoved  = true,
            ),
            isPlaying = true,
            progress  = 0.35f,
        )
    }
}

@Preview(name = "MiniPlayer — hidden (no song)")
@Composable
private fun PreviewMiniPlayerHidden() {
    MelotapeTheme {
        MiniPlayerPlaceholder(onTap = {}, song = null)
    }
}

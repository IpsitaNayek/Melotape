package com.melotape.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.melotape.ui.model.DownloadStatus
import com.melotape.ui.model.SongItemUi
import com.melotape.ui.model.SourceBadgeUi
import com.melotape.ui.theme.*

/**
 * Universal song/track row — used in Song List, Search, Trending, Downloads,
 * Loved, Playlist, Device Library, and Home sections.
 *
 * Slot-based: callers provide [leading] (index/rank/equalizer), [meta] (chips),
 * and [trailing] (heart/download/play button). One layout, consistent appearance.
 *
 * @param song       The UI model to display
 * @param isCurrent  Whether this is the currently playing track (highlights row)
 * @param leading    Optional leading slot: rank number, track index, or equalizer glyph
 * @param meta       Optional meta slot in the text area: chips like "FLAC 24", "NOW", source badge
 * @param trailing   Trailing slot: heart + play, download badge, etc.
 */
@Composable
fun SongRow(
    song: SongItemUi,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isCurrent: Boolean = false,
    leading: @Composable (() -> Unit)? = null,
    meta: @Composable (RowScope.() -> Unit)? = null,
    trailing: @Composable RowScope.() -> Unit = {},
) {
    val bgColor = if (isCurrent) SurfaceElevated else Surface

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .background(bgColor)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 10.dp)
            .semantics { selected = isCurrent },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        // Leading slot: track index, rank, or equalizer
        if (leading != null) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(28.dp),
            ) {
                leading()
            }
        }

        // Artwork
        ArtworkImage(
            model              = song.artwork,
            contentDescription = "Album art for ${song.title}",
            size               = 46.dp,
        )

        // Text + meta chips
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(3.dp),
        ) {
            Text(
                text     = song.title,
                style    = MaterialTheme.typography.titleSmall,
                color    = if (isCurrent) PrimaryAccent else TextPrimary,
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
            if (meta != null || song.formatLabel != null) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment     = Alignment.CenterVertically,
                ) {
                    if (isCurrent) {
                        TapeChip("NOW", style = ChipStyle.Accent)
                    }
                    if (song.formatLabel != null) {
                        TapeChip(song.formatLabel)
                    }
                    meta?.invoke(this)
                }
            }
        }

        // Trailing slot
        Row(
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            trailing()
        }
    }
}

/** Rank number for trending lists */
@Composable
fun RankNumber(rank: Int) {
    Text(
        text  = "%02d".format(rank),
        style = MaterialTheme.typography.labelLarge,
        color = TextSecondary,
    )
}

@Preview(name = "SongRow — default")
@Composable
private fun PreviewSongRow() {
    MelotapeTheme {
        Column(
            verticalArrangement = Arrangement.spacedBy(2.dp),
            modifier = Modifier.padding(8.dp),
        ) {
            SongRow(
                song = SongItemUi(
                    id = "1", title = "The Youth", subtitle = "MGMT • 3:52",
                    artwork = null, formatLabel = "FLAC 24",
                    source = SourceBadgeUi.Jamendo,
                ),
                onClick = {},
                trailing = {
                    HeartButton(isLoved = false, onToggle = {})
                    PlayPauseButton(
                        isPlaying = false, onClick = {},
                        contentDescription = "Play",
                        size = PlayButtonSize.Small,
                    )
                },
            )
            SongRow(
                song = SongItemUi(
                    id = "2", title = "Time to Pretend (very long song title that should ellipsis)",
                    subtitle = "MGMT • 4:21", artwork = null, formatLabel = "FLAC",
                    source = SourceBadgeUi.Device, isLoved = true,
                ),
                onClick = {},
                isCurrent = true,
                trailing = {
                    HeartButton(isLoved = true, onToggle = {})
                    PlayPauseButton(
                        isPlaying = true, onClick = {},
                        contentDescription = "Pause",
                        size = PlayButtonSize.Small,
                    )
                },
            )
            SongRow(
                song = SongItemUi(
                    id = "3", title = "Plastic Love", subtitle = "Mariya T. • SIDE A",
                    artwork = null, formatLabel = null, source = null,
                ),
                onClick = {},
                leading = { RankNumber(1) },
                trailing = {
                    PlayPauseButton(
                        isPlaying = false, onClick = {},
                        contentDescription = "Play",
                        size = PlayButtonSize.Small,
                    )
                },
            )
        }
    }
}

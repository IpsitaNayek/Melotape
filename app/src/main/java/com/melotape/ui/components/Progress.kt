package com.melotape.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.melotape.ui.theme.MelotapeTheme
import com.melotape.ui.theme.PrimaryAccent
import com.melotape.ui.theme.Surface
import com.melotape.ui.theme.SurfaceElevated

/**
 * Thin horizontal progress bar — used for:
 * - Mini player progress
 * - Storage bar in Profile
 * - Download / upload progress in rows
 * - Reel side progress in Cassette header
 *
 * [progress] is 0f..1f
 */
@Composable
fun ProgressLine(
    progress: Float,
    modifier: Modifier = Modifier,
    trackColor: Color = SurfaceElevated,
    fillColor: Color = PrimaryAccent,
    height: androidx.compose.ui.unit.Dp = 3.dp,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(trackColor)
            .height(height)
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(progress.coerceIn(0f, 1f))
                .clip(RoundedCornerShape(50))
                .background(fillColor)
        )
    }
}

/**
 * Source badge chip — small colored dot + label, indicates where a song comes from.
 */
@Composable
fun SourceBadge(
    source: com.melotape.ui.model.SourceBadgeUi,
    modifier: Modifier = Modifier,
) {
    val (label, color) = when (source) {
        com.melotape.ui.model.SourceBadgeUi.Jamendo    -> "JAMENDO" to MaterialTheme.colorScheme.secondary
        com.melotape.ui.model.SourceBadgeUi.Cloud      -> "CLOUD" to PrimaryAccent
        com.melotape.ui.model.SourceBadgeUi.Device     -> "DEVICE" to MaterialTheme.colorScheme.tertiary
        com.melotape.ui.model.SourceBadgeUi.Downloaded -> "SAVED" to MaterialTheme.colorScheme.tertiary
    }
    TapeChip(
        text        = label,
        modifier    = modifier,
        leadingDot  = true,
        dotColor    = color,
        style       = ChipStyle.Default,
    )
}

@Preview
@Composable
private fun PreviewProgressLine() {
    MelotapeTheme {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(16.dp),
        ) {
            ProgressLine(0f)
            ProgressLine(0.35f)
            ProgressLine(1f)
            ProgressLine(0.6f, fillColor = MaterialTheme.colorScheme.secondary, height = 6.dp)
        }
    }
}

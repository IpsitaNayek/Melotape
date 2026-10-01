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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.melotape.ui.theme.*

/**
 * Cassette album / playlist card for horizontal carousels.
 * Used in: Home (Recently Played, Downloaded, Pocket Mixtape), Loved playlists.
 */
@Composable
fun CassetteCard(
    title: String,
    subtitle: String,
    artwork: Any?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    topLabel: String? = null,      // e.g. "TAPE #01", "TOKYO 1984"
    tag: String? = null,           // e.g. "33 RPM", "SIDE B"
    accentColor: Color = PrimaryAccent,
) {
    Box(
        modifier = modifier
            .width(150.dp)
            .height(140.dp)
            .clip(MaterialTheme.shapes.medium)
            .background(SurfaceElevated)
            .clickable(onClick = onClick),
    ) {
        // Background artwork
        if (artwork != null) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(artwork)
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                contentScale       = ContentScale.Crop,
                modifier           = Modifier.fillMaxSize(),
            )
        }

        // Gradient overlay for text legibility
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        0f to Color.Transparent,
                        0.4f to Background.copy(alpha = 0.4f),
                        1f to Background.copy(alpha = 0.85f),
                    )
                )
        )

        // Top label + tag
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top,
        ) {
            if (topLabel != null) {
                Text(
                    text  = topLabel,
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary,
                )
            }
            if (tag != null) {
                TapeChip(text = tag)
            }
        }

        // Bottom content
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(
                text     = title,
                style    = MaterialTheme.typography.titleSmall,
                color    = TextPrimary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text     = subtitle,
                style    = MaterialTheme.typography.labelSmall,
                color    = TextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }

        // Accent corner dot
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(8.dp)
                .size(8.dp)
                .clip(androidx.compose.foundation.shape.CircleShape)
                .background(accentColor)
        )
    }
}

@Preview(name = "CassetteCard — no artwork")
@Composable
private fun PreviewCassetteCard() {
    MelotapeTheme {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(16.dp),
        ) {
            CassetteCard(
                title    = "Cassette Lo-Fi",
                subtitle = "Warm tape hiss",
                artwork  = null,
                topLabel = "TAPE #01",
                onClick  = {},
                accentColor = VaultRed,
            )
            CassetteCard(
                title    = "City Pop & Funk",
                subtitle = "Busting neon soul",
                artwork  = null,
                topLabel = "TOKYO 1984",
                tag      = "33 RPM",
                onClick  = {},
                accentColor = Amber,
            )
        }
    }
}

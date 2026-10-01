package com.melotape.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.melotape.ui.theme.MelotapeTheme
import com.melotape.ui.theme.Surface
import com.melotape.ui.theme.SurfaceElevated

/**
 * Artwork image with Coil + cassette-placeholder fallback.
 * Used in song rows, now playing platter, mini player, and profile avatar.
 *
 * [model] accepts: URL string, content URI, android.net.Uri, etc.
 * [placeholder] is shown while loading and on error.
 */
@Composable
fun ArtworkImage(
    model: Any?,
    contentDescription: String? = null,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    shape: androidx.compose.ui.graphics.Shape = MaterialTheme.shapes.small,
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(shape)
            .background(SurfaceElevated),
        contentAlignment = Alignment.Center,
    ) {
        if (model != null) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(model)
                    .crossfade(true)
                    .build(),
                contentDescription = contentDescription,
                contentScale       = ContentScale.Crop,
                modifier           = Modifier.fillMaxSize(),
            )
        } else {
            // Placeholder — cassette spool motif
            CassettePlaceholder(size = size * 0.55f)
        }
    }
}

/** Simple geometric cassette spool placeholder */
@Composable
private fun CassettePlaceholder(size: Dp) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(size),
    ) {
        // Outer ring
        Box(
            modifier = Modifier
                .size(size)
                .clip(CircleShape)
                .background(Surface)
        )
        // Inner hub
        Box(
            modifier = Modifier
                .size(size * 0.4f)
                .clip(CircleShape)
                .background(SurfaceElevated)
        )
    }
}

@Preview
@Composable
private fun PreviewArtworkImage() {
    MelotapeTheme {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(16.dp),
        ) {
            ArtworkImage(null, "placeholder", size = 48.dp)
            ArtworkImage(null, "placeholder", size = 64.dp, shape = CircleShape)
            ArtworkImage(
                model = "https://example.com/image.jpg",
                contentDescription = "Album art",
                size = 48.dp,
            )
        }
    }
}

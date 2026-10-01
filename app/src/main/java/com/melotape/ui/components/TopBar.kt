package com.melotape.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.melotape.ui.theme.*

/**
 * Melotape top app bar — logo tile + title + profile avatar button.
 * Consistent across all screens (primary tabs + secondary screens vary by title).
 */
@Composable
fun MelotapeTopBar(
    modifier: Modifier = Modifier,
    title: String = "Melotape",
    showLogo: Boolean = true,
    onProfileClick: () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = { DefaultProfileButton(onClick = onProfileClick) },
    navigationIcon: @Composable () -> Unit = {},
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Background)
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        // Navigation icon (e.g. back arrow on secondary screens)
        navigationIcon()

        // Logo tile
        if (showLogo) {
            LogoTile()
        }

        // Title
        Text(
            text     = title,
            style    = MaterialTheme.typography.headlineMedium,
            color    = TextPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )

        // Actions (profile button by default)
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment     = Alignment.CenterVertically,
        ) {
            actions()
        }
    }
}

/** Melotape logo tile — cassette spool icon with gradient */
@Composable
fun LogoTile(
    modifier: Modifier = Modifier,
    size: androidx.compose.ui.unit.Dp = 36.dp,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(8.dp))
            .background(SurfaceElevated),
    ) {
        // Two reel circles — simplified logo
        Row(
            horizontalArrangement = Arrangement.spacedBy(3.dp),
            verticalAlignment     = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(PrimaryAccent)
            )
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(PrimaryAccent.copy(alpha = 0.6f))
            )
        }
    }
}

/** Default profile / avatar button in the top bar */
@Composable
fun DefaultProfileButton(
    onClick: () -> Unit = {},
    avatarUrl: Any? = null,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(SurfaceElevated)
            .clickable(onClick = onClick),
    ) {
        if (avatarUrl != null) {
            ArtworkImage(
                model              = avatarUrl,
                contentDescription = "Profile",
                size               = 36.dp,
                shape              = CircleShape,
            )
        } else {
            Icon(
                imageVector        = Icons.Filled.Person,
                contentDescription = "Profile",
                tint               = TextSecondary,
                modifier           = Modifier.size(20.dp),
            )
        }
    }
}

@Preview(name = "MelotapeTopBar — home")
@Composable
private fun PreviewTopBarHome() {
    MelotapeTheme {
        MelotapeTopBar()
    }
}

@Preview(name = "MelotapeTopBar — secondary")
@Composable
private fun PreviewTopBarSecondary() {
    MelotapeTheme {
        MelotapeTopBar(
            title    = "Cassette Album Detail",
            showLogo = true,
        )
    }
}

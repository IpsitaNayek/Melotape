package com.melotape.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.melotape.ui.theme.MelotapeTheme
import com.melotape.ui.theme.PrimaryAccent
import com.melotape.ui.theme.Surface
import com.melotape.ui.theme.TextSecondary

enum class PlayButtonSize(val diameter: Dp) {
    Small(32.dp), Medium(44.dp), Large(56.dp)
}

/**
 * Play/Pause circle button used in: track rows, mini player, Now Playing, deck transport, featured card.
 * Three sizes: Small (rows), Medium (mini player / cards), Large (Now Playing).
 */
@Composable
fun PlayPauseButton(
    isPlaying: Boolean,
    onClick: () -> Unit,
    contentDescription: String = if (isPlaying) "Pause" else "Play",
    modifier: Modifier = Modifier,
    size: PlayButtonSize = PlayButtonSize.Medium,
    containerColor: Color = PrimaryAccent,
    contentColor: Color = Color.White,
) {
    FilledIconButton(
        onClick = onClick,
        modifier = modifier
            .size(size.diameter)
            .semantics { role = Role.Button },
        colors = IconButtonDefaults.filledIconButtonColors(
            containerColor = containerColor,
            contentColor   = contentColor,
        ),
    ) {
        Icon(
            imageVector        = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
            contentDescription = contentDescription,
            modifier           = Modifier.size(size.diameter * 0.55f),
        )
    }
}

/**
 * Heart / Loved toggle button.
 * Uses optimistic state — the ViewModel should update immediately.
 */
@Composable
fun HeartButton(
    isLoved: Boolean,
    onToggle: () -> Unit,
    contentDescription: String = if (isLoved) "Remove from Loved" else "Add to Loved",
    modifier: Modifier = Modifier,
    size: Dp = 24.dp,
    activeColor: Color = PrimaryAccent,
    inactiveColor: Color = TextSecondary,
) {
    // Subtle pulse animation when toggled to loved
    val scale by animateFloatAsState(
        targetValue = if (isLoved) 1.15f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "heart_scale",
    )

    IconButton(
        onClick  = onToggle,
        modifier = modifier
            .size(size + 8.dp)
            .semantics { role = Role.Checkbox },
    ) {
        Icon(
            imageVector        = if (isLoved) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
            contentDescription = contentDescription,
            tint               = if (isLoved) activeColor else inactiveColor,
            modifier           = Modifier
                .size(size)
                .scale(scale),
        )
    }
}

/**
 * Generic Melotape icon button with required content description for accessibility.
 */
@Composable
fun MelotapeIconButton(
    icon: @Composable () -> Unit,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 40.dp,
    enabled: Boolean = true,
) {
    IconButton(
        onClick  = onClick,
        enabled  = enabled,
        modifier = modifier.size(size),
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(size),
        ) {
            icon()
        }
    }
}

@Preview(name = "PlayPauseButton — all sizes")
@Composable
private fun PreviewPlayPause() {
    MelotapeTheme {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment     = Alignment.CenterVertically,
            modifier              = Modifier.padding(16.dp),
        ) {
            PlayPauseButton(false, {}, "Play", size = PlayButtonSize.Small)
            PlayPauseButton(true,  {}, "Pause", size = PlayButtonSize.Medium)
            PlayPauseButton(false, {}, "Play", size = PlayButtonSize.Large)
        }
    }
}

@Preview(name = "HeartButton — loved / not loved")
@Composable
private fun PreviewHeart() {
    MelotapeTheme {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(16.dp),
        ) {
            HeartButton(isLoved = false, onToggle = {})
            HeartButton(isLoved = true, onToggle = {})
        }
    }
}

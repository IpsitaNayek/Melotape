package com.melotape.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.MusicOff
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.melotape.ui.theme.*

/**
 * Loading state — used in every list/network screen while data is fetching.
 */
@Composable
fun LoadingState(
    modifier: Modifier = Modifier,
    message: String = "Loading…",
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 64.dp),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            CircularProgressIndicator(color = PrimaryAccent)
            Text(
                text      = message,
                style     = MaterialTheme.typography.bodyMedium,
                color     = TextSecondary,
                textAlign = TextAlign.Center,
            )
        }
    }
}

/**
 * Empty state — customizable icon, title, subtitle.
 */
@Composable
fun EmptyState(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    message: String? = null,
    icon: ImageVector = Icons.Filled.MusicOff,
    actionText: String? = null,
    onAction: (() -> Unit)? = null,
) {
    val descriptionText = message ?: subtitle
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 64.dp),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(horizontal = 32.dp),
        ) {
            Icon(
                imageVector        = icon,
                contentDescription = null,
                tint               = TextDisabled,
                modifier           = Modifier.size(56.dp),
            )
            Text(
                text      = title,
                style     = MaterialTheme.typography.titleMedium,
                color     = TextPrimary,
                textAlign = TextAlign.Center,
            )
            if (descriptionText != null) {
                Text(
                    text      = descriptionText,
                    style     = MaterialTheme.typography.bodyMedium,
                    color     = TextSecondary,
                    textAlign = TextAlign.Center,
                )
            }
            if (actionText != null && onAction != null) {
                Spacer(Modifier.height(4.dp))
                PrimaryButton(text = actionText, onClick = onAction)
            }
        }
    }
}

/**
 * Error state — with optional Retry action.
 */
@Composable
fun ErrorState(
    message: String,
    modifier: Modifier = Modifier,
    onRetry: (() -> Unit)? = null,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 64.dp),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(horizontal = 32.dp),
        ) {
            Icon(
                imageVector        = Icons.Filled.CloudOff,
                contentDescription = null,
                tint               = StatusRed,
                modifier           = Modifier.size(48.dp),
            )
            Text(
                text      = message,
                style     = MaterialTheme.typography.bodyMedium,
                color     = TextSecondary,
                textAlign = TextAlign.Center,
            )
            if (onRetry != null) {
                SecondaryButton(
                    text  = "Retry",
                    icon  = Icons.Filled.Refresh,
                    onClick = onRetry,
                )
            }
        }
    }
}

/**
 * Non-blocking error banner (e.g. "Couldn't reach Jamendo • Retry")
 */
@Composable
fun ErrorBanner(
    message: String,
    modifier: Modifier = Modifier,
    onRetry: (() -> Unit)? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment     = Alignment.CenterVertically,
    ) {
        Text(
            text     = message,
            style    = MaterialTheme.typography.bodySmall,
            color    = StatusRed,
            modifier = Modifier.weight(1f),
        )
        if (onRetry != null) {
            androidx.compose.material3.TextButton(onClick = onRetry) {
                Text("Retry", color = PrimaryAccent, style = MaterialTheme.typography.labelMedium)
            }
        }
    }
}

@Preview(name = "States — Loading / Empty / Error")
@Composable
private fun PreviewStates() {
    MelotapeTheme {
        Column {
            LoadingState()
            EmptyState(
                title    = "No songs here yet",
                subtitle = "Add some music to get started",
                icon     = Icons.Filled.MusicOff,
                actionText = "Browse Jamendo",
                onAction = {},
            )
            ErrorState(
                message  = "Couldn't load your library. Check your connection.",
                onRetry  = {},
            )
            ErrorBanner(
                message  = "Couldn't reach Jamendo",
                onRetry  = {},
            )
        }
    }
}

package com.melotape.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.melotape.ui.model.SongItemUi
import com.melotape.ui.theme.*

/**
 * Bottom Sheet displaying full analog & digital track specs.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SongDetailsBottomSheet(
    song: SongItemUi?,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (song == null) return

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = SurfaceElevated,
        dragHandle = {
            BottomSheetDefaults.DragHandle(color = TextSecondary)
        },
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .padding(bottom = 24.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "ANALOG SPECS & MASTER CHAIN",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                )
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = TextSecondary,
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            // Song row summary
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
            ) {
                ArtworkImage(
                    model = song.artwork,
                    modifier = Modifier
                        .size(54.dp)
                        .clip(RoundedCornerShape(8.dp)),
                )
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = song.title,
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary,
                    )
                    Text(
                        text = song.subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            HorizontalDivider(color = Surface)

            Spacer(Modifier.height(12.dp))

            // Specs grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                StatTile(
                    label = "ENCODING",
                    value = song.formatLabel ?: "FLAC 24",
                    modifier = Modifier.weight(1f),
                    emphasis = true,
                )
                StatTile(
                    label = "HEADROOM",
                    value = "+3.2 dB VU",
                    modifier = Modifier.weight(1f),
                )
                StatTile(
                    label = "WARMTH",
                    value = "Type II Bias",
                    modifier = Modifier.weight(1f),
                )
            }

            Spacer(Modifier.height(12.dp))

            // Metadata rows
            SettingsRow(
                title = "Source Audio Deck",
                subtitle = when (song.source) {
                    com.melotape.ui.model.SourceBadgeUi.Device -> "Local MediaStore (Device Flash)"
                    com.melotape.ui.model.SourceBadgeUi.Jamendo -> "Jamendo Music Commons (Streamed)"
                    com.melotape.ui.model.SourceBadgeUi.Cloud -> "Melotape Cloud Vault (Storage)"
                    com.melotape.ui.model.SourceBadgeUi.Downloaded -> "Offline Cassette Cache (Complete)"
                    null -> "Direct Audio Stream"
                },
                onClick = {},
            )

            SettingsRow(
                title = "Noise Reduction",
                subtitle = "Dolby B-Type NR active (Hiss shelf filter)",
                onClick = {},
            )
        }
    }
}

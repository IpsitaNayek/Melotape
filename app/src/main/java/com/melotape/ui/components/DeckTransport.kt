package com.melotape.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.FiberManualRecord
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.melotape.ui.theme.*

/**
 * Deck Master Calibration Transport controls.
 * Features Rewind, Stop, Play/Pause, Fast Forward, and Record button.
 */
@Composable
fun DeckTransport(
    isPlaying: Boolean,
    onRewind: () -> Unit,
    onStop: () -> Unit,
    onPlayPause: () -> Unit,
    onFastForward: () -> Unit,
    onRecord: () -> Unit,
    modifier: Modifier = Modifier,
    isRecording: Boolean = false,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Surface)
            .border(1.dp, SurfaceElevated, RoundedCornerShape(12.dp))
            .padding(horizontal = 16.dp, vertical = 12.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "DECK MASTER CALIBRATION",
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary,
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(if (isPlaying) Color(0xFF4ADE80) else TextSecondary),
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = if (isPlaying) "4.76 cm/s • ACTIVE" else "STANDBY",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isPlaying) Color(0xFF4ADE80) else TextSecondary,
                )
            }
        }

        Spacer(Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Rewind
            IconButton(
                onClick = onRewind,
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(SurfaceElevated),
            ) {
                Icon(
                    imageVector = Icons.Default.FastRewind,
                    contentDescription = "Rewind",
                    tint = TextPrimary,
                )
            }

            // Stop
            IconButton(
                onClick = onStop,
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(SurfaceElevated),
            ) {
                Icon(
                    imageVector = Icons.Default.Stop,
                    contentDescription = "Stop",
                    tint = TextPrimary,
                )
            }

            // Play / Pause (Main Accent button)
            IconButton(
                onClick = onPlayPause,
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(PrimaryAccent),
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (isPlaying) "Pause" else "Play",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp),
                )
            }

            // Fast Forward
            IconButton(
                onClick = onFastForward,
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(SurfaceElevated),
            ) {
                Icon(
                    imageVector = Icons.Default.FastForward,
                    contentDescription = "Fast Forward",
                    tint = TextPrimary,
                )
            }

            // Rec Button (Vintage Red dot)
            IconButton(
                onClick = onRecord,
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isRecording) Burgundy else SurfaceElevated)
                    .border(
                        width = 1.dp,
                        color = if (isRecording) Color.Red else Color.Transparent,
                        shape = RoundedCornerShape(8.dp),
                    ),
            ) {
                Icon(
                    imageVector = Icons.Default.FiberManualRecord,
                    contentDescription = "Record",
                    tint = if (isRecording) Color.Red else Color(0xFFEF4444),
                    modifier = Modifier.size(18.dp),
                )
            }
        }
    }
}

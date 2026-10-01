package com.melotape.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.melotape.ui.theme.*

/**
 * Dashed Drop Zone for Upload & Tape Rip in Profile and Upload flow.
 */
@Composable
fun DashedDropZone(
    title: String = "Drag & Drop Audio Files",
    subtitle: String = "Supports FLAC, MP3, WAV • High-Bias Tape Calibration",
    buttonText: String = "Select Audio File",
    onSelectFile: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(160.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF0F1B27)),
        contentAlignment = Alignment.Center,
    ) {
        // Dashed border drawn via Canvas
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawRoundRect(
                color = Color(0xFF334B66),
                size = size,
                cornerRadius = CornerRadius(12.dp.toPx(), 12.dp.toPx()),
                style = Stroke(
                    width = 2.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(16f, 16f), 0f),
                ),
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(16.dp),
        ) {
            Icon(
                imageVector = Icons.Default.CloudUpload,
                contentDescription = null,
                tint = Amber,
                modifier = Modifier.size(36.dp),
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary,
            )

            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary,
            )

            Spacer(Modifier.height(10.dp))

            Button(
                onClick = onSelectFile,
                colors = ButtonDefaults.buttonColors(containerColor = Amber),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                modifier = Modifier.height(34.dp),
            ) {
                Text(
                    text = buttonText,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF1E293B),
                )
            }
        }
    }
}

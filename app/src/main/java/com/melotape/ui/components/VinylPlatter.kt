package com.melotape.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.melotape.ui.theme.*

/**
 * Vinyl / Turntable platter with concentric grooves, center label with album art,
 * and mechanical tonearm resting on the record.
 */
@Composable
fun VinylPlatter(
    artwork: Any?,
    isPlaying: Boolean,
    modifier: Modifier = Modifier,
    size: Dp = 260.dp,
) {
    // Rotation animation when playing
    val infiniteTransition = rememberInfiniteTransition(label = "VinylSpin")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "VinylRotation",
    )

    val currentRotation = if (isPlaying) rotation else 0f

    Box(
        modifier = modifier
            .size(size)
            .padding(8.dp),
        contentAlignment = Alignment.Center,
    ) {
        // 1. Vinyl Record Disc (Grooves and outer rim)
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.toPx() / 2f, size.toPx() / 2f)
            val outerRadius = (size.toPx() / 2f) - 4f

            // Outer dark vinyl rim
            drawCircle(
                color = Color(0xFF10141A),
                radius = outerRadius,
                center = center,
            )

            // Outer bevel
            drawCircle(
                color = Color(0xFF252D38),
                radius = outerRadius,
                center = center,
                style = Stroke(width = 2f),
            )

            // Concentric vinyl sound grooves
            val grooveRadii = listOf(
                outerRadius * 0.92f, outerRadius * 0.88f, outerRadius * 0.84f,
                outerRadius * 0.80f, outerRadius * 0.74f, outerRadius * 0.68f,
                outerRadius * 0.62f, outerRadius * 0.56f, outerRadius * 0.50f
            )

            for (r in grooveRadii) {
                drawCircle(
                    color = Color(0x18FFFFFF),
                    radius = r,
                    center = center,
                    style = Stroke(width = 1f),
                )
            }

            // Lead-in and lead-out groove bands
            drawCircle(
                color = Color(0x22FFFFFF),
                radius = outerRadius * 0.44f,
                center = center,
                style = Stroke(width = 3f),
            )
        }

        // 2. Center Record Label with artwork (rotates when playing)
        Box(
            modifier = Modifier
                .size(size * 0.44f)
                .rotate(currentRotation)
                .clip(CircleShape)
                .background(Cream)
                .border(2.dp, Amber, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            ArtworkImage(
                model = artwork,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape),
            )

            // Center spindle hole
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .clip(CircleShape)
                    .background(Background)
                    .border(1.5.dp, Color(0xFFC4B89D), CircleShape),
            )
        }

        // 3. Tonearm resting on platter
        Tonearm(
            isPlaying = isPlaying,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = 12.dp, y = (-8).dp),
        )
    }
}

/**
 * Mechanical tonearm assembly with pivot base, curved arm, and cartridge head.
 */
@Composable
fun Tonearm(
    isPlaying: Boolean,
    modifier: Modifier = Modifier,
) {
    val armAngle = if (isPlaying) 22f else 5f

    Canvas(
        modifier = modifier
            .size(width = 80.dp, height = 140.dp)
            .rotate(armAngle),
    ) {
        val pivotCenter = Offset(60f, 15f)

        // Pivot base mount
        drawCircle(
            color = Color(0xFF4A5568),
            radius = 16f,
            center = pivotCenter,
        )
        drawCircle(
            color = Amber,
            radius = 8f,
            center = pivotCenter,
        )

        // Counterweight
        drawRoundRect(
            color = Color(0xFF2D3748),
            topLeft = Offset(50f, 2f),
            size = androidx.compose.ui.geometry.Size(20f, 12f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f, 4f),
        )

        // Metal Tonearm shaft
        drawLine(
            brush = Brush.verticalGradient(listOf(Color(0xFFCBD5E1), Color(0xFF64748B))),
            start = pivotCenter,
            end = Offset(20f, 110f),
            strokeWidth = 4f,
        )

        // Cartridge / Headshell
        drawRoundRect(
            color = PrimaryAccent,
            topLeft = Offset(12f, 108f),
            size = androidx.compose.ui.geometry.Size(16f, 24f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(3f, 3f),
        )

        // Stylus needle tip
        drawCircle(
            color = Color.White,
            radius = 2.5f,
            center = Offset(20f, 134f),
        )
    }
}

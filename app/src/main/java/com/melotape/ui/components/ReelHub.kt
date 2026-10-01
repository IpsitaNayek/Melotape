package com.melotape.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.melotape.ui.theme.Amber
import kotlin.math.cos
import kotlin.math.sin

/**
 * Skeuomorphic Cassette Reel Hub with mechanical cog teeth/spokes that rotates when playing.
 */
@Composable
fun ReelHub(
    isSpinning: Boolean,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    spokeCount: Int = 6,
    hubColor: Color = Color.White,
    centerColor: Color = Color(0xFF14202E),
) {
    val infiniteTransition = rememberInfiniteTransition(label = "ReelSpin")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "ReelRotation",
    )

    val currentRotation = if (isSpinning) rotation else 0f

    Box(
        modifier = modifier
            .size(size)
            .rotate(currentRotation),
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val center = Offset(size.toPx() / 2f, size.toPx() / 2f)
            val outerRadius = size.toPx() / 2f - 2f
            val innerRadius = outerRadius * 0.45f
            val hubRadius = outerRadius * 0.25f

            // Outer white plastic hub rim
            drawCircle(
                color = hubColor.copy(alpha = 0.9f),
                radius = outerRadius,
                center = center,
                style = Stroke(width = 3f),
            )

            // Spoke teeth connecting outer hub to center spindle
            val angleStep = (2 * Math.PI / spokeCount).toFloat()
            for (i in 0 until spokeCount) {
                val angle = i * angleStep
                val startX = center.x + hubRadius * cos(angle)
                val startY = center.y + hubRadius * sin(angle)
                val endX = center.x + outerRadius * cos(angle)
                val endY = center.y + outerRadius * sin(angle)

                drawLine(
                    color = hubColor.copy(alpha = 0.95f),
                    start = Offset(startX, startY),
                    end = Offset(endX, endY),
                    strokeWidth = 3.5f,
                )

                // Reel cog teeth notches
                val notchAngle = angle + angleStep / 2f
                val notchRadius = (innerRadius + outerRadius) / 2f
                drawCircle(
                    color = Amber.copy(alpha = 0.8f),
                    radius = 2.5f,
                    center = Offset(
                        center.x + notchRadius * cos(notchAngle),
                        center.y + notchRadius * sin(notchAngle),
                    ),
                )
            }

            // Center spindle ring & hole
            drawCircle(
                color = hubColor,
                radius = innerRadius,
                center = center,
                style = Stroke(width = 2.5f),
            )

            drawCircle(
                color = centerColor,
                radius = hubRadius,
                center = center,
            )
        }
    }
}

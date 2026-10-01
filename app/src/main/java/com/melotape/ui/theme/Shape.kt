package com.melotape.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val MelotapeShapes = Shapes(
    // Small — chips, small badges, format tags
    extraSmall = RoundedCornerShape(4.dp),
    small      = RoundedCornerShape(8.dp),
    // Medium — rows, cards, buttons
    medium     = RoundedCornerShape(12.dp),
    // Large — bottom sheets, large cards, featured card
    large      = RoundedCornerShape(16.dp),
    // Extra Large — full-bleed modal sheets
    extraLarge = RoundedCornerShape(20.dp),
)

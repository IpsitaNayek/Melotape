package com.melotape.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Spacing tokens for Melotape. Use these instead of hard-coded dp values.
 * Access via MelotapeTheme.spacing.md
 */
data class Spacing(
    val none    : Dp = 0.dp,
    val xs      : Dp = 4.dp,
    val sm      : Dp = 8.dp,
    val md      : Dp = 12.dp,
    val lg      : Dp = 16.dp,
    val xl      : Dp = 20.dp,
    val xxl     : Dp = 24.dp,
    val xxxl    : Dp = 32.dp,
    val huge    : Dp = 48.dp,
    val gutter  : Dp = 16.dp,   // horizontal screen gutter
    val cardPad : Dp = 16.dp,   // padding inside cards
    val rowHeight: Dp = 64.dp,  // standard row height
    val chipH   : Dp = 24.dp,   // chip height
    val bottomNavH: Dp = 56.dp, // bottom nav height
    val miniPlayerH: Dp = 72.dp, // mini player height
    val topBarH  : Dp = 56.dp,  // top app bar height
    val touchTarget: Dp = 48.dp, // minimum touch target (a11y)
)

val LocalSpacing = staticCompositionLocalOf { Spacing() }

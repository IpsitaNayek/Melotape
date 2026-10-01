package com.melotape.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// === Font Families — using system defaults for Phase 1 ===
// Phase 2+ can swap these for Space Grotesk / Space Mono
val SansSerif  = FontFamily.Default   // → can be replaced with SpaceGrotesk
val Monospace  = FontFamily.Monospace  // → can be replaced with SpaceMono

val MelotapeTypography = Typography(
    // Display — large featured titles
    displayLarge = TextStyle(
        fontFamily   = SansSerif,
        fontWeight   = FontWeight.Bold,
        fontSize     = 36.sp,
        lineHeight   = 40.sp,
        letterSpacing = (-0.5).sp,
    ),
    displayMedium = TextStyle(
        fontFamily   = SansSerif,
        fontWeight   = FontWeight.SemiBold,
        fontSize     = 28.sp,
        lineHeight   = 34.sp,
        letterSpacing = (-0.3).sp,
    ),
    displaySmall = TextStyle(
        fontFamily   = SansSerif,
        fontWeight   = FontWeight.SemiBold,
        fontSize     = 22.sp,
        lineHeight   = 28.sp,
        letterSpacing = 0.sp,
    ),
    // Headline — screen titles, section headers
    headlineLarge = TextStyle(
        fontFamily   = SansSerif,
        fontWeight   = FontWeight.Bold,
        fontSize     = 20.sp,
        lineHeight   = 26.sp,
        letterSpacing = 0.sp,
    ),
    headlineMedium = TextStyle(
        fontFamily   = SansSerif,
        fontWeight   = FontWeight.SemiBold,
        fontSize     = 17.sp,
        lineHeight   = 22.sp,
        letterSpacing = 0.sp,
    ),
    headlineSmall = TextStyle(
        fontFamily   = SansSerif,
        fontWeight   = FontWeight.Medium,
        fontSize     = 15.sp,
        lineHeight   = 20.sp,
        letterSpacing = 0.sp,
    ),
    // Title — card titles, song titles
    titleLarge = TextStyle(
        fontFamily   = SansSerif,
        fontWeight   = FontWeight.SemiBold,
        fontSize     = 16.sp,
        lineHeight   = 22.sp,
        letterSpacing = 0.sp,
    ),
    titleMedium = TextStyle(
        fontFamily   = SansSerif,
        fontWeight   = FontWeight.Medium,
        fontSize     = 14.sp,
        lineHeight   = 20.sp,
        letterSpacing = 0.1.sp,
    ),
    titleSmall = TextStyle(
        fontFamily   = SansSerif,
        fontWeight   = FontWeight.Medium,
        fontSize     = 12.sp,
        lineHeight   = 16.sp,
        letterSpacing = 0.1.sp,
    ),
    // Body — row subtitles, descriptions
    bodyLarge = TextStyle(
        fontFamily   = SansSerif,
        fontWeight   = FontWeight.Normal,
        fontSize     = 15.sp,
        lineHeight   = 22.sp,
        letterSpacing = 0.15.sp,
    ),
    bodyMedium = TextStyle(
        fontFamily   = SansSerif,
        fontWeight   = FontWeight.Normal,
        fontSize     = 13.sp,
        lineHeight   = 18.sp,
        letterSpacing = 0.15.sp,
    ),
    bodySmall = TextStyle(
        fontFamily   = SansSerif,
        fontWeight   = FontWeight.Normal,
        fontSize     = 11.sp,
        lineHeight   = 16.sp,
        letterSpacing = 0.1.sp,
    ),
    // Label — chips, tags, counters (monospace for tape aesthetic)
    labelLarge = TextStyle(
        fontFamily   = Monospace,
        fontWeight   = FontWeight.Bold,
        fontSize     = 12.sp,
        lineHeight   = 16.sp,
        letterSpacing = 0.5.sp,
    ),
    labelMedium = TextStyle(
        fontFamily   = Monospace,
        fontWeight   = FontWeight.Normal,
        fontSize     = 10.sp,
        lineHeight   = 14.sp,
        letterSpacing = 0.5.sp,
    ),
    labelSmall = TextStyle(
        fontFamily   = Monospace,
        fontWeight   = FontWeight.Normal,
        fontSize     = 9.sp,
        lineHeight   = 12.sp,
        letterSpacing = 0.6.sp,
    ),
)
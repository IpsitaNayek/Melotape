package com.melotape.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

private val MelotapeDarkColorScheme = darkColorScheme(
    primary           = PrimaryAccent,
    onPrimary         = Cream,
    primaryContainer  = Burgundy,
    onPrimaryContainer= Cream,
    secondary         = Amber,
    onSecondary       = SurfaceVariant,
    secondaryContainer= SurfaceElevated,
    onSecondaryContainer = TextPrimary,
    tertiary          = StatusGreen,
    onTertiary        = SurfaceVariant,
    background        = Background,
    onBackground      = TextPrimary,
    surface           = Surface,
    onSurface         = TextPrimary,
    surfaceVariant    = SurfaceElevated,
    onSurfaceVariant  = TextSecondary,
    error             = StatusRed,
    onError           = Cream,
    outline           = TextDisabled,
    outlineVariant    = SurfaceElevated,
    scrim             = SurfaceVariant,
    inverseSurface    = Cream,
    inverseOnSurface  = Background,
    inversePrimary    = PrimaryAccentDark,
)

// Expose extended colors not in Material's scheme
data class MelotapeExtendedColors(
    val cream: Color,
    val amber: Color,
    val burgundy: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textDisabled: Color,
    val statusGreen: Color,
    val reelHub: Color,
    val reelSpoke: Color,
    val platterBase: Color,
    val tonearm: Color,
    val cassetteLabelBg: Color,
    val cassetteLabelText: Color,
    val vaultRed: Color,
    val vaultBlue: Color,
    val vaultCream: Color,
    val vaultTeal: Color,
    val vaultPurple: Color,
    val vaultAmber: Color,
)

val LocalMelotapeColors = staticCompositionLocalOf {
    MelotapeExtendedColors(
        cream             = Cream,
        amber             = Amber,
        burgundy          = Burgundy,
        textPrimary       = TextPrimary,
        textSecondary     = TextSecondary,
        textDisabled      = TextDisabled,
        statusGreen       = StatusGreen,
        reelHub           = ReelHubColor,
        reelSpoke         = ReelSpokeColor,
        platterBase       = PlatterBase,
        tonearm           = TonearmColor,
        cassetteLabelBg   = CassetteLabelBg,
        cassetteLabelText = CassetteLabelText,
        vaultRed          = VaultRed,
        vaultBlue         = VaultBlue,
        vaultCream        = VaultCream,
        vaultTeal         = VaultTeal,
        vaultPurple       = VaultPurple,
        vaultAmber        = VaultAmberColor,
    )
}

// Convenient accessor: MelotapeTheme.colors.cream
object MelotapeTheme {
    val colors: MelotapeExtendedColors
        @Composable get() = LocalMelotapeColors.current
    val spacing: Spacing
        @Composable get() = LocalSpacing.current
}

@Composable
fun MelotapeTheme(
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(
        LocalMelotapeColors provides MelotapeExtendedColors(
            cream             = Cream,
            amber             = Amber,
            burgundy          = Burgundy,
            textPrimary       = TextPrimary,
            textSecondary     = TextSecondary,
            textDisabled      = TextDisabled,
            statusGreen       = StatusGreen,
            reelHub           = ReelHubColor,
            reelSpoke         = ReelSpokeColor,
            platterBase       = PlatterBase,
            tonearm           = TonearmColor,
            cassetteLabelBg   = CassetteLabelBg,
            cassetteLabelText = CassetteLabelText,
            vaultRed          = VaultRed,
            vaultBlue         = VaultBlue,
            vaultCream        = VaultCream,
            vaultTeal         = VaultTeal,
            vaultPurple       = VaultPurple,
            vaultAmber        = VaultAmberColor,
        ),
        LocalSpacing provides Spacing(),
    ) {
        MaterialTheme(
            colorScheme = MelotapeDarkColorScheme,
            typography  = MelotapeTypography,
            shapes      = MelotapeShapes,
            content     = content,
        )
    }
}
package com.melotape.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.melotape.ui.theme.*

/**
 * Primary filled button — salmon / PrimaryAccent color.
 * Used for main actions: Play Deck, Play All, Sign In, etc.
 */
@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    enabled: Boolean = true,
) {
    Button(
        onClick  = onClick,
        enabled  = enabled,
        modifier = modifier.heightIn(min = 44.dp),
        colors   = ButtonDefaults.buttonColors(
            containerColor         = PrimaryAccent,
            contentColor           = Color.White,
            disabledContainerColor = SurfaceElevated,
            disabledContentColor   = TextDisabled,
        ),
        shape = MaterialTheme.shapes.medium,
    ) {
        if (icon != null) {
            Icon(
                imageVector        = icon,
                contentDescription = null,
                modifier           = Modifier.size(18.dp),
            )
            Spacer(Modifier.width(6.dp))
        }
        Text(text = text, style = MaterialTheme.typography.titleSmall)
    }
}

/**
 * Secondary outlined/tonal button — used for secondary actions: Shuffle, Manage Downloads, etc.
 */
@Composable
fun SecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    enabled: Boolean = true,
) {
    OutlinedButton(
        onClick  = onClick,
        enabled  = enabled,
        modifier = modifier.heightIn(min = 44.dp),
        colors   = ButtonDefaults.outlinedButtonColors(
            contentColor           = TextPrimary,
            disabledContentColor   = TextDisabled,
        ),
        border = ButtonDefaults.outlinedButtonBorder(enabled = enabled).copy(
            brush = androidx.compose.ui.graphics.SolidColor(if (enabled) SurfaceElevated else TextDisabled),
        ),
        shape = MaterialTheme.shapes.medium,
    ) {
        if (icon != null) {
            Icon(
                imageVector        = icon,
                contentDescription = null,
                modifier           = Modifier.size(18.dp),
            )
            Spacer(Modifier.width(6.dp))
        }
        Text(text = text, style = MaterialTheme.typography.titleSmall)
    }
}

/**
 * Danger / destructive button — burgundy / red.
 * Used for: Clear Cache, Delete Account, Remove Download.
 */
@Composable
fun DangerButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    enabled: Boolean = true,
) {
    Button(
        onClick  = onClick,
        enabled  = enabled,
        modifier = modifier.heightIn(min = 44.dp),
        colors   = ButtonDefaults.buttonColors(
            containerColor         = Burgundy,
            contentColor           = Cream,
            disabledContainerColor = SurfaceElevated,
            disabledContentColor   = TextDisabled,
        ),
        shape = MaterialTheme.shapes.medium,
    ) {
        if (icon != null) {
            Icon(
                imageVector        = icon,
                contentDescription = null,
                modifier           = Modifier.size(18.dp),
            )
            Spacer(Modifier.width(6.dp))
        }
        Text(text = text, style = MaterialTheme.typography.titleSmall)
    }
}

@Preview(name = "All button types")
@Composable
private fun PreviewButtons() {
    MelotapeTheme {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(16.dp),
        ) {
            PrimaryButton("▶ Play Deck", onClick = {})
            PrimaryButton("Add to Playlist", icon = Icons.Filled.Add, onClick = {})
            SecondaryButton("⇄ Shuffle", onClick = {})
            DangerButton("Clear Cache", onClick = {})
            PrimaryButton("Disabled", onClick = {}, enabled = false)
        }
    }
}

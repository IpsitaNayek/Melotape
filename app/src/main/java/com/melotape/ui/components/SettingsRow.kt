package com.melotape.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.melotape.ui.theme.*

/**
 * Settings / config row — icon + title + subtitle + optional trailing composable.
 * Used in Profile Deck Configuration and Settings sub-screens.
 */
@Composable
fun SettingsRow(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailing: @Composable (RowScope.() -> Unit)? = null,
    onClick: (() -> Unit)? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (leadingIcon != null) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(32.dp)
                    .padding(end = 4.dp),
            ) {
                leadingIcon()
            }
            Spacer(Modifier.width(12.dp))
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(
                text  = title,
                style = MaterialTheme.typography.titleSmall,
                color = TextPrimary,
            )
            if (subtitle != null) {
                Text(
                    text  = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                )
            }
        }

        if (trailing != null) {
            Spacer(Modifier.width(8.dp))
            trailing()
        }
    }
}

/**
 * Settings row with a toggle switch.
 */
@Composable
fun SettingsToggleRow(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    stateDescription: String = if (checked) "Enabled" else "Disabled",
) {
    SettingsRow(
        title      = title,
        subtitle   = subtitle,
        leadingIcon = leadingIcon,
        onClick    = { onCheckedChange(!checked) },
        modifier   = modifier.semantics {
            role = Role.Switch
            this.stateDescription = stateDescription
        },
        trailing   = {
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor  = Color.White,
                    checkedTrackColor  = PrimaryAccent,
                    uncheckedThumbColor = TextSecondary,
                    uncheckedTrackColor = SurfaceElevated,
                ),
            )
        },
    )
}

@Preview(name = "SettingsRow")
@Composable
private fun PreviewSettingsRow() {
    MelotapeTheme {
        Column(Modifier.padding(8.dp)) {
            SettingsRow(
                title    = "Audio Quality",
                subtitle = "Master FLAC 96kHz / 24-Bit",
                onClick  = {},
            )
            SettingsToggleRow(
                title           = "Dolby B-Type NR",
                subtitle        = "Tape hiss suppression filter",
                checked         = true,
                onCheckedChange = {},
            )
            SettingsToggleRow(
                title           = "Deck Aesthetic",
                subtitle        = "Warm Retro Slate (#051423)",
                checked         = false,
                onCheckedChange = {},
            )
        }
    }
}

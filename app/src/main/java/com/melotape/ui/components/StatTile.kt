package com.melotape.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.melotape.ui.theme.*

/**
 * Stat tile — label + value, used in:
 * - Profile stats (Mixtapes, Tape Bias, Hours spun)
 * - Now Playing "Analog Specs & Master Chain" tiles (Encoding, Headroom, Warmth)
 * - Song Details sheet
 */
@Composable
fun StatTile(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    emphasis: Boolean = false,
    labelColor: Color = TextSecondary,
    valueColor: Color = TextPrimary,
) {
    Column(
        modifier = modifier
            .clip(MaterialTheme.shapes.small)
            .background(SurfaceElevated)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        Text(
            text  = label.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = labelColor,
        )
        Text(
            text  = value,
            style = if (emphasis) MaterialTheme.typography.titleMedium else MaterialTheme.typography.labelLarge,
            color = if (emphasis) PrimaryAccent else valueColor,
            maxLines = 1,
        )
    }
}

/**
 * Section header — icon + title + optional "See all" action.
 * Used in Home and Search.
 */
@Composable
fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    leadingIcon: @Composable (() -> Unit)? = null,
    actionText: String? = null,
    onAction: (() -> Unit)? = null,
) {
    Row(
        modifier          = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (leadingIcon != null) {
            leadingIcon()
            Spacer(Modifier.width(6.dp))
        }
        Text(
            text     = title,
            style    = MaterialTheme.typography.titleMedium,
            color    = TextPrimary,
            modifier = Modifier.weight(1f),
        )
        if (actionText != null && onAction != null) {
            androidx.compose.material3.TextButton(onClick = onAction) {
                Text(
                    text  = actionText,
                    style = MaterialTheme.typography.labelMedium,
                    color = PrimaryAccent,
                )
            }
        }
    }
}

@Preview(name = "StatTile — row of three")
@Composable
private fun PreviewStatTile() {
    MelotapeTheme {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(16.dp)
        ) {
            StatTile("MIXTAPES", "28")
            StatTile("TAPE BIAS", "Type II", emphasis = true)
            StatTile("HOURS SPUN", "342h")
        }
    }
}

@Preview(name = "SectionHeader")
@Composable
private fun PreviewSectionHeader() {
    MelotapeTheme {
        SectionHeader(
            title      = "Recently Played",
            actionText = "See all",
            onAction   = {},
        )
    }
}

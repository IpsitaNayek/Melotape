package com.melotape.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.melotape.ui.theme.MelotapeTheme
import com.melotape.ui.theme.PrimaryAccent
import com.melotape.ui.theme.SurfaceElevated
import com.melotape.ui.theme.TextPrimary
import com.melotape.ui.theme.TextSecondary

/**
 * Tape-style chip used throughout Melotape:
 *   - Bias type chips: "TYPE I • NORMAL BIAS"
 *   - Format chips: "FLAC 24", "320K"
 *   - Side chips: "SIDE A"
 *   - Status chips: "NOW", "OFFLINE"
 *
 * Single-line with ellipsis — never wraps.
 */
@Composable
fun TapeChip(
    text: String,
    modifier: Modifier = Modifier,
    leadingDot: Boolean = false,
    dotColor: Color = PrimaryAccent,
    containerColor: Color = SurfaceElevated,
    contentColor: Color = TextPrimary,
    style: ChipStyle = ChipStyle.Default,
    tone: ChipStyle = style,
) {
    val activeStyle = if (tone != ChipStyle.Default) tone else style
    val bgColor = when (activeStyle) {
        ChipStyle.Default   -> containerColor
        ChipStyle.Surface   -> SurfaceElevated
        ChipStyle.Accent    -> PrimaryAccent
        ChipStyle.Burgundy  -> MaterialTheme.colorScheme.primaryContainer
        ChipStyle.Amber     -> MaterialTheme.colorScheme.secondary
        ChipStyle.Outline   -> Color.Transparent
    }
    val textColor = when (activeStyle) {
        ChipStyle.Accent, ChipStyle.Burgundy, ChipStyle.Amber -> MaterialTheme.colorScheme.onPrimary
        else -> contentColor
    }

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(bgColor)
            .padding(horizontal = 6.dp, vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        if (leadingDot) {
            Box(
                modifier = Modifier
                    .size(5.dp)
                    .clip(RoundedCornerShape(50))
                    .background(dotColor)
            )
        }
        Text(
            text       = text,
            style      = MaterialTheme.typography.labelMedium,
            color      = textColor,
            maxLines   = 1,
            softWrap   = false,
            overflow   = TextOverflow.Ellipsis,
        )
    }
}

enum class ChipStyle { Default, Accent, Burgundy, Amber, Outline, Surface }
typealias ChipTone = ChipStyle

@Preview(name = "TapeChip — Default")
@Composable
private fun PreviewTapeChipDefault() {
    MelotapeTheme {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(16.dp)
        ) {
            TapeChip("TYPE I • NORMAL BIAS")
            TapeChip("FLAC 24")
            TapeChip("SIDE A", leadingDot = true)
            TapeChip("NOW", style = ChipStyle.Accent)
            TapeChip("OFFLINE", style = ChipStyle.Amber)
        }
    }
}

@Preview(name = "TapeChip — Long text")
@Composable
private fun PreviewTapeChipLong() {
    MelotapeTheme {
        TapeChip(
            text = "VERY LONG CHIP TEXT THAT SHOULD ELLIPSIS",
            modifier = Modifier
                .widthIn(max = 120.dp)
                .padding(16.dp)
        )
    }
}

package com.melotape.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.melotape.ui.theme.*

/**
 * Skeuomorphic Cassette Tape Header for the Album Detail / Song List screen.
 * Displays tape specifications, dual spinning reel hubs, tape progress window,
 * and Play All / Shuffle actions.
 */
@Composable
fun CassetteHeader(
    title: String,
    sideLabel: String,
    tapeType: String,
    trackCountInfo: String,
    progressText: String,
    isSpinning: Boolean,
    onPlayAll: () -> Unit,
    onShuffle: () -> Unit,
    onSort: () -> Unit,
    modifier: Modifier = Modifier,
    tapeLengthLabel: String = "C-90 STEREO",
    yearLabel: String? = "REC. 1994",
    reelLabel: String = "REEL 01",
    sideTracksLabel: String = "Side A Tracks",
    sortLabel: String = "Sequence",
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
    ) {
        // Spec Chips
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TapeChip(
                text = tapeType,
                tone = ChipTone.Amber,
                leadingDot = true,
            )
            TapeChip(
                text = trackCountInfo,
                tone = ChipTone.Surface,
                leadingDot = false,
            )
        }

        Spacer(Modifier.height(12.dp))

        // Cassette Tape Body (Chassis)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF14202E))
                .border(1.5.dp, Color(0xFF2C3E55), RoundedCornerShape(16.dp))
                .padding(14.dp),
        ) {
            Column {
                // Cassette Label Header Strip
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Cream)
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = sideLabel,
                            style = MaterialTheme.typography.labelMedium,
                            color = Burgundy,
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                        )
                        if (!yearLabel.isNullOrBlank()) {
                            Text(
                                text = yearLabel,
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF64748B),
                                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                            )
                        }
                        Text(
                            text = tapeLengthLabel,
                            style = MaterialTheme.typography.labelMedium,
                            color = Burgundy,
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                        )
                    }

                    Spacer(Modifier.height(4.dp))

                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge,
                        color = Color(0xFF1E293B),
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                    )
                }

                Spacer(Modifier.height(14.dp))


                // Tape Window with Dual Reel Hubs & Tape Bridge
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(84.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF0A1017))
                        .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(10.dp))
                        .padding(horizontal = 24.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        // Left Reel Hub
                        ReelHub(
                            isSpinning = isSpinning,
                            size = 52.dp,
                            spokeCount = 6,
                        )

                        // Center Tape Window with Progress Indicator
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Text(
                                text = progressText,
                                style = MaterialTheme.typography.labelSmall,
                                color = Amber,
                            )
                            Spacer(Modifier.height(4.dp))
                            // Tape strip simulation bar
                            Box(
                                modifier = Modifier
                                    .width(72.dp)
                                    .height(5.dp)
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(Color(0xFF45241C)),
                            )
                        }

                        // Right Reel Hub
                        ReelHub(
                            isSpinning = isSpinning,
                            size = 52.dp,
                            spokeCount = 6,
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // Actions: Play All (Salmon) & Shuffle (Secondary)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Button(
                onClick = onPlayAll,
                modifier = Modifier
                    .weight(1f)
                    .height(46.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryAccent),
                shape = RoundedCornerShape(10.dp),
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp),
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = "Play All",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                )
            }

            OutlinedButton(
                onClick = onShuffle,
                modifier = Modifier
                    .weight(1f)
                    .height(46.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary),
                border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceElevated),
                shape = RoundedCornerShape(10.dp),
            ) {
                Icon(
                    imageVector = Icons.Default.Shuffle,
                    contentDescription = null,
                    tint = TextPrimary,
                    modifier = Modifier.size(18.dp),
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = "Shuffle",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        // Section Tracklist Header: "Side A Tracks" + REEL 01 chip + Sequence Sort
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = sideTracksLabel,
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                )
                Spacer(Modifier.width(8.dp))
                TapeChip(
                    text = reelLabel,
                    tone = ChipTone.Surface,
                    leadingDot = false,
                )
            }

            TextButton(onClick = onSort) {
                Icon(
                    imageVector = Icons.Default.Sort,
                    contentDescription = "Sort sequence",
                    tint = TextSecondary,
                    modifier = Modifier.size(16.dp),
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = sortLabel,
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary,
                )
            }
        }
    }
}


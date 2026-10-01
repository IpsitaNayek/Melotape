package com.melotape.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.melotape.ui.model.PlaylistUi
import com.melotape.ui.theme.*

/**
 * Bottom Sheet for adding a song to one or more user mixtapes/playlists,
 * complete with checkboxes and an inline "Create New Mixtape" action.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddToPlaylistBottomSheet(
    songTitle: String,
    playlists: List<PlaylistUi>,
    containingPlaylistIds: Set<String>,
    onTogglePlaylist: (playlistId: String, isAdded: Boolean) -> Unit,
    onCreateNewPlaylist: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = SurfaceElevated,
        dragHandle = { BottomSheetDefaults.DragHandle(color = TextSecondary) },
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .padding(bottom = 32.dp),
        ) {
            Text(
                text = "ADD TO MIXTAPE",
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary,
            )
            Text(
                text = "Dub \"$songTitle\" onto cassette reels",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                maxLines = 1,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
            )

            Spacer(Modifier.height(16.dp))

            // Inline Create New Mixtape button
            OutlinedButton(
                onClick = onCreateNewPlaylist,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp),
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Amber),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Amber),
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    tint = Amber,
                    modifier = Modifier.size(18.dp),
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Create New Mixtape",
                    style = MaterialTheme.typography.labelMedium,
                    color = Amber,
                )
            }

            Spacer(Modifier.height(16.dp))

            if (playlists.isEmpty()) {
                Text(
                    text = "No mixtapes available yet. Create your first cassette above!",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    modifier = Modifier.padding(vertical = 16.dp),
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    items(playlists, key = { it.id }) { playlist ->
                        val isChecked = containingPlaylistIds.contains(playlist.id)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onTogglePlaylist(playlist.id, !isChecked) }
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = playlist.name,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = TextPrimary,
                                )
                                Text(
                                    text = "${playlist.songCount} Tracks • ${playlist.totalDurationFormatted}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary,
                                )
                            }

                            Checkbox(
                                checked = isChecked,
                                onCheckedChange = { onTogglePlaylist(playlist.id, it) },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = PrimaryAccent,
                                    uncheckedColor = TextSecondary,
                                    checkmarkColor = androidx.compose.ui.graphics.Color.White,
                                ),
                            )
                        }
                    }
                }
            }
        }
    }
}

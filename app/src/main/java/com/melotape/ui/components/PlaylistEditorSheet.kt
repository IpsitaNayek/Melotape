package com.melotape.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.melotape.ui.theme.*

/**
 * Universal Playlist / Mixtape Editor Bottom Sheet.
 * Reusable for creating new mixtapes, renaming existing mixtapes, and inline mixtape creation.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistEditorSheet(
    onDismiss: () -> Unit,
    onConfirm: (name: String, description: String?) -> Unit,
    modifier: Modifier = Modifier,
    sheetTitle: String = "CREATE NEW MIXTAPE",
    confirmText: String = "Create Tape",
    initialName: String = "",
    initialDescription: String? = null,
) {
    var name by remember { mutableStateOf(initialName) }
    var description by remember { mutableStateOf(initialDescription ?: "") }
    var tapeLength by remember { mutableStateOf("C-60 (60 Min)") }

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
                text = sheetTitle,
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary,
            )

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Mixtape Title", color = TextSecondary) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedBorderColor = PrimaryAccent,
                    unfocusedBorderColor = Surface,
                    focusedContainerColor = Surface,
                    unfocusedContainerColor = Surface,
                ),
                shape = RoundedCornerShape(10.dp),
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Tape Liner Notes (Description)", color = TextSecondary) },
                maxLines = 3,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedBorderColor = PrimaryAccent,
                    unfocusedBorderColor = Surface,
                    focusedContainerColor = Surface,
                    unfocusedContainerColor = Surface,
                ),
                shape = RoundedCornerShape(10.dp),
            )

            Spacer(Modifier.height(16.dp))

            Text(
                text = "CASSETTE TAPE LENGTH",
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary,
            )

            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                listOf("C-46", "C-60", "C-90", "C-120").forEach { len ->
                    FilterChip(
                        selected = tapeLength.startsWith(len),
                        onClick = { tapeLength = "$len ($len Min)" },
                        label = { Text(len) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PrimaryAccent,
                            selectedLabelColor = androidx.compose.ui.graphics.Color.White,
                            containerColor = Surface,
                            labelColor = TextSecondary,
                        ),
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        onConfirm(name.trim(), description.trim().ifBlank { null })
                        onDismiss()
                    }
                },
                enabled = name.isNotBlank(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryAccent,
                    disabledContainerColor = SurfaceElevated,
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
            ) {
                Text(
                    text = confirmText,
                    style = MaterialTheme.typography.titleMedium,
                    color = androidx.compose.ui.graphics.Color.White,
                )
            }
        }
    }
}

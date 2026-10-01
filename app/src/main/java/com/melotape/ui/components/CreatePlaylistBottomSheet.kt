package com.melotape.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.melotape.ui.theme.*

/**
 * Bottom Sheet for creating a new custom Mixtape.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePlaylistBottomSheet(
    onDismiss: () -> Unit,
    onCreate: (name: String, description: String?) -> Unit,
    modifier: Modifier = Modifier,
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
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
                text = "CREATE NEW MIXTAPE",
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
                            selectedLabelColor = Color.White,
                            containerColor = Surface,
                            labelColor = TextSecondary,
                        ),
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        onCreate(name.trim(), description.trim().ifEmpty { null })
                        onDismiss()
                    }
                },
                enabled = name.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryAccent,
                    disabledContainerColor = Surface,
                ),
                shape = RoundedCornerShape(10.dp),
            ) {
                Text(
                    text = "Record Mixtape",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                )
            }
        }
    }
}

package com.melotape.ui.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.melotape.ui.theme.*

/**
 * Skeuomorphic / Retro Confirmation Dialog for destructive or key actions
 * (e.g. deleting a mixtape, clearing cache, removing downloads).
 */
@Composable
fun ConfirmDialog(
    title: String,
    message: String,
    confirmText: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    dismissText: String = "Cancel",
    isDestructive: Boolean = true,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary,
            )
        },
        text = {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm()
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isDestructive) Burgundy else PrimaryAccent,
                ),
                shape = RoundedCornerShape(8.dp),
            ) {
                Text(
                    text = confirmText,
                    color = androidx.compose.ui.graphics.Color.White,
                    style = MaterialTheme.typography.labelLarge,
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
            ) {
                Text(
                    text = dismissText,
                    color = TextSecondary,
                    style = MaterialTheme.typography.labelLarge,
                )
            }
        },
        containerColor = SurfaceElevated,
        shape = RoundedCornerShape(14.dp),
        modifier = modifier,
    )
}

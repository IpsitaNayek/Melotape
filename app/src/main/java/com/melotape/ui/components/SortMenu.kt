package com.melotape.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.melotape.ui.model.SongSortOrder
import com.melotape.ui.theme.*

/**
 * Bottom Sheet for selecting the Sequence / Sort Order on Cassette Album Detail / Song List.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SortOrderBottomSheet(
    currentOrder: SongSortOrder,
    onSelectOrder: (SongSortOrder) -> Unit,
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
                text = "TAPE SEQUENCE ORDER",
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary,
            )
            Text(
                text = "Re-align tracks on the cassette reel",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
            )

            Spacer(Modifier.height(16.dp))

            SongSortOrder.entries.forEach { order ->
                val isSelected = order == currentOrder
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onSelectOrder(order)
                            onDismiss()
                        }
                        .padding(vertical = 12.dp, horizontal = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = order.displayName,
                        style = MaterialTheme.typography.bodyLarge,
                        color = if (isSelected) PrimaryAccent else TextPrimary,
                    )
                    if (isSelected) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Selected",
                            tint = PrimaryAccent,
                            modifier = Modifier.size(20.dp),
                        )
                    }
                }
            }
        }
    }
}

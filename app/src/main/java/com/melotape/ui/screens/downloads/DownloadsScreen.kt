package com.melotape.ui.screens.downloads

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.melotape.data.fake.FakeMusicRepository
import com.melotape.ui.components.*
import com.melotape.ui.mapper.toItemUi
import com.melotape.ui.model.SongItemUi
import com.melotape.ui.theme.*

@Composable
fun DownloadsScreen(
    onBack: () -> Unit,
    onSongClick: (SongItemUi) -> Unit,
    modifier: Modifier = Modifier,
) {
    var downloadedSongs by remember {
        mutableStateOf(FakeMusicRepository.createInitialSongList().take(5).map { it.toItemUi() })
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Background),
    ) {
        MelotapeTopBar(
            title = "Downloaded Cassettes",
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = TextPrimary,
                    )
                }
            },
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Surface)
                        .padding(16.dp),
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Text("OFFLINE TAPE STORAGE", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                            Text("4.2 GB / 64 GB", style = MaterialTheme.typography.labelSmall, color = Amber)
                        }
                        Spacer(Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress = { 0.065f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = PrimaryAccent,
                            trackColor = SurfaceElevated,
                        )
                    }
                }
                Spacer(Modifier.height(12.dp))
                SectionHeader(
                    title = "Saved Tracks",
                    actionText = "${downloadedSongs.size} Tracks",
                    onAction = {},
                )
            }

            items(downloadedSongs, key = { it.id }) { song ->
                SongRow(
                    song = song,
                    onClick = { onSongClick(song) },
                    trailing = {
                        TapeChip(text = song.formatLabel ?: "FLAC", tone = ChipTone.Amber)
                        Spacer(Modifier.width(8.dp))
                        IconButton(
                            onClick = { downloadedSongs = downloadedSongs.filterNot { it.id == song.id } },
                            modifier = Modifier.size(32.dp),
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Remove download",
                                tint = TextSecondary,
                                modifier = Modifier.size(18.dp),
                            )
                        }
                    },
                )
            }
        }
    }
}

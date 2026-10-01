package com.melotape.ui.screens.search

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.melotape.ui.components.*
import com.melotape.ui.model.SongItemUi
import com.melotape.ui.model.VaultUi
import com.melotape.ui.theme.*

/**
 * Route (stateful) — connects Hilt ViewModel to the SearchScreen.
 */
@Composable
fun SearchRoute(
    onNavigateToNowPlaying: () -> Unit,
    onNavigateToVault: (vaultTag: String) -> Unit,
    viewModel: SearchViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    SearchScreen(
        state = state,
        onQueryChange = viewModel::onQueryChange,
        onClearQuery = viewModel::onClearQuery,
        onClearRecents = viewModel::onClearRecents,
        onVaultClick = { vault -> onNavigateToVault(vault.tag) },
        onSongClick = { onNavigateToNowPlaying() },
        onToggleLoved = viewModel::onToggleLoved,
    )
}

/**
 * Screen (stateless) — renders the Search screen matching the screenshot.
 */
@Composable
fun SearchScreen(
    state: SearchUiState,
    onQueryChange: (String) -> Unit,
    onClearQuery: () -> Unit,
    onClearRecents: () -> Unit,
    onVaultClick: (VaultUi) -> Unit,
    onSongClick: (SongItemUi) -> Unit,
    onToggleLoved: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Background)
            .verticalScroll(rememberScrollState()),
    ) {
        MelotapeTopBar(title = "Search")

        Spacer(Modifier.height(8.dp))

        // 1. Search Field
        OutlinedTextField(
            value = state.query,
            onValueChange = onQueryChange,
            placeholder = { Text("Search tapes, artists, vaults...", color = TextSecondary) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = TextSecondary,
                )
            },
            trailingIcon = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (state.query.isNotEmpty()) {
                        IconButton(onClick = onClearQuery) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear",
                                tint = TextSecondary,
                            )
                        }
                    }
                    IconButton(onClick = {}) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = "Voice Search",
                            tint = Amber,
                        )
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                focusedBorderColor = PrimaryAccent,
                unfocusedBorderColor = SurfaceElevated,
                focusedContainerColor = Surface,
                unfocusedContainerColor = Surface,
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
        )

        Spacer(Modifier.height(16.dp))

        if (state.isSearching) {
            // === Search Results Mode ===
            SectionHeader(
                title = "Results for \"${state.query}\"",
                actionText = "",
                onAction = {},
            )
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                if (state.searchResults.isEmpty()) {
                    EmptyState(
                        title = "No Tapes Found",
                        message = "Try searching for a different artist, tape title, or vault genre.",
                    )
                } else {
                    state.searchResults.forEach { song ->
                        SongRow(
                            song = song,
                            onClick = { onSongClick(song) },
                            trailing = {
                                HeartButton(
                                    isLoved = song.isLoved,
                                    onToggle = { onToggleLoved(song.id) },
                                )
                                PlayPauseButton(
                                    isPlaying = false,
                                    onClick = { onSongClick(song) },
                                    size = PlayButtonSize.Small,
                                )
                            },
                        )
                    }
                }
            }
        } else {
            // === Browse Mode (Screenshots) ===

            // 2. Recents Chips
            if (state.recents.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Recents",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary,
                    )
                    Text(
                        text = "Clear all",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary,
                        modifier = Modifier.clickable { onClearRecents() },
                    )
                }

                Spacer(Modifier.height(8.dp))

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(state.recents) { recentTag ->
                        TapeChip(
                            text = recentTag,
                            tone = ChipTone.Surface,
                            modifier = Modifier.clickable { onQueryChange(recentTag) },
                        )
                    }
                }

                Spacer(Modifier.height(20.dp))
            }

            // 3. Sound Vault (2x3 Grid)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Sound Vault",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                )
                Text(
                    text = "6 Vaults",
                    style = MaterialTheme.typography.labelSmall,
                    color = Amber,
                )
            }

            Spacer(Modifier.height(8.dp))

            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                state.soundVaults.chunked(2).forEach { rowVaults ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        rowVaults.forEach { vault ->
                            VaultTile(
                                vault = vault,
                                onClick = { onVaultClick(vault) },
                                modifier = Modifier.weight(1f),
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // 4. Trending Tape Reissues (Ranked List 01-03)
            SectionHeader(
                title = "Trending Tape Reissues",
                actionText = "See all",
                onAction = {},
            )
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                state.trendingSongs.take(3).forEachIndexed { index, song ->
                    SongRow(
                        song = song,
                        onClick = { onSongClick(song) },
                        leading = {
                            Text(
                                text = String.format("%02d", index + 1),
                                style = MaterialTheme.typography.titleMedium,
                                color = if (index == 0) Amber else TextSecondary,
                            )
                        },
                        trailing = {
                            TapeChip(
                                text = song.formatLabel ?: "FLAC",
                                tone = ChipTone.Surface,
                            )
                            Spacer(Modifier.width(8.dp))
                            PlayPauseButton(
                                isPlaying = false,
                                onClick = { onSongClick(song) },
                                size = PlayButtonSize.Small,
                            )
                        },
                    )
                }
            }
        }

        Spacer(Modifier.height(32.dp))
    }
}

/**
 * Vault tile for the 2x3 Sound Vault grid.
 */
@Composable
private fun VaultTile(
    vault: VaultUi,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .height(78.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(if (vault.isLocal) Color(0xFF1E2836) else Surface)
            .border(1.dp, if (vault.isLocal) Amber.copy(alpha = 0.5f) else SurfaceElevated, RoundedCornerShape(10.dp))
            .clickable { onClick() }
            .padding(12.dp),
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = vault.title,
                style = MaterialTheme.typography.titleMedium,
                color = if (vault.isLocal) Amber else TextPrimary,
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = vault.subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary,
            )
        }
    }
}

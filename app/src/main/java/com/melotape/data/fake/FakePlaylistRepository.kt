package com.melotape.data.fake

import com.melotape.domain.model.Playlist
import com.melotape.domain.model.Song
import com.melotape.domain.repository.PlaylistRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FakePlaylistRepository @Inject constructor() : PlaylistRepository {

    private val playlistsFlow = MutableStateFlow(
        listOf(
            Playlist(
                id = "pl_featured",
                name = "Cassette Lo-Fi 1994",
                description = "Warm analog saturation, Otari tape heads, 12 vintage masters",
                coverArtUri = null,
                songCount = 12,
                totalDurationMs = 2_880_000L, // 48 min
                isPinned = true,
                createdAt = 1704067200000L,
                updatedAt = 1704067200000L,
            ),
            Playlist(
                id = "pl_citypop",
                name = "Tokyo City Pop 1984",
                description = "Night drive cassette recordings from Shinjuku",
                coverArtUri = null,
                songCount = 10,
                totalDurationMs = 2_450_000L,
                isPinned = false,
                createdAt = 1704153600000L,
                updatedAt = 1704153600000L,
            ),
            Playlist(
                id = "pl_synthwave",
                name = "Analog Synthwave Vol. 1",
                description = "Type II high bias tape masters recorded on Tascam 424",
                coverArtUri = null,
                songCount = 8,
                totalDurationMs = 2_100_000L,
                isPinned = false,
                createdAt = 1704240000000L,
                updatedAt = 1704240000000L,
            ),
            Playlist(
                id = "pl_vintage",
                name = "Vintage Masters C-90",
                description = "Both sides complete: 90 minutes of analog warmth",
                coverArtUri = null,
                songCount = 18,
                totalDurationMs = 4_440_000L, // 1h 14m
                isPinned = false,
                createdAt = 1704326400000L,
                updatedAt = 1704326400000L,
            ),
        )
    )

    override fun getPlaylists(): Flow<List<Playlist>> = playlistsFlow

    override fun getPlaylistById(id: String): Flow<Playlist?> {
        return playlistsFlow.map { list -> list.find { it.id == id } }
    }

    override fun getPlaylistSongs(playlistId: String): Flow<List<Song>> {
        val allSongs = FakeMusicRepository.createInitialSongList()
        return MutableStateFlow(
            when (playlistId) {
                "pl_featured" -> allSongs.take(8)
                "pl_vintage" -> allSongs
                else -> allSongs.take(6)
            }
        )
    }

    override suspend fun createPlaylist(name: String, description: String?): String {
        val newId = "pl_${System.currentTimeMillis()}"
        val newPlaylist = Playlist(
            id = newId,
            name = name,
            description = description,
            songCount = 0,
            totalDurationMs = 0L,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis(),
        )
        playlistsFlow.value = playlistsFlow.value + newPlaylist
        return newId
    }

    override suspend fun deletePlaylist(playlistId: String) {
        playlistsFlow.value = playlistsFlow.value.filterNot { it.id == playlistId }
    }
}

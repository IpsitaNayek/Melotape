package com.melotape.data.repository

import com.melotape.data.db.dao.FavoriteDao
import com.melotape.data.db.dao.PlaylistDao
import com.melotape.data.db.dao.SongDao
import com.melotape.data.db.entity.PlaylistEntity
import com.melotape.data.db.entity.PlaylistSongEntity
import com.melotape.data.db.entity.toDomain
import com.melotape.data.db.entity.toEntity
import com.melotape.di.IoDispatcher
import com.melotape.domain.model.Playlist
import com.melotape.domain.model.Song
import com.melotape.domain.repository.PlaylistRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
@Singleton
class PlaylistRepositoryImpl @Inject constructor(
    private val playlistDao: PlaylistDao,
    private val songDao: SongDao,
    private val favoriteDao: FavoriteDao,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : PlaylistRepository {

    private var hasAttemptedSeed = false

    private suspend fun seedDefaultPlaylists() = withContext(ioDispatcher) {
        val initialSongs = com.melotape.data.fake.FakeMusicRepository.createInitialSongList()
        // Save initial songs into songDao first so foreign key references are valid
        songDao.upsertAll(initialSongs.map { it.toEntity() })

        val playlists = listOf(
            PlaylistEntity(
                id = "pl_indie",
                name = "Indie Cassette Nostalgia",
                description = "Warm analog masterings, dreamy jangly guitars, & bedroom pop archives.",
                coverArtUri = null,
                pinned = true,
                createdAt = 1704067200000L,
                updatedAt = 1704067200000L,
            ),
            PlaylistEntity(
                id = "pl_tokyo",
                name = "Tokyo Vinyl Sessions",
                description = "City Pop • Casiopea, Tatsuro Yamashita archives",
                coverArtUri = null,
                pinned = false,
                createdAt = 1704153600000L,
                updatedAt = 1704153600000L,
            ),
            PlaylistEntity(
                id = "pl_synth",
                name = "Midnight Drive",
                description = "Synthwave • Kavinsky, Gunship, retro electro",
                coverArtUri = null,
                pinned = false,
                createdAt = 1704240000000L,
                updatedAt = 1704240000000L,
            ),
            PlaylistEntity(
                id = "pl_dreampop",
                name = "80s Dream Pop",
                description = "Cocteau Twins • Beach House • Analog dreams",
                coverArtUri = null,
                pinned = false,
                createdAt = 1704326400000L,
                updatedAt = 1704326400000L,
            ),
        )

        playlists.forEach { playlistDao.insertPlaylist(it) }

        // Link initial songs to "pl_indie" (all 18+ songs)
        initialSongs.forEachIndexed { index, song ->
            playlistDao.insertPlaylistSong(
                PlaylistSongEntity(
                    playlistId = "pl_indie",
                    songId = song.id,
                    position = index,
                    addedAt = System.currentTimeMillis() + index,
                )
            )
        }

        // Link a few songs to the other playlists
        initialSongs.take(8).forEachIndexed { index, song ->
            playlistDao.insertPlaylistSong(
                PlaylistSongEntity(
                    playlistId = "pl_tokyo",
                    songId = song.id,
                    position = index,
                    addedAt = System.currentTimeMillis() + index,
                )
            )
        }
        initialSongs.drop(4).take(6).forEachIndexed { index, song ->
            playlistDao.insertPlaylistSong(
                PlaylistSongEntity(
                    playlistId = "pl_synth",
                    songId = song.id,
                    position = index,
                    addedAt = System.currentTimeMillis() + index,
                )
            )
        }
    }

    override fun getPlaylists(): Flow<List<Playlist>> {
        return playlistDao.getActivePlaylists().flatMapLatest { entities ->
            if (entities.isEmpty()) {
                if (!hasAttemptedSeed) {
                    hasAttemptedSeed = true
                    seedDefaultPlaylists()
                }
                flowOf(emptyList())
            } else {
                val flows = entities.map { entity ->
                    combine(
                        playlistDao.getSongCountForPlaylist(entity.id),
                        playlistDao.getTotalDurationForPlaylist(entity.id)
                    ) { count, duration ->
                        entity.toDomain(songCount = count, totalDurationMs = duration)
                    }
                }
                combine(flows) { it.toList() }
            }
        }.flowOn(ioDispatcher)
    }

    override fun getPlaylistById(id: String): Flow<Playlist?> {
        return playlistDao.getPlaylistById(id).flatMapLatest { entity ->
            if (entity == null) {
                flowOf(null)
            } else {
                combine(
                    playlistDao.getSongCountForPlaylist(entity.id),
                    playlistDao.getTotalDurationForPlaylist(entity.id)
                ) { count, duration ->
                    entity.toDomain(songCount = count, totalDurationMs = duration)
                }
            }
        }.flowOn(ioDispatcher)
    }

    override fun getPlaylistSongs(playlistId: String): Flow<List<Song>> {
        return combine(
            playlistDao.getSongsForPlaylist(playlistId),
            favoriteDao.getAllFavorites()
        ) { songEntities, favorites ->
            val favSet = favorites.map { it.songId }.toSet()
            songEntities.map { entity ->
                entity.toDomain(isLoved = favSet.contains(entity.id))
            }
        }.flowOn(ioDispatcher)
    }

    override suspend fun createPlaylist(name: String, description: String?): String = withContext(ioDispatcher) {
        val newId = UUID.randomUUID().toString()
        val playlist = PlaylistEntity(
            id = newId,
            name = name,
            description = description,
            coverArtUri = null,
            pinned = false,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis(),
        )
        playlistDao.insertPlaylist(playlist)
        newId
    }

    override suspend fun deletePlaylist(playlistId: String) = withContext(ioDispatcher) {
        playlistDao.softDeletePlaylist(playlistId)
    }

    override suspend fun addSongToPlaylist(playlistId: String, songId: String) = withContext(ioDispatcher) {
        val nextPos = playlistDao.getMaxPosition(playlistId) + 1
        val crossRef = PlaylistSongEntity(
            playlistId = playlistId,
            songId = songId,
            position = nextPos,
            addedAt = System.currentTimeMillis(),
        )
        playlistDao.insertPlaylistSong(crossRef)
    }

    override suspend fun removeSongFromPlaylist(playlistId: String, songId: String) = withContext(ioDispatcher) {
        playlistDao.removeSongFromPlaylist(playlistId, songId)
    }

    override suspend fun reorderPlaylist(playlistId: String, songIdsInOrder: List<String>) = withContext(ioDispatcher) {
        playlistDao.reorderPlaylistSongs(playlistId, songIdsInOrder)
    }

    override suspend fun renamePlaylist(playlistId: String, name: String, description: String?) = withContext(ioDispatcher) {
        playlistDao.updatePlaylistDetails(playlistId, name, description)
    }
}


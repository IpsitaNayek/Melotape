package com.melotape.data.repository

import com.melotape.data.db.dao.FavoriteDao
import com.melotape.data.db.dao.PlaylistDao
import com.melotape.data.db.dao.SongDao
import com.melotape.data.db.entity.PlaylistEntity
import com.melotape.data.db.entity.PlaylistSongEntity
import com.melotape.data.db.entity.toDomain
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

    override fun getPlaylists(): Flow<List<Playlist>> {
        return playlistDao.getActivePlaylists().flatMapLatest { entities ->
            if (entities.isEmpty()) {
                // Return flow with default seed playlists or empty
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

    suspend fun addSongToPlaylist(playlistId: String, songId: String) = withContext(ioDispatcher) {
        val nextPos = playlistDao.getMaxPosition(playlistId) + 1
        val crossRef = PlaylistSongEntity(
            playlistId = playlistId,
            songId = songId,
            position = nextPos,
            addedAt = System.currentTimeMillis(),
        )
        playlistDao.insertPlaylistSong(crossRef)
    }

    suspend fun removeSongFromPlaylist(playlistId: String, songId: String) = withContext(ioDispatcher) {
        playlistDao.removeSongFromPlaylist(playlistId, songId)
    }
}

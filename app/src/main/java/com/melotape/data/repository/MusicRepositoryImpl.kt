package com.melotape.data.repository

import com.melotape.data.db.dao.DownloadDao
import com.melotape.data.db.dao.FavoriteDao
import com.melotape.data.db.dao.PlayHistoryDao
import com.melotape.data.db.dao.SongDao
import com.melotape.data.db.entity.toDomain
import com.melotape.data.db.entity.toEntity
import com.melotape.data.fake.FakeMusicRepository
import com.melotape.data.mediastore.MediaStoreDataSource
import com.melotape.di.IoDispatcher
import com.melotape.domain.model.Song
import com.melotape.domain.repository.MusicRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MusicRepositoryImpl @Inject constructor(
    private val mediaStoreDataSource: MediaStoreDataSource,
    private val songDao: SongDao,
    private val favoriteDao: FavoriteDao,
    private val playHistoryDao: PlayHistoryDao,
    private val downloadDao: DownloadDao,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : MusicRepository {

    private val seedSongs = MutableStateFlow(FakeMusicRepository.createInitialSongList())

    private val allAvailableSongs: Flow<List<Song>> = combine(
        mediaStoreDataSource.localSongsFlow,
        seedSongs
    ) { local, seed ->
        val merged = (local + seed).distinctBy { it.id }
        // Ensure all known songs are cached in Room for relational integrity
        withContext(ioDispatcher) {
            songDao.upsertAll(merged.map { it.toEntity() })
        }
        merged
    }.flowOn(ioDispatcher)

    override fun getRecentlyPlayed(): Flow<List<Song>> {
        return combine(
            playHistoryDao.getRecentlyPlayedSongs(),
            favoriteDao.getAllFavorites(),
            allAvailableSongs
        ) { historyEntities, favorites, allSongs ->
            val favSet = favorites.map { it.songId }.toSet()
            if (historyEntities.isNotEmpty()) {
                historyEntities.map { entity ->
                    entity.toDomain(isLoved = favSet.contains(entity.id))
                }
            } else {
                allSongs.take(8).map { it.copy(isLoved = favSet.contains(it.id)) }
            }
        }.flowOn(ioDispatcher)
    }

    override fun getDownloadedSongs(): Flow<List<Song>> {
        return combine(
            downloadDao.getCompletedDownloadedSongs(),
            mediaStoreDataSource.localSongsFlow,
            favoriteDao.getAllFavorites()
        ) { downloadedEntities, localSongs, favorites ->
            val favSet = favorites.map { it.songId }.toSet()
            val roomSongs = downloadedEntities.map { it.toDomain(isLoved = favSet.contains(it.id)) }
            val localDomain = localSongs.map { it.copy(isLoved = favSet.contains(it.id)) }
            (roomSongs + localDomain).distinctBy { it.id }.take(10)
        }.flowOn(ioDispatcher)
    }

    override fun getLocalSongs(): Flow<List<Song>> {
        return combine(
            mediaStoreDataSource.localSongsFlow,
            favoriteDao.getAllFavorites()
        ) { localList, favorites ->
            val favSet = favorites.map { it.songId }.toSet()
            if (localList.isNotEmpty()) {
                localList.map { it.copy(isLoved = favSet.contains(it.id)) }
            } else {
                seedSongs.value
                    .filter { it.source is com.melotape.domain.model.MusicSource.Local }
                    .map { it.copy(isLoved = favSet.contains(it.id)) }
            }
        }.flowOn(ioDispatcher)
    }

    override fun getTrendingSongs(): Flow<List<Song>> {
        return combine(seedSongs, favoriteDao.getAllFavorites()) { seed, favorites ->
            val favSet = favorites.map { it.songId }.toSet()
            seed.take(6).map { it.copy(isLoved = favSet.contains(it.id)) }
        }.flowOn(ioDispatcher)
    }

    override fun getSongById(id: String): Flow<Song?> {
        return combine(
            allAvailableSongs,
            favoriteDao.isFavorite(id)
        ) { songs, isLoved ->
            songs.find { it.id == id }?.copy(isLoved = isLoved)
        }.flowOn(ioDispatcher)
    }

    override fun searchSongs(query: String): Flow<List<Song>> {
        return combine(
            allAvailableSongs,
            favoriteDao.getAllFavorites()
        ) { songs, favorites ->
            if (query.isBlank()) {
                emptyList()
            } else {
                val favSet = favorites.map { it.songId }.toSet()
                songs.filter {
                    it.title.contains(query, ignoreCase = true) ||
                    it.artist.contains(query, ignoreCase = true) ||
                    (it.album?.contains(query, ignoreCase = true) ?: false)
                }.map { it.copy(isLoved = favSet.contains(it.id)) }
            }
        }.flowOn(ioDispatcher)
    }

    override fun getLovedSongs(): Flow<List<Song>> {
        return combine(
            favoriteDao.getFavoriteSongs(),
            favoriteDao.getAllFavorites(),
            allAvailableSongs
        ) { dbFavoriteSongs, favorites, allSongs ->
            val favSet = favorites.map { it.songId }.toSet()
            if (dbFavoriteSongs.isNotEmpty()) {
                dbFavoriteSongs.map { it.toDomain(isLoved = true) }
            } else {
                allSongs.filter { favSet.contains(it.id) }.map { it.copy(isLoved = true) }
            }
        }.flowOn(ioDispatcher)
    }

    override suspend fun toggleLoved(songId: String) = withContext(ioDispatcher) {
        // Ensure song is in database before favoriting
        val existingSong = allAvailableSongs.first().find { it.id == songId }
        if (existingSong != null) {
            songDao.upsert(existingSong.toEntity())
        }
        favoriteDao.toggleFavorite(songId)
    }

    fun forceRescanLocal() {
        mediaStoreDataSource.forceRescan()
    }
}

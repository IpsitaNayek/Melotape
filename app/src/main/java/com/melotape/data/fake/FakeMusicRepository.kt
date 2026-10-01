package com.melotape.data.fake

import com.melotape.domain.model.MusicSource
import com.melotape.domain.model.Song
import com.melotape.domain.model.SourcePrefix
import com.melotape.domain.repository.MusicRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FakeMusicRepository @Inject constructor() : MusicRepository {

    private val songsFlow = MutableStateFlow(createInitialSongList())

    override fun getRecentlyPlayed(): Flow<List<Song>> {
        return songsFlow.map { list -> list.take(8) }
    }

    override fun getDownloadedSongs(): Flow<List<Song>> {
        return songsFlow.map { list -> list.filter { it.downloadAllowed }.take(6) }
    }

    override fun getLocalSongs(): Flow<List<Song>> {
        return songsFlow.map { list -> list.filter { it.source is MusicSource.Local } }
    }

    override fun getTrendingSongs(): Flow<List<Song>> {
        return songsFlow.map { list -> list.take(6) }
    }

    override fun getSongById(id: String): Flow<Song?> {
        return songsFlow.map { list -> list.find { it.id == id } }
    }

    override fun searchSongs(query: String): Flow<List<Song>> {
        return songsFlow.map { list ->
            if (query.isBlank()) emptyList()
            else list.filter {
                it.title.contains(query, ignoreCase = true) ||
                it.artist.contains(query, ignoreCase = true) ||
                (it.album?.contains(query, ignoreCase = true) ?: false)
            }
        }
    }

    override fun getLovedSongs(): Flow<List<Song>> {
        return songsFlow.map { list -> list.filter { it.isLoved } }
    }

    override suspend fun toggleLoved(songId: String) {
        val current = songsFlow.value
        val updated = current.map { song ->
            if (song.id == songId) song.copy(isLoved = !song.isLoved)
            else song
        }
        songsFlow.value = updated
    }

    companion object {
        fun createInitialSongList(): List<Song> {
            return listOf(
                Song(
                    id = SourcePrefix.local(101L),
                    title = "Electric Feel (Analog Remaster 1994)",
                    artist = "MGMT",
                    album = "Oracular Spectacular Tape Edition",
                    artworkUri = null,
                    durationMs = 229_000L,
                    source = MusicSource.Local("content://media/external/audio/media/101"),
                    year = 2007,
                    genre = "Psychedelic Pop",
                    mimeType = "audio/flac",
                    fileSizeBytes = 34_500_000L,
                    bitrate = 960,
                    sampleRate = 96000,
                    trackNumber = 1,
                    isLoved = true,
                    downloadAllowed = true,
                ),
                Song(
                    id = SourcePrefix.local(102L),
                    title = "Plastic Love (Special Vinyl Master)",
                    artist = "Mariya Takeuchi",
                    album = "Variety Cassette Rip",
                    artworkUri = null,
                    durationMs = 295_000L,
                    source = MusicSource.Local("content://media/external/audio/media/102"),
                    year = 1984,
                    genre = "City Pop",
                    mimeType = "audio/flac",
                    fileSizeBytes = 42_100_000L,
                    bitrate = 1411,
                    sampleRate = 44100,
                    trackNumber = 2,
                    isLoved = true,
                    downloadAllowed = true,
                ),
                Song(
                    id = SourcePrefix.local(103L),
                    title = "Resonance (Tape Saturator Mix)",
                    artist = "HOME",
                    album = "Odyssey Deck Vol. 1",
                    artworkUri = null,
                    durationMs = 212_000L,
                    source = MusicSource.Local("content://media/external/audio/media/103"),
                    year = 2014,
                    genre = "Chillwave",
                    mimeType = "audio/mpeg",
                    fileSizeBytes = 8_200_000L,
                    bitrate = 320,
                    sampleRate = 44100,
                    trackNumber = 3,
                    isLoved = false,
                    downloadAllowed = true,
                ),
                Song(
                    id = SourcePrefix.jamendo("j_201"),
                    title = "Midnight Horizon (Otari MX50 Mix)",
                    artist = "Vintage Sound Collective",
                    album = "Vault 01: Lo-Fi Tape Beats",
                    artworkUri = null,
                    durationMs = 246_000L,
                    source = MusicSource.Jamendo(
                        trackUrl = "https://prod-1.storage.jamendo.com/?trackid=201",
                        downloadUrl = null,
                        audioDownloadAllowed = true,
                        licenseUrl = "https://creativecommons.org/licenses/by-nc-nd/4.0/",
                    ),
                    year = 2022,
                    genre = "Lo-Fi",
                    mimeType = "audio/mpeg",
                    fileSizeBytes = 9_800_000L,
                    bitrate = 320,
                    sampleRate = 48000,
                    trackNumber = 1,
                    isLoved = false,
                    downloadAllowed = true,
                ),
                Song(
                    id = SourcePrefix.jamendo("j_202"),
                    title = "Cassette Sunset (Side A)",
                    artist = "Neon Chrome Project",
                    album = "Synthwave Vault 1986",
                    artworkUri = null,
                    durationMs = 278_000L,
                    source = MusicSource.Jamendo(
                        trackUrl = "https://prod-1.storage.jamendo.com/?trackid=202",
                        downloadUrl = null,
                        audioDownloadAllowed = true,
                        licenseUrl = null,
                    ),
                    year = 2023,
                    genre = "Synthwave",
                    mimeType = "audio/flac",
                    fileSizeBytes = 38_000_000L,
                    bitrate = 960,
                    sampleRate = 96000,
                    trackNumber = 2,
                    isLoved = true,
                    downloadAllowed = true,
                ),
                Song(
                    id = SourcePrefix.jamendo("j_203"),
                    title = "Tokyo Rain & Tape Hiss",
                    artist = "Sora Yume",
                    album = "Neo Tokyo Ambient",
                    artworkUri = null,
                    durationMs = 310_000L,
                    source = MusicSource.Jamendo(
                        trackUrl = "https://prod-1.storage.jamendo.com/?trackid=203",
                        downloadUrl = null,
                        audioDownloadAllowed = false,
                        licenseUrl = null,
                    ),
                    year = 2021,
                    genre = "Ambient",
                    mimeType = "audio/mpeg",
                    fileSizeBytes = 7_400_000L,
                    bitrate = 192,
                    sampleRate = 44100,
                    trackNumber = 3,
                    isLoved = false,
                    downloadAllowed = false,
                ),
                Song(
                    id = SourcePrefix.local(104L),
                    title = "Show Me How (Reel-to-Reel Tape)",
                    artist = "Men I Trust",
                    album = "Oncle Jazz Tape",
                    artworkUri = null,
                    durationMs = 215_000L,
                    source = MusicSource.Local("content://media/external/audio/media/104"),
                    year = 2019,
                    genre = "Indie Pop",
                    mimeType = "audio/flac",
                    fileSizeBytes = 31_200_000L,
                    bitrate = 880,
                    sampleRate = 48000,
                    trackNumber = 4,
                    isLoved = true,
                    downloadAllowed = true,
                ),
                Song(
                    id = SourcePrefix.local(105L),
                    title = "Space Song (Warm Hiss Cut)",
                    artist = "Beach House",
                    album = "Depression Cherry Deck",
                    artworkUri = null,
                    durationMs = 320_000L,
                    source = MusicSource.Local("content://media/external/audio/media/105"),
                    year = 2015,
                    genre = "Dream Pop",
                    mimeType = "audio/mpeg",
                    fileSizeBytes = 12_400_000L,
                    bitrate = 320,
                    sampleRate = 44100,
                    trackNumber = 5,
                    isLoved = false,
                    downloadAllowed = true,
                ),
                Song(
                    id = SourcePrefix.firebase("fb_301"),
                    title = "Alex's Studio Cassette Session 01",
                    artist = "Alex & Friends",
                    album = "Home Tapes 2024",
                    artworkUri = null,
                    durationMs = 345_000L,
                    source = MusicSource.Firebase("users/alex/audio/session_01.flac"),
                    year = 2024,
                    genre = "Demo",
                    mimeType = "audio/flac",
                    fileSizeBytes = 55_000_000L,
                    bitrate = 1411,
                    sampleRate = 96000,
                    trackNumber = 1,
                    isLoved = true,
                    downloadAllowed = true,
                ),
                Song(
                    id = SourcePrefix.local(106L),
                    title = "Harvest Moon (Acoustic Reel)",
                    artist = "Poolside",
                    album = "Pacific Standard Cassette",
                    artworkUri = null,
                    durationMs = 296_000L,
                    source = MusicSource.Local("content://media/external/audio/media/106"),
                    year = 2012,
                    genre = "Nu-Disco",
                    mimeType = "audio/mpeg",
                    fileSizeBytes = 11_100_000L,
                    bitrate = 320,
                    sampleRate = 44100,
                    trackNumber = 6,
                    isLoved = true,
                    downloadAllowed = true,
                ),
                Song(
                    id = SourcePrefix.local(107L),
                    title = "The Youth (Dolby C Calibration)",
                    artist = "MGMT",
                    album = "Oracular Spectacular Tape Edition",
                    artworkUri = null,
                    durationMs = 232_000L,
                    source = MusicSource.Local("content://media/external/audio/media/107"),
                    year = 2007,
                    genre = "Psychedelic Pop",
                    mimeType = "audio/flac",
                    fileSizeBytes = 36_000_000L,
                    bitrate = 960,
                    sampleRate = 96000,
                    trackNumber = 7,
                    isLoved = false,
                    downloadAllowed = true,
                ),
                Song(
                    id = SourcePrefix.jamendo("j_204"),
                    title = "Warm Analog Overdrive",
                    artist = "Tape Machine Orchestra",
                    album = "Master Calibration Vault",
                    artworkUri = null,
                    durationMs = 198_000L,
                    source = MusicSource.Jamendo(
                        trackUrl = "https://prod-1.storage.jamendo.com/?trackid=204",
                        downloadUrl = null,
                        audioDownloadAllowed = true,
                        licenseUrl = null,
                    ),
                    year = 2020,
                    genre = "Instrumental",
                    mimeType = "audio/mpeg",
                    fileSizeBytes = 7_900_000L,
                    bitrate = 320,
                    sampleRate = 44100,
                    trackNumber = 4,
                    isLoved = false,
                    downloadAllowed = true,
                ),
            )
        }
    }
}

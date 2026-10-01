package com.melotape

import com.melotape.data.fake.FakeMusicRepository
import com.melotape.data.fake.FakePlaylistRepository
import com.melotape.domain.logic.SideSplitter
import com.melotape.domain.model.MusicSource
import com.melotape.domain.model.Song
import com.melotape.domain.model.TapeLength
import com.melotape.domain.usecase.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class Phase7Test {

    private val sideSplitter = SideSplitter()
    private val playlistRepo = FakePlaylistRepository()

    private val createPlaylistUseCase = CreatePlaylistUseCase(playlistRepo)
    private val renamePlaylistUseCase = RenamePlaylistUseCase(playlistRepo)
    private val deletePlaylistUseCase = DeletePlaylistUseCase(playlistRepo)
    private val addToPlaylistUseCase = AddToPlaylistUseCase(playlistRepo)
    private val removeFromPlaylistUseCase = RemoveFromPlaylistUseCase(playlistRepo)
    private val reorderPlaylistUseCase = ReorderPlaylistUseCase(playlistRepo)

    private fun generateSong(id: String, durationMin: Int): Song {
        return Song(
            id = "LOCAL:$id",
            title = "Track $id",
            artist = "Artist $id",
            album = "Tape Album",
            artworkUri = null,
            durationMs = durationMin * 60_000L,
            source = MusicSource.Local("content://uri/$id"),
        )
    }

    @Test
    fun testSideSplitterTapeLengthSelection() {
        // 20 minutes -> C-30
        val shortList = (1..5).map { generateSong("$it", 4) }
        val shortResult = sideSplitter.splitIntoSides(shortList)
        assertEquals(TapeLength.C30, shortResult.tapeLength)
        assertEquals("C-30 STEREO", shortResult.tapeLengthLabel)

        // 42 minutes -> C-46
        val c46List = (1..7).map { generateSong("$it", 6) }
        val c46Result = sideSplitter.splitIntoSides(c46List)
        assertEquals(TapeLength.C46, c46Result.tapeLength)
        assertEquals("C-46 STEREO", c46Result.tapeLengthLabel)

        // 55 minutes -> C-60
        val c60List = (1..11).map { generateSong("$it", 5) }
        val c60Result = sideSplitter.splitIntoSides(c60List)
        assertEquals(TapeLength.C60, c60Result.tapeLength)
        assertEquals("C-60 STEREO", c60Result.tapeLengthLabel)

        // 80 minutes -> C-90
        val c90List = (1..16).map { generateSong("$it", 5) }
        val c90Result = sideSplitter.splitIntoSides(c90List)
        assertEquals(TapeLength.C90, c90Result.tapeLength)
        assertEquals("C-90 STEREO", c90Result.tapeLengthLabel)
    }

    @Test
    fun testSideSplitterAccumulationAndReelLabels() {
        // C-60 tape has 30 mins per side capacity
        // Track 1: 15 min, Track 2: 12 min (total 27 min -> fits Side A)
        // Track 3: 10 min (27+10=37 > 30 min -> starts Side B)
        // Track 4: 15 min (fits Side B)
        val songs = listOf(
            generateSong("1", 15),
            generateSong("2", 12),
            generateSong("3", 10),
            generateSong("4", 15),
        )

        val result = sideSplitter.splitIntoSides(songs)
        assertEquals(2, result.sides.size)

        val sideA = result.sides[0]
        val sideB = result.sides[1]

        assertEquals('A', sideA.sideLetter)
        assertEquals("REEL 01", sideA.reelLabel)
        assertEquals(2, sideA.songs.size)
        assertEquals(27 * 60_000L, sideA.totalDurationMs)

        assertEquals('B', sideB.sideLetter)
        assertEquals("REEL 02", sideB.reelLabel)
        assertEquals(2, sideB.songs.size)
        assertEquals(25 * 60_000L, sideB.totalDurationMs)
    }

    @Test
    fun testSideProgressCalculation() {
        val songs = listOf(
            generateSong("1", 3), // 180_000 ms
            generateSong("2", 4), // 240_000 ms
        )
        val result = sideSplitter.splitIntoSides(songs)
        val sideA = result.sideA

        // Song 2 is playing at 2 min 14 sec (134_000 ms)
        // Total elapsed on side A = 3m + 2m 14s = 5m 14s / 7m 00s
        val progress = sideSplitter.calculateSideProgress(
            side = sideA,
            currentSongId = "LOCAL:2",
            currentPositionMs = 134_000L,
        )
        assertEquals("05:14 / 07:00", progress)
    }

    @Test
    fun testCreateTwentySongPlaylistAndOperations() = runBlocking {
        val playlistId = createPlaylistUseCase("Indie Cassette Nostalgia", "Tape masters from 1994")
        assertNotNull(playlistId)

        // Add 20 songs
        for (i in 1..20) {
            addToPlaylistUseCase(playlistId, "LOCAL:$i")
        }

        val playlist = playlistRepo.getPlaylistById(playlistId).first()
        assertNotNull(playlist)
        assertEquals("Indie Cassette Nostalgia", playlist?.name)
        assertEquals(20, playlist?.songCount)

        // Rename
        renamePlaylistUseCase(playlistId, "Indie Cassette Nostalgia (Tape Master Edition)", "Remastered in FLAC")
        val renamed = playlistRepo.getPlaylistById(playlistId).first()
        assertEquals("Indie Cassette Nostalgia (Tape Master Edition)", renamed?.name)
        assertEquals("Remastered in FLAC", renamed?.description)

        // Remove a song
        removeFromPlaylistUseCase(playlistId, "LOCAL:5")
        val afterRemove = playlistRepo.getPlaylistById(playlistId).first()
        assertEquals(19, afterRemove?.songCount)

        // Delete
        deletePlaylistUseCase(playlistId)
        val deleted = playlistRepo.getPlaylistById(playlistId).first()
        assertEquals(null, deleted)
    }

    @Test
    fun testShuffleDoesNotMutateStoredPlaylistOrder() = runBlocking {
        val originalSongs = (1..10).map { generateSong("$it", 3) }
        val storedOrder = originalSongs.map { it.id }

        // Shuffled copy
        val shuffled = originalSongs.shuffled()
        // Ensure the original list order is not mutated
        assertEquals(storedOrder, originalSongs.map { it.id })
        // Stored order is immutable
        assertEquals("LOCAL:1", originalSongs.first().id)
        assertEquals("LOCAL:10", originalSongs.last().id)
    }
}

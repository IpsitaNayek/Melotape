package com.melotape.domain.logic

import com.melotape.domain.model.MusicSource
import com.melotape.domain.model.Song
import com.melotape.domain.model.TapeLength
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SideSplitterTest {

    private val splitter = SideSplitter()

    private fun createSong(id: String, durationMin: Int): Song {
        return Song(
            id = id,
            title = "Track $id",
            artist = "Artist",
            album = "Album",
            artworkUri = null,
            durationMs = durationMin * 60_000L,
            source = MusicSource.Local("content://uri/$id"),
        )
    }

    @Test
    fun testEmptySongsReturnsDefaultC60WithTwoSides() {
        val result = splitter.splitIntoSides(emptyList())
        assertEquals(TapeLength.C60, result.tapeLength)
        assertEquals("C-60 STEREO", result.tapeLengthLabel)
        assertEquals(2, result.sides.size)
        assertEquals('A', result.sides[0].sideLetter)
        assertEquals('B', result.sides[1].sideLetter)
        assertEquals(0, result.totalTracks)
    }

    @Test
    fun testShortTracklistPicksSmallestTapeLength() {
        // 20 minutes total -> C-30 (15 min per side capacity)
        val songs = listOf(
            createSong("1", 10),
            createSong("2", 10),
        )
        val result = splitter.splitIntoSides(songs)
        assertEquals(TapeLength.C30, result.tapeLength)
        assertEquals("C-30 STEREO", result.tapeLengthLabel)
        assertEquals(2, result.sides.size)
        assertEquals(1, result.sides[0].songs.size) // 10 min fits in side 1 (capacity 15)
        assertEquals(1, result.sides[1].songs.size) // next 10 min would exceed 15 min, so goes to side B
    }

    @Test
    fun testMediumTracklistPicksC60AndSplitsCorrectly() {
        // 4 tracks of 12 min each = 48 min total -> fits in C-60 (30 min per side)
        val songs = listOf(
            createSong("1", 12),
            createSong("2", 12), // 24 min on Side A
            createSong("3", 12), // 24+12=36 > 30 min -> Side B
            createSong("4", 12), // Side B
        )
        val result = splitter.splitIntoSides(songs)
        assertEquals(TapeLength.C60, result.tapeLength)
        assertEquals("C-60 STEREO", result.tapeLengthLabel)
        assertEquals(2, result.sides.size)
        assertEquals(2, result.sides[0].songs.size)
        assertEquals(2, result.sides[1].songs.size)
        assertEquals(24 * 60_000L, result.sides[0].totalDurationMs)
        assertEquals(24 * 60_000L, result.sides[1].totalDurationMs)
    }

    @Test
    fun testSideProgressCalculation() {
        val songs = listOf(
            createSong("1", 4), // 4 mins = 240_000 ms
            createSong("2", 3), // 3 mins = 180_000 ms
        )
        val result = splitter.splitIntoSides(songs)
        val sideA = result.sideA

        // Song 2 is playing at 1m 30s (90_000 ms).
        // Total elapsed on side A should be 4m + 1m 30s = 5m 30s out of 7m 00s
        val progress = splitter.calculateSideProgress(
            side = sideA,
            currentSongId = "2",
            currentPositionMs = 90_000L,
        )
        assertEquals("05:30 / 07:00", progress)
    }
}

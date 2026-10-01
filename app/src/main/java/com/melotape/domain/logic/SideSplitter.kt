package com.melotape.domain.logic

import com.melotape.domain.model.Song
import com.melotape.domain.model.TapeLength
import com.melotape.domain.model.TapeSide
import com.melotape.domain.model.TapeSidesResult
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Pure domain logic for calculating analog cassette tape capacity and splitting
 * a tracklist into physical tape sides (Side A, Side B, etc.).
 */
@Singleton
class SideSplitter @Inject constructor() {

    /**
     * Splits a list of songs across cassette sides according to vintage compact cassette standards.
     * Selects the smallest tape length (C-30, C-46, C-60, C-90, C-120) that fits the total duration.
     */
    fun splitIntoSides(songs: List<Song>): TapeSidesResult {
        if (songs.isEmpty()) {
            val defaultLength = TapeLength.C60
            return TapeSidesResult(
                tapeLength = defaultLength,
                tapeLengthLabel = "${defaultLength.code} STEREO",
                sides = listOf(
                    TapeSide('A', 0, "REEL 01", emptyList(), 0L),
                    TapeSide('B', 1, "REEL 02", emptyList(), 0L),
                ),
                totalDurationMs = 0L,
                totalTracks = 0,
            )
        }

        val totalDurationMs = songs.sumOf { it.durationMs }

        // Choose smallest tape length whose total duration fits the content
        val tapeLength = TapeLength.entries.firstOrNull { it.totalDurationMs >= totalDurationMs }
            ?: TapeLength.C120

        val perSideCapacityMs = tapeLength.sideCapacityMs
        val sideList = mutableListOf<TapeSide>()
        var currentSideSongs = mutableListOf<Song>()
        var currentSideDuration = 0L
        var sideIndex = 0

        for (song in songs) {
            // If adding this song would exceed half the tape length (per-side capacity), start next side
            if (currentSideSongs.isNotEmpty() && currentSideDuration + song.durationMs > perSideCapacityMs) {
                val sideLetter = ('A'.code + sideIndex).toChar()
                val reelNum = String.format("%02d", sideIndex + 1)
                sideList.add(
                    TapeSide(
                        sideLetter = sideLetter,
                        sideIndex = sideIndex,
                        reelLabel = "REEL $reelNum",
                        songs = currentSideSongs.toList(),
                        totalDurationMs = currentSideDuration,
                    )
                )
                sideIndex++
                currentSideSongs = mutableListOf()
                currentSideDuration = 0L
            }

            currentSideSongs.add(song)
            currentSideDuration += song.durationMs
        }

        // Add the final remaining side
        if (currentSideSongs.isNotEmpty()) {
            val sideLetter = ('A'.code + sideIndex).toChar()
            val reelNum = String.format("%02d", sideIndex + 1)
            sideList.add(
                TapeSide(
                    sideLetter = sideLetter,
                    sideIndex = sideIndex,
                    reelLabel = "REEL $reelNum",
                    songs = currentSideSongs.toList(),
                    totalDurationMs = currentSideDuration,
                )
            )
            sideIndex++
        }

        // Always guarantee at least Side A and Side B exist for the user experience
        if (sideList.size == 1) {
            sideList.add(
                TapeSide(
                    sideLetter = 'B',
                    sideIndex = 1,
                    reelLabel = "REEL 02",
                    songs = emptyList(),
                    totalDurationMs = 0L,
                )
            )
        }

        return TapeSidesResult(
            tapeLength = tapeLength,
            tapeLengthLabel = "${tapeLength.code} STEREO",
            sides = sideList,
            totalDurationMs = totalDurationMs,
            totalTracks = songs.size,
        )
    }

    /**
     * Calculates the playback progress within the currently active cassette side.
     * Returns a formatted string like "03:52 / 44:20".
     */
    fun calculateSideProgress(
        side: TapeSide,
        currentSongId: String?,
        currentPositionMs: Long,
    ): String {
        val totalSideMs = side.totalDurationMs
        if (totalSideMs <= 0L || currentSongId == null) {
            return "00:00 / ${formatDuration(totalSideMs)}"
        }

        val songIndex = side.songs.indexOfFirst { it.id == currentSongId }
        if (songIndex == -1) {
            return "00:00 / ${formatDuration(totalSideMs)}"
        }

        val elapsedPriorMs = side.songs.take(songIndex).sumOf { it.durationMs }
        val currentSongElapsed = currentPositionMs.coerceIn(0L, side.songs[songIndex].durationMs)
        val elapsedSideMs = (elapsedPriorMs + currentSongElapsed).coerceAtMost(totalSideMs)

        return "${formatDuration(elapsedSideMs)} / ${formatDuration(totalSideMs)}"
    }

    companion object {
        fun formatDuration(durationMs: Long): String {
            val totalSeconds = (durationMs / 1000).coerceAtLeast(0)
            val minutes = totalSeconds / 60
            val seconds = totalSeconds % 60
            return String.format("%02d:%02d", minutes, seconds)
        }

        fun formatTrackCountInfo(trackCount: Int, durationMs: Long): String {
            val totalMinutes = (durationMs / 60000).toInt()
            val hours = totalMinutes / 60
            val mins = totalMinutes % 60
            val timeStr = if (hours > 0) "${hours}h ${mins}m" else "${mins}m"
            return "$trackCount Tracks • $timeStr"
        }
    }
}

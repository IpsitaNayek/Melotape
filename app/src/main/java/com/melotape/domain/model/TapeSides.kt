package com.melotape.domain.model

/**
 * Standard compact cassette formats and lengths.
 */
enum class TapeLength(val code: String, val totalMinutes: Int) {
    C30("C-30", 30),
    C46("C-46", 46),
    C60("C-60", 60),
    C90("C-90", 90),
    C120("C-120", 120);

    val totalDurationMs: Long get() = totalMinutes * 60_000L
    val sideCapacityMs: Long get() = totalDurationMs / 2L
}

/**
 * Represents one side of a cassette (Side A, Side B, etc.)
 */
data class TapeSide(
    val sideLetter: Char,          // 'A', 'B', etc.
    val sideIndex: Int,            // 0, 1, etc.
    val reelLabel: String,         // "REEL 01", "REEL 02"
    val songs: List<Song>,
    val totalDurationMs: Long,
) {
    val trackCount: Int get() = songs.size
}

/**
 * Result of splitting a tracklist into cassette sides.
 */
data class TapeSidesResult(
    val tapeLength: TapeLength,
    val tapeLengthLabel: String,   // e.g. "C-90 STEREO"
    val sides: List<TapeSide>,
    val totalDurationMs: Long,
    val totalTracks: Int,
) {
    val sideA: TapeSide get() = sides.getOrElse(0) { TapeSide('A', 0, "REEL 01", emptyList(), 0L) }
    val sideB: TapeSide get() = sides.getOrElse(1) { TapeSide('B', 1, "REEL 02", emptyList(), 0L) }
}

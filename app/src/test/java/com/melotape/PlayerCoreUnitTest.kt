package com.melotape

import com.melotape.domain.model.MusicSource
import com.melotape.domain.model.Song
import com.melotape.player.model.PlaybackStateUi
import com.melotape.player.resolver.AudioSourceResolver
import org.junit.Assert.*
import org.junit.Test

class PlayerCoreUnitTest {

    private val audioSourceResolver = AudioSourceResolver()

    @Test
    fun testAudioSourceResolverLocalSong() {
        val song = Song(
            id = "LOCAL:42",
            title = "Midnight Analog Session",
            artist = "Vintage Deck",
            album = "Cassette Classics",
            artworkUri = "content://media/external/audio/albumart/10",
            durationMs = 210_000L,
            source = MusicSource.Local("content://media/external/audio/media/42"),
        )

        val uriString = audioSourceResolver.resolveUriString(song)
        assertEquals("content://media/external/audio/media/42", uriString)
    }

    @Test
    fun testAudioSourceResolverJamendoSong() {
        val song = Song(
            id = "JAMENDO:track_99",
            title = "Tokyo Neon Glow",
            artist = "Retro Synth",
            album = "Shinjuku Tape",
            artworkUri = "https://img.jamendo.com/99.jpg",
            durationMs = 180_000L,
            source = MusicSource.Jamendo(
                trackUrl = "https://stream.jamendo.com/99.mp3",
                downloadUrl = null,
                audioDownloadAllowed = true,
                licenseUrl = null,
            ),
        )

        val uriString = audioSourceResolver.resolveUriString(song)
        assertEquals("https://stream.jamendo.com/99.mp3", uriString)
    }

    @Test
    fun testPlaybackStateUiCalculations() {
        val state = PlaybackStateUi(
            isPlaying = true,
            currentPositionMs = 35_000L,
            durationMs = 200_000L,
            repeatMode = 2,
            shuffleEnabled = true,
        )

        assertTrue(state.isPlaying)
        assertEquals(35_000L, state.currentPositionMs)
        assertEquals(200_000L, state.durationMs)
        assertEquals(2, state.repeatMode)
        assertTrue(state.shuffleEnabled)

        // Test forward 10s seek calculation
        val forward10s = (state.currentPositionMs + 10_000L).coerceIn(0L, state.durationMs)
        assertEquals(45_000L, forward10s)

        // Test rewind 10s seek calculation
        val rewind10s = (state.currentPositionMs - 10_000L).coerceIn(0L, state.durationMs)
        assertEquals(25_000L, rewind10s)

        // Test seek beyond duration clamps cleanly
        val overflowSeek = (state.currentPositionMs + 300_000L).coerceIn(0L, state.durationMs)
        assertEquals(200_000L, overflowSeek)

        // Test seek before 0 clamps cleanly
        val underflowSeek = (state.currentPositionMs - 50_000L).coerceIn(0L, state.durationMs)
        assertEquals(0L, underflowSeek)
    }
}

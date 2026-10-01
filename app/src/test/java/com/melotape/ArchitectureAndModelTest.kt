package com.melotape

import com.melotape.data.fake.FakeMusicRepository
import com.melotape.data.fake.FakePlaylistRepository
import com.melotape.data.fake.FakeUserRepository
import com.melotape.domain.model.MusicSource
import com.melotape.domain.model.Song
import com.melotape.domain.model.SourcePrefix
import com.melotape.ui.mapper.toItemUi
import com.melotape.ui.mapper.toProfileUi
import com.melotape.ui.model.DownloadStatus
import com.melotape.ui.model.SongItemUi
import com.melotape.ui.model.SourceBadgeUi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class ArchitectureAndModelTest {

    @Test
    fun testSongItemUiCreation() {
        val songItem = SongItemUi(
            id = "LOCAL:101",
            title = "Midnight Tape",
            subtitle = "Vintage Reel • 3:45",
            artwork = null,
            formatLabel = "FLAC 24",
            source = SourceBadgeUi.Device,
            isLoved = true,
            download = DownloadStatus.Completed,
        )

        assertEquals("LOCAL:101", songItem.id)
        assertEquals("Midnight Tape", songItem.title)
        assertTrue(songItem.isLoved)
        assertEquals(DownloadStatus.Completed, songItem.download)
    }

    @Test
    fun testSourcePrefixConstants() {
        assertEquals("LOCAL:123", SourcePrefix.local(123L))
        assertEquals("JAMENDO:jam_456", SourcePrefix.jamendo("jam_456"))
        assertEquals("FIREBASE:fb_789", SourcePrefix.firebase("fb_789"))
    }

    @Test
    fun testUiMappers() {
        val song = Song(
            id = "JAMENDO:j1",
            title = "Tokyo City Lights",
            artist = "Neon Deck",
            album = "Shinjuku 1984",
            artworkUri = null,
            durationMs = 215_000L,
            source = MusicSource.Jamendo("https://track.url", null, true, null),
            mimeType = "audio/flac",
            bitrate = 960,
            downloadAllowed = true,
        )

        val uiModel = song.toItemUi()
        assertEquals("JAMENDO:j1", uiModel.id)
        assertEquals("Tokyo City Lights", uiModel.title)
        assertEquals("Neon Deck • 3:35", uiModel.subtitle)
        assertEquals(SourceBadgeUi.Jamendo, uiModel.source)
        assertEquals("FLAC 24", uiModel.formatLabel)
        assertEquals(DownloadStatus.Completed, uiModel.download)
    }

    @Test
    fun testLocalSongModelAndMapping() {
        val localSong = Song(
            id = SourcePrefix.local(9876L),
            title = "Acoustic Cassette Session",
            artist = "Vintage Tapes",
            album = "Analog Archives Vol. 1",
            artworkUri = null,
            durationMs = 184_000L,
            source = MusicSource.Local("content://media/external/audio/media/9876"),
            mimeType = "audio/flac",
            bitrate = 1411,
            downloadAllowed = true,
        )

        val uiModel = localSong.toItemUi()
        assertEquals("LOCAL:9876", uiModel.id)
        assertEquals("Acoustic Cassette Session", uiModel.title)
        assertEquals("Vintage Tapes • 3:04", uiModel.subtitle)
        assertEquals(SourceBadgeUi.Device, uiModel.source)
        assertEquals("FLAC 24", uiModel.formatLabel)
        assertEquals(DownloadStatus.Completed, uiModel.download)
    }

    @Test
    fun testFakeMusicRepositoryFlows() = runBlocking {
        val repo = FakeMusicRepository()
        val recents = repo.getRecentlyPlayed().first()
        assertTrue(recents.isNotEmpty())

        val firstSong = recents.first()
        val initialLoved = firstSong.isLoved
        repo.toggleLoved(firstSong.id)

        val updatedSong = repo.getSongById(firstSong.id).first()
        assertNotNull(updatedSong)
        assertEquals(!initialLoved, updatedSong?.isLoved)
    }

    @Test
    fun testFakePlaylistRepositoryFlows() = runBlocking {
        val repo = FakePlaylistRepository()
        val playlists = repo.getPlaylists().first()
        assertTrue(playlists.isNotEmpty())

        val newId = repo.createPlaylist("Midnight Cassette", "Analog beats")
        val created = repo.getPlaylistById(newId).first()
        assertNotNull(created)
        assertEquals("Midnight Cassette", created?.name)
    }

    @Test
    fun testFakeUserRepositoryFlows() = runBlocking {
        val repo = FakeUserRepository()
        val user = repo.getUserProfile().first()
        assertEquals("Alex", user.displayName)
        assertTrue(user.isProDeck)

        val profileUi = user.toProfileUi()
        assertEquals("Alex", profileUi.displayName)
        assertEquals(28, profileUi.mixtapeCount)
    }

    @Test
    fun testUiComponentsDoNotImportDataOrDomainOrHilt() {
        // Enforce Phase 1 & 2 rule: ui/components must not import data/, domain/, or hilt
        val componentsDir = File("src/main/java/com/melotape/ui/components")
        if (!componentsDir.exists()) return

        val files = componentsDir.listFiles { file -> file.extension == "kt" } ?: emptyArray()
        for (file in files) {
            val lines = file.readLines()
            for (line in lines) {
                if (line.startsWith("import com.melotape.data.") ||
                    line.startsWith("import com.melotape.domain.") ||
                    line.startsWith("import dagger.hilt.") ||
                    line.startsWith("import androidx.hilt.")
                ) {
                    throw AssertionError("Forbidden import in component ${file.name}: $line")
                }
            }
        }
    }
}

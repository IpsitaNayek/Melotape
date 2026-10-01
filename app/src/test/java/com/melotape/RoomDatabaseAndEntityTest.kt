package com.melotape

import com.melotape.data.db.entity.*
import com.melotape.domain.model.MusicSource
import com.melotape.domain.model.Song
import com.melotape.domain.model.SourcePrefix
import com.melotape.ui.model.DownloadStatus
import org.junit.Assert.*
import org.junit.Test

class RoomDatabaseAndEntityTest {

    @Test
    fun testSourceAwareIdGuardsAgainstCollisions() {
        val localId = SourcePrefix.local(100L)
        val jamendoId = SourcePrefix.jamendo("100")
        val firebaseId = SourcePrefix.firebase("100")

        assertNotEquals(localId, jamendoId)
        assertNotEquals(localId, firebaseId)
        assertNotEquals(jamendoId, firebaseId)

        assertEquals("LOCAL:100", localId)
        assertEquals("JAMENDO:100", jamendoId)
        assertEquals("FIREBASE:100", firebaseId)
    }

    @Test
    fun testSongEntityToDomainAndBack() {
        val original = Song(
            id = "JAMENDO:track_777",
            title = "Sunset Cassette",
            artist = "Analog Dreams",
            album = "Tape Deck Chronicles",
            artworkUri = "https://artwork.url/777.jpg",
            durationMs = 240_000L,
            source = MusicSource.Jamendo(
                trackUrl = "https://audio.url/777.mp3",
                downloadUrl = "https://download.url/777.mp3",
                audioDownloadAllowed = true,
                licenseUrl = null,
            ),
            year = 1989,
            genre = "Synthwave",
            mimeType = "audio/mp3",
            fileSizeBytes = 8_500_000L,
            bitrate = 320,
            sampleRate = 44100,
            trackNumber = 3,
            isLoved = true,
            downloadAllowed = true,
        )

        val entity = original.toEntity()
        assertEquals("JAMENDO:track_777", entity.id)
        assertEquals("Sunset Cassette", entity.title)
        assertEquals("JAMENDO", entity.sourceType)
        assertEquals("https://audio.url/777.mp3", entity.sourceData)
        assertEquals("https://download.url/777.mp3", entity.extra)

        val restored = entity.toDomain(isLoved = true)
        assertEquals(original.id, restored.id)
        assertEquals(original.title, restored.title)
        assertEquals(original.artist, restored.artist)
        assertEquals(original.album, restored.album)
        assertEquals(original.durationMs, restored.durationMs)
        assertEquals(original.year, restored.year)
        assertEquals(original.genre, restored.genre)
        assertEquals(original.bitrate, restored.bitrate)
        assertTrue(restored.isLoved)
        assertTrue(restored.source is MusicSource.Jamendo)
    }

    @Test
    fun testPlaylistEntityMapping() {
        val entity = PlaylistEntity(
            id = "pl_1",
            name = "Late Night Drive",
            description = "Cruising with vintage tape deck",
            coverArtUri = null,
            pinned = true,
            createdAt = 1000L,
            updatedAt = 2000L,
            cloudId = "cloud_99",
            syncState = "SYNCED",
            deleted = false,
        )

        val domain = entity.toDomain(songCount = 14, totalDurationMs = 3_200_000L)
        assertEquals("pl_1", domain.id)
        assertEquals("Late Night Drive", domain.name)
        assertEquals("Cruising with vintage tape deck", domain.description)
        assertTrue(domain.isPinned)
        assertEquals(14, domain.songCount)
        assertEquals(3_200_000L, domain.totalDurationMs)
        assertEquals("cloud_99", domain.cloudId)
    }

    @Test
    fun testPlayHistoryEntityCumulativeHoursSpunCalculation() {
        val entry1 = PlayHistoryEntity(id = 1, songId = "LOCAL:1", playedAt = 1000L, listenedMs = 1_800_000L) // 0.5 hours
        val entry2 = PlayHistoryEntity(id = 2, songId = "JAMENDO:2", playedAt = 2000L, listenedMs = 5_400_000L) // 1.5 hours

        val totalListenedMs = entry1.listenedMs + entry2.listenedMs
        val hoursSpun = totalListenedMs / (1000.0 * 60.0 * 60.0)

        assertEquals(7_200_000L, totalListenedMs)
        assertEquals(2.0, hoursSpun, 0.001)
    }

    @Test
    fun testDownloadEntityStatusMapping() {
        val completedDownload = DownloadEntity(
            id = "LOCAL:5",
            progress = 100,
            status = "COMPLETED",
            fileSize = 15_000_000L,
            originSourceId = "LOCAL:5",
        )
        val downloading = DownloadEntity(
            id = "JAMENDO:6",
            progress = 65,
            status = "DOWNLOADING",
            fileSize = 20_000_000L,
            originSourceId = "JAMENDO:6",
        )

        assertEquals(DownloadStatus.Completed, completedDownload.toUiStatus())
        val downloadingStatus = downloading.toUiStatus()
        assertTrue(downloadingStatus is DownloadStatus.Downloading)
        assertEquals(0.65f, (downloadingStatus as DownloadStatus.Downloading).progress, 0.01f)
    }
}

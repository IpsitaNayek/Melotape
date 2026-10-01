package com.melotape.domain.usecase

import com.melotape.data.fake.FakePlaylistRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class PlaylistUseCasesTest {

    private val fakeRepo = FakePlaylistRepository()
    private val createUseCase = CreatePlaylistUseCase(fakeRepo)
    private val renameUseCase = RenamePlaylistUseCase(fakeRepo)
    private val deleteUseCase = DeletePlaylistUseCase(fakeRepo)
    private val addToPlaylistUseCase = AddToPlaylistUseCase(fakeRepo)

    @Test
    fun testCreateAndRenamePlaylist() = runBlocking {
        val id = createUseCase("Analog Gold", "Custom tape mix")
        val created = fakeRepo.getPlaylistById(id).first()
        assertNotNull(created)
        assertEquals("Analog Gold", created?.name)
        assertEquals("Custom tape mix", created?.description)

        renameUseCase(id, "Analog Gold Vol. 2", "Remastered")
        val updated = fakeRepo.getPlaylistById(id).first()
        assertEquals("Analog Gold Vol. 2", updated?.name)
        assertEquals("Remastered", updated?.description)
    }

    @Test
    fun testAddSongIncrementsCount() = runBlocking {
        val id = createUseCase("Test Tape")
        val before = fakeRepo.getPlaylistById(id).first()
        assertEquals(0, before?.songCount)

        addToPlaylistUseCase(id, "LOCAL:101")
        val after = fakeRepo.getPlaylistById(id).first()
        assertEquals(1, after?.songCount)
    }

    @Test
    fun testDeletePlaylist() = runBlocking {
        val id = createUseCase("Temporary Tape")
        val beforeList = fakeRepo.getPlaylists().first()
        assertTrue(beforeList.any { it.id == id })

        deleteUseCase(id)
        val afterList = fakeRepo.getPlaylists().first()
        assertTrue(afterList.none { it.id == id })
    }
}

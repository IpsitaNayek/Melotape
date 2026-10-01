package com.melotape.domain.usecase

import com.melotape.domain.repository.PlaylistRepository
import javax.inject.Inject

class CreatePlaylistUseCase @Inject constructor(
    private val playlistRepository: PlaylistRepository,
) {
    suspend operator fun invoke(name: String, description: String? = null): String {
        require(name.isNotBlank()) { "Mixtape title cannot be empty" }
        return playlistRepository.createPlaylist(name.trim(), description?.trim())
    }
}

class AddToPlaylistUseCase @Inject constructor(
    private val playlistRepository: PlaylistRepository,
) {
    suspend operator fun invoke(playlistId: String, songId: String) {
        playlistRepository.addSongToPlaylist(playlistId, songId)
    }
}

class RemoveFromPlaylistUseCase @Inject constructor(
    private val playlistRepository: PlaylistRepository,
) {
    suspend operator fun invoke(playlistId: String, songId: String) {
        playlistRepository.removeSongFromPlaylist(playlistId, songId)
    }
}

class ReorderPlaylistUseCase @Inject constructor(
    private val playlistRepository: PlaylistRepository,
) {
    suspend operator fun invoke(playlistId: String, songIdsInOrder: List<String>) {
        playlistRepository.reorderPlaylist(playlistId, songIdsInOrder)
    }
}

class RenamePlaylistUseCase @Inject constructor(
    private val playlistRepository: PlaylistRepository,
) {
    suspend operator fun invoke(playlistId: String, newName: String, newDescription: String? = null) {
        require(newName.isNotBlank()) { "Mixtape title cannot be empty" }
        playlistRepository.renamePlaylist(playlistId, newName.trim(), newDescription?.trim())
    }
}

class DeletePlaylistUseCase @Inject constructor(
    private val playlistRepository: PlaylistRepository,
) {
    suspend operator fun invoke(playlistId: String) {
        playlistRepository.deletePlaylist(playlistId)
    }
}

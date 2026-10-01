package com.melotape.domain.usecase

import com.melotape.domain.repository.MusicRepository
import javax.inject.Inject

class ToggleLovedUseCase @Inject constructor(
    private val musicRepository: MusicRepository,
) {
    suspend operator fun invoke(songId: String) {
        musicRepository.toggleLoved(songId)
    }
}

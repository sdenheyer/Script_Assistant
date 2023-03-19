package com.stevedenheyer.scriptassistant.audioeditor.domain.usecases

import com.stevedenheyer.scriptassistant.common.domain.model.audio.AudioDetails
import com.stevedenheyer.scriptassistant.common.domain.repositories.AudioRepository
import com.stevedenheyer.scriptassistant.data.AudioDetailsDB
import javax.inject.Inject

class UpdateAudioDetails @Inject constructor(private val audioRepository: AudioRepository) {
    suspend operator fun invoke(id: Long, details: AudioDetails) {
        audioRepository.updateAudioDetails(AudioDetailsDB.fromDomain(projectId = id, details = details))
    }
}
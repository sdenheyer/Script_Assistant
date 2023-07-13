package com.stevedenheyer.scriptassistant.editor.domain.usecases

import com.stevedenheyer.scriptassistant.common.domain.repositories.AudioRepository
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetSettings @Inject constructor (private val audioRepository: AudioRepository) {
    operator fun invoke(id: Long) = audioRepository.getAudioAggregate(id).map {
        it.associateBy { it.audioOwnerId }.mapValues { it.value.settings }
    }
}
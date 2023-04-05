package com.stevedenheyer.scriptassistant.audioeditor.domain.usecases

import com.stevedenheyer.scriptassistant.common.domain.repositories.AudioRepository
import com.stevedenheyer.scriptassistant.audioeditor.domain.model.SentencesCollection
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetSentences @Inject constructor(private val audioRepository: AudioRepository) {
    operator fun invoke(projectId: Long) = audioRepository.getAudioAggregate(projectId).map {
        it.associateBy { it.audioOwnerId }.mapValues { SentencesCollection(it.value.audioOwnerId, it.value.sentences.toList()) }
    }
}
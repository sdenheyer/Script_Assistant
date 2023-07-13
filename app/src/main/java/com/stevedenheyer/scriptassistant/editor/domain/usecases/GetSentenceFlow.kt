package com.stevedenheyer.scriptassistant.editor.domain.usecases

import com.stevedenheyer.scriptassistant.common.domain.repositories.SentencesRepository
import javax.inject.Inject

class GetSentenceFlow @Inject constructor(private val repository: SentencesRepository) {
    operator fun invoke() = repository.getSentencesCollectionFlow()
}
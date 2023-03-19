package com.stevedenheyer.scriptassistant.audioeditor.domain.usecases

import com.stevedenheyer.scriptassistant.common.domain.model.audio.Settings
import com.stevedenheyer.scriptassistant.common.domain.repositories.SentencesRepository
import com.stevedenheyer.scriptassistant.audioeditor.domain.model.SentencesDetails
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetSentencesDetails @Inject constructor(private val repository: SentencesRepository) {
    operator fun invoke() = repository.getSentencesCollectionFlow().map { sentenceCollection ->
        val id = 0L
        val settings = Settings(threshold = 0, pause = 0)
        val sentencesDetails = SentencesDetails(id = id,
            sentences = sentenceCollection.data,
            //waveform = emptyArray<Byte>().toByteArray(),
            settings = settings)
        sentencesDetails
    }
}
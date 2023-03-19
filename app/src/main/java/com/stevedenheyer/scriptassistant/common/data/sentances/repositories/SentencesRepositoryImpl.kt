package com.stevedenheyer.scriptassistant.common.data.sentances.repositories

import com.stevedenheyer.scriptassistant.common.domain.repositories.WaveformRepository
import com.stevedenheyer.scriptassistant.audioeditor.domain.model.Sentence
import com.stevedenheyer.scriptassistant.audioeditor.domain.model.SentencesCollection
import com.stevedenheyer.scriptassistant.audioeditor.domain.model.Waveform
import com.stevedenheyer.scriptassistant.common.data.room.daos.ProjectDao
import com.stevedenheyer.scriptassistant.common.data.sentances.FindSentences
import com.stevedenheyer.scriptassistant.common.domain.repositories.SentencesRepository
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class SentencesRepositoryImpl @Inject constructor(private val waveformRepository: WaveformRepository):SentencesRepository {

    private val waveform = MutableStateFlow(Waveform(id = 0, data = emptyArray<Byte>().toByteArray(), isLoading = true))

    private val finder = FindSentences(waveform.map { it.data })

    override fun setThreshold(level: Float) = finder.setThreshold(level)

    override fun getThreshold() = finder.getThreshold()

    override fun setPause(pause: Float) = finder.setPauseLength(pause)

    override fun getPause() = finder.getPauseLength()

    override fun getSentencesCollectionFlow() = finder.getSentanceListFlow().transform { genSentences ->
            val sentences = SentencesCollection(0, genSentences.map {
                Sentence(it, null, null)
            }.toTypedArray())

            emit(sentences)
        }

    override fun getWaveformFlow() = waveform

    override fun setWaveform(value: Waveform) {
        waveform.value = value
    }

}
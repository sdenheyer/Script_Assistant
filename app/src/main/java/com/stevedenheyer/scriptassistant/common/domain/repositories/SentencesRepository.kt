package com.stevedenheyer.scriptassistant.common.domain.repositories

import com.stevedenheyer.scriptassistant.editor.domain.model.SentencesCollection
import com.stevedenheyer.scriptassistant.editor.domain.model.Waveform
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

interface SentencesRepository {
    fun getSentencesCollectionFlow() : Flow<SentencesCollection>

    fun setThreshold(level: Float)

    fun getThreshold(): Flow<Float>

    fun setPause(pause: Float)

    fun getPause(): MutableStateFlow<Float>

    fun getWaveformFlow(): Flow<Waveform>

    fun setWaveform(waveform: Waveform)
}
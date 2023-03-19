package com.stevedenheyer.scriptassistant.common.domain.repositories

import com.stevedenheyer.scriptassistant.audioeditor.domain.model.SentencesCollection
import com.stevedenheyer.scriptassistant.audioeditor.domain.model.Waveform
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

interface SentencesRepository {
    fun getSentencesCollectionFlow() : Flow<SentencesCollection>

    fun setThreshold(level: Float)

    fun getThreshold(): Flow<Float>

    fun setPause(pause: Float)

    fun getPause(): MutableStateFlow<Float>

    fun getWaveformFlow(): Flow<Waveform>

    fun setWaveform(waveform: Waveform)
}
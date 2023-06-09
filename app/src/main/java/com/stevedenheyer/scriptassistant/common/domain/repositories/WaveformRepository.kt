package com.stevedenheyer.scriptassistant.common.domain.repositories

import com.stevedenheyer.scriptassistant.audioeditor.domain.model.Waveform
import java.io.File
import kotlinx.coroutines.flow.SharedFlow

interface WaveformRepository {

    fun getWaveformMapFlow() : SharedFlow<List<Waveform>>

    fun generateWaveforms(files: Map<Long, File>) : SharedFlow<List<Waveform>>

}
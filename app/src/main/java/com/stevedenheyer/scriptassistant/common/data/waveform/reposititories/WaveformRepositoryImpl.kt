package com.stevedenheyer.scriptassistant.common.data.waveform.reposititories

import com.stevedenheyer.scriptassistant.common.data.waveform.WaveformsCollector
import com.stevedenheyer.scriptassistant.common.domain.repositories.WaveformRepository
import java.io.File
import javax.inject.Inject

class WaveformRepositoryImpl @Inject constructor(private val waveformsCollector: WaveformsCollector) : WaveformRepository {

    override fun generateWaveforms(files: Map<Long, File>) = waveformsCollector.generateWaveforms(files)

    override fun getWaveformMapFlow() = waveformsCollector.getWaveformsMapFlow()

}
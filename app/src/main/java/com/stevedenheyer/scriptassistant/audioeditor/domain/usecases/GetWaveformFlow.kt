package com.stevedenheyer.scriptassistant.audioeditor.domain.usecases

import com.stevedenheyer.scriptassistant.common.domain.repositories.WaveformRepository
import javax.inject.Inject

class GetWaveformFlow @Inject constructor(private val waveformRepository: WaveformRepository) {
    operator fun invoke() = waveformRepository.getWaveformMapFlow()
}
package com.stevedenheyer.scriptassistant.editor.domain.usecases

import com.stevedenheyer.scriptassistant.common.domain.repositories.AudioRepository
import com.stevedenheyer.scriptassistant.common.domain.repositories.WaveformRepository
import kotlinx.coroutines.flow.flatMapConcat
import javax.inject.Inject

class GetWaveformMapFlow @Inject constructor (private val waveformRepository: WaveformRepository) {
    operator fun invoke() = waveformRepository.getWaveformMapFlow()
}
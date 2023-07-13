package com.stevedenheyer.scriptassistant.editor.domain.usecases

import com.stevedenheyer.scriptassistant.common.domain.repositories.AudioRepository
import com.stevedenheyer.scriptassistant.common.domain.repositories.WaveformRepository
import kotlinx.coroutines.flow.flatMapConcat
import java.io.File
import javax.inject.Inject

class GenerateWaveform @Inject constructor (private val waveformRepository: WaveformRepository) {
    operator fun invoke(fileList: Map<Long, File>) =  waveformRepository.generateWaveforms(fileList)
    }
package com.stevedenheyer.scriptassistant.audioeditor.domain.usecases

import android.util.Log
import com.stevedenheyer.scriptassistant.audioeditor.domain.model.Waveform
import com.stevedenheyer.scriptassistant.common.domain.repositories.AudioRepository
import com.stevedenheyer.scriptassistant.common.domain.repositories.WaveformRepository
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.transform
import javax.inject.Inject

class GetWaveformMapFlow @Inject constructor (private val audioRepository: AudioRepository, private val waveformRepository: WaveformRepository) {
    operator fun invoke(id: Long) = audioRepository.getAudioAggregate(id).flatMapConcat { details ->
        val fileList = details.associateBy { it.id }.mapValues { it.value.audioFile }
        waveformRepository.generateWaveforms(fileList) }
    }
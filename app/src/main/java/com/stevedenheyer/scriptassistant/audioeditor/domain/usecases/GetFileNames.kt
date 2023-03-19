package com.stevedenheyer.scriptassistant.audioeditor.domain.usecases

import android.util.Log
import com.stevedenheyer.scriptassistant.common.domain.repositories.AudioRepository
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetFileNames @Inject constructor(private val audioRepository: AudioRepository) {
    operator fun invoke(projectId: Long) = audioRepository.getAudioAggregate(projectId).map {
        Log.d("FUC", "Audio details: ${it.size}, ${it}")
        it.associateBy { it.audioOwnerId }.mapValues { it.value.audioFile }
    }
}
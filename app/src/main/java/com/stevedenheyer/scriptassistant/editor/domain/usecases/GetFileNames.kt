package com.stevedenheyer.scriptassistant.editor.domain.usecases

import android.util.Log
import com.stevedenheyer.scriptassistant.common.domain.repositories.AudioRepository
import kotlinx.coroutines.flow.map
import java.io.File
import javax.inject.Inject

class GetFileNames @Inject constructor(private val audioRepository: AudioRepository) {
    operator fun invoke(projectId: Long) = audioRepository.getAudioFiles(projectId).map {
        Log.d("FUC", "Audio details: ${it.size}, ${it}")
        it.associateBy { it.id }.mapValues { it.value.file }
    }
}
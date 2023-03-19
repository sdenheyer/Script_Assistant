package com.stevedenheyer.scriptassistant.filebrowser.domain.usecases

import android.util.Log
import com.stevedenheyer.scriptassistant.common.domain.repositories.AudioRepository
import com.stevedenheyer.scriptassistant.data.AudioDetailsDB
import com.stevedenheyer.scriptassistant.data.AudioFileDB
import com.stevedenheyer.scriptassistant.data.ProjectAudiofilesCrossRef
import javax.inject.Inject

class CreateNewAudioData @Inject constructor(private val audioRepository: AudioRepository) {
    suspend operator fun invoke(id: Long, filepath: String) {
        Log.d("TEMP", "creating")
        val audioFile = AudioFileDB(filepath)
        val audioId = audioRepository.insertAudioFile(audioFile)
        Log.d("TEMP", "creating: $audioId")
        val projectAndAudio = ProjectAudiofilesCrossRef(id, audioId)
        audioRepository.insertProjectAndAudio(projectAndAudio)
        audioRepository.insertAudioDetails(AudioDetailsDB(id, audioId))
    }
}

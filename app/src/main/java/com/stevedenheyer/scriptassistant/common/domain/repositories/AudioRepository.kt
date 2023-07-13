package com.stevedenheyer.scriptassistant.common.domain.repositories

import com.stevedenheyer.scriptassistant.common.data.room.model.AudioAggregateDB
import com.stevedenheyer.scriptassistant.common.domain.model.audio.AudioDetails
import com.stevedenheyer.scriptassistant.common.domain.model.audio.AudioFile
import com.stevedenheyer.scriptassistant.data.AudioDetailsDB
import com.stevedenheyer.scriptassistant.data.AudioFileDB
import com.stevedenheyer.scriptassistant.data.ProjectAudiofilesCrossRef
import kotlinx.coroutines.flow.Flow

interface AudioRepository {

    suspend fun insertAudioFile(audioFileDB: AudioFileDB) : Long

    suspend fun insertProjectAndAudio(projectAndAudio: ProjectAudiofilesCrossRef) : Long

    fun getProjectWithAudio(id: Long) : Flow<AudioAggregateDB>

    //suspend fun getProjectAndAudio() : AudioAggregate

    suspend fun updateAudio(audioDB: AudioFileDB?)

    suspend fun insertAudioDetails(audioDetailsDB: AudioDetailsDB)

    suspend fun updateAudioDetails (audioDetailsDB: AudioDetailsDB)

    fun getAudioAggregate (id: Long): Flow<List<AudioDetails>>

    fun getAudioFiles (id: Long): Flow<List<AudioFile>>

}
package com.stevedenheyer.scriptassistant.common.data.room.daos

import androidx.room.*
import com.stevedenheyer.scriptassistant.common.data.room.model.AudioAggregateDB
import com.stevedenheyer.scriptassistant.data.AudioFileDB
import com.stevedenheyer.scriptassistant.data.ProjectAudiofilesCrossRef
import kotlinx.coroutines.flow.Flow

@Dao
interface AudioFileDao {
    @Insert
    suspend fun insertAudio(audioFileDB: AudioFileDB) : Long

    @Insert
    suspend fun insertProjectAndAudio(projectAndAudio: ProjectAudiofilesCrossRef): Long

    @Transaction
    @Query("SELECT * FROM projectdb WHERE :id IS projectId")
    fun getProjectWithAudioById(id: Long?) : Flow<AudioAggregateDB>

    @Query("SELECT * FROM projectaudiofilescrossref")
    suspend fun projectAndAudio() : List<ProjectAudiofilesCrossRef>

    @Update
    suspend fun updateAudio(audioDB: AudioFileDB?)
}
package com.stevedenheyer.scriptassistant.common.data.room.repositories

import com.stevedenheyer.scriptassistant.common.data.room.daos.AudioDetailsDao
import com.stevedenheyer.scriptassistant.common.data.room.daos.AudioFileDao
import com.stevedenheyer.scriptassistant.common.domain.model.audio.AudioDetails
import com.stevedenheyer.scriptassistant.common.domain.repositories.AudioRepository
import com.stevedenheyer.scriptassistant.data.AudioDetailsDB
import com.stevedenheyer.scriptassistant.data.AudioFileDB
import com.stevedenheyer.scriptassistant.data.ProjectAudiofilesCrossRef
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.io.File
import javax.inject.Inject

class AudioRepositoryImpl @Inject constructor(private val audioFilesDao: AudioFileDao,
                                                private val audioDetailsDao: AudioDetailsDao,
                                                    ) : AudioRepository {

    override suspend fun insertAudioFile(audioFileDB: AudioFileDB) : Long = audioFilesDao.insertAudio(audioFileDB)

    override suspend fun insertProjectAndAudio(projectAndAudio: ProjectAudiofilesCrossRef) : Long = audioFilesDao.insertProjectAndAudio(projectAndAudio)

    override fun getProjectWithAudio(id: Long) = audioFilesDao.getProjectWithAudioById(id)

    override suspend fun updateAudio(audioDB: AudioFileDB) = audioFilesDao.updateAudio(audioDB)

    override suspend fun insertAudioDetails(audioDetailsDB: AudioDetailsDB) = audioDetailsDao.insertAudioDetails(audioDetailsDB)

    override suspend fun updateAudioDetails(audioDetailsDB: AudioDetailsDB) = audioDetailsDao.updateAudioDetails(audioDetailsDB)

    override fun getAudioAggregate(id: Long) = audioDetailsDao.getAudioAggregate(id).map { audioAggregate ->
            audioAggregate.toDomain()
        }

    override fun getAudioFiles(id: Long) = audioFilesDao.getAudioFilePathsFlow(id).map {audioFileDB ->
        audioFileDB.toDomain()
    }
}
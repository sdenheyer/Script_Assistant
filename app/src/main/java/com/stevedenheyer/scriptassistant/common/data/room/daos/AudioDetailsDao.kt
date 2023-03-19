package com.stevedenheyer.scriptassistant.common.data.room.daos

import androidx.room.*
import com.stevedenheyer.scriptassistant.common.data.room.model.AudioAggregateDB
import com.stevedenheyer.scriptassistant.data.AudioDetailsDB
import kotlinx.coroutines.flow.Flow

@Dao
interface AudioDetailsDao {
    @Insert
    suspend fun insertAudioDetails(audioDetailsDB: AudioDetailsDB)

    @Update
    suspend fun updateAudioDetails(audioDetailsDB: AudioDetailsDB)

   @Transaction
   @Query("SELECT * FROM ProjectDB WHERE :id IS projectId")
   fun getAudioAggregate(id: Long) : Flow<AudioAggregateDB>

}
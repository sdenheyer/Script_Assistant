package com.stevedenheyer.scriptassistant.data

import androidx.room.*
import com.stevedenheyer.scriptassistant.common.data.room.model.ProjectDB

@Entity
data class AudioFileDB(
    var audioFilePath: String,
    @PrimaryKey(autoGenerate = true) var audioId: Long = 0,
)

@Entity(primaryKeys = ["projectId", "audioId"], indices = [Index(value = ["audioId"])])
data class ProjectAudiofilesCrossRef(
    val projectId: Long,
    val audioId: Long,
)

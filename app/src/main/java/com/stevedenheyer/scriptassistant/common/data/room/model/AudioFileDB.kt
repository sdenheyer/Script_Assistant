package com.stevedenheyer.scriptassistant.data

import androidx.room.*
import com.stevedenheyer.scriptassistant.common.data.room.model.ProjectDB
import com.stevedenheyer.scriptassistant.common.domain.model.audio.AudioFile
import java.io.File

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

data class ProjectWithAudioFilesDB(
    @Embedded val project: ProjectDB,
    @Relation(
        parentColumn = "projectId",
        entityColumn = "audioId",
        associateBy = Junction(ProjectAudiofilesCrossRef::class)
    )
    val audioFileDBS: List<AudioFileDB>
) {
    fun toDomain(): List<AudioFile> {
        return audioFileDBS.map { AudioFile(id = it.audioId, file = File(it.audioFilePath)) }
    }
}

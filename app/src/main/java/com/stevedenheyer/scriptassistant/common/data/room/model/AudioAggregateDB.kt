package com.stevedenheyer.scriptassistant.common.data.room.model

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.stevedenheyer.scriptassistant.common.domain.model.audio.AudioDetails
import com.stevedenheyer.scriptassistant.common.domain.model.audio.Settings
import com.stevedenheyer.scriptassistant.data.AudioDetailsDB
import com.stevedenheyer.scriptassistant.data.AudioFileDB
import com.stevedenheyer.scriptassistant.data.ProjectAudiofilesCrossRef
import java.io.File

data class AudioAggregateDB(
    @Embedded val projectDB: ProjectDB,

    @Relation(
        parentColumn = "projectId",
        entityColumn = "audioId",
        associateBy = Junction(ProjectAudiofilesCrossRef::class)
    )
    val audioFileDBS: List<AudioFileDB>,

    @Relation(
        parentColumn = "projectId",
        entityColumn = "projectOwnerId",
    )
    val audioDetailsDBS: List<AudioDetailsDB>

) {
    fun toDomain(): List<AudioDetails> {
        val list = ArrayList<AudioDetails>()

        audioFileDBS.forEach {
            val name = it.audioFilePath.replaceBeforeLast("/", "").removePrefix("/")

            val position = audioDetailsDBS.indexOfFirst { item ->
                it.audioId == item.audioOwnerId
            }

            if (position == -1) {
                list.add(
                    AudioDetails(
                        id = -1,
                        audioOwnerId = it.audioId,
                        name = name,
                        audioFile = File(it.audioFilePath),
                        settings = Settings(0F, 0F),
                        sentences = emptyArray(),
                    )
                )
            } else {
                list.add(
                    AudioDetails(
                        id = audioDetailsDBS[position].id,
                        audioOwnerId = it.audioId,
                        name = name,
                        audioFile = File(it.audioFilePath),
                        settings = audioDetailsDBS[position].toDomainSettings(),
                        sentences = audioDetailsDBS[position].toDomainSentences(),
                    )
                )
            }
        }

        return list
    }
}
package com.stevedenheyer.scriptassistant.data

import androidx.room.*
import com.stevedenheyer.scriptassistant.common.data.room.model.SentenceDB
import com.stevedenheyer.scriptassistant.common.domain.model.audio.AudioDetails
import com.stevedenheyer.scriptassistant.common.domain.model.audio.Settings
import com.stevedenheyer.scriptassistant.audioeditor.domain.model.Sentence
import kotlin.math.roundToInt

@Entity
data class AudioDetailsDB(
    var projectOwnerId: Long,
    var audioOwnerId: Long,
    var threshold: Int = 0,
    var pause: Int = 0,
    var sentenceDBS: Array<SentenceDB> = emptyArray(),
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
) {
    companion object {
        fun fromDomain(projectId: Long, details: AudioDetails) =
            AudioDetailsDB(
                projectId,
                details.audioOwnerId,
                details.settings.threshold.roundToInt(),
                details.settings.pause.roundToInt(),
                details.sentences.map {
                    SentenceDB.fromDomain(it)
                }.toTypedArray(),
                details.id
            )
    }

    fun toDomainSettings() = Settings(threshold.toFloat(), pause.toFloat())

    fun toDomainSentences(): Array<Sentence> {
        return sentenceDBS.map {
            it.toDomain()
        }.toTypedArray()
    }


}

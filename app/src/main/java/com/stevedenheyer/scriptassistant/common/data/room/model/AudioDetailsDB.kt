package com.stevedenheyer.scriptassistant.data

import androidx.room.*
import com.stevedenheyer.scriptassistant.common.data.room.model.SentenceDB
import com.stevedenheyer.scriptassistant.common.domain.model.audio.AudioDetails
import com.stevedenheyer.scriptassistant.common.domain.model.audio.StartingSettings
import com.stevedenheyer.scriptassistant.common.domain.model.audio.SentenceAudio
import kotlin.math.roundToInt

@Entity
data class AudioDetailsDB(
    var projectOwnerId: Long,
    var audioOwnerId: Long,
    var threshold: Int = 0,
    var pause: Int = 0,
    var offsetX: Float = 0f,
    var scaleX:Float = 1f,
    var sentenceDBS: Array<SentenceDB> = emptyArray(),
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
) {
    companion object {
        fun fromDomain(projectId: Long, details: AudioDetails) =
            AudioDetailsDB(
                projectId,
                details.audioOwnerId,
                details.startingSettings.thresholdSlider.roundToInt(),
                details.startingSettings.pauseSlider.roundToInt(),
                details.startingSettings.viewOffsetX,
                details.startingSettings.viewScaleX,
                details.sentences.map {
                    SentenceDB.fromDomain(it)
                }.toTypedArray(),
                details.id
            )
    }

    fun toDomainSettings() = StartingSettings(threshold.toFloat(), pause.toFloat(), offsetX, scaleX)

    fun toDomainSentences(): Array<SentenceAudio> {
        return sentenceDBS.map {
            it.toDomain()
        }.toTypedArray()
    }


}

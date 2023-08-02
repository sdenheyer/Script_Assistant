package com.stevedenheyer.scriptassistant.common.data.room.model

import android.util.Range
import com.stevedenheyer.scriptassistant.common.domain.model.audio.SentenceAudio

data class SentenceDB(
    val begin: Int,
    val end: Int,
    val isLocked: Boolean = false,
    val lineId: Long?,
    val take: Int?,
) {
    companion object {
        fun fromDomain(sentence: SentenceAudio) = SentenceDB(sentence.waveformRange.lower, sentence.waveformRange.upper, sentence.isLocked, sentence.scriptLineId, sentence.scriptTake)
    }

    fun toDomain() = SentenceAudio(Range(begin, end), isLocked, lineId, take)
}
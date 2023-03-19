package com.stevedenheyer.scriptassistant.common.data.room.model

import android.util.Range
import com.stevedenheyer.scriptassistant.audioeditor.domain.model.Sentence

data class SentenceDB(
    val begin: Int,
    val end: Int,
    val lineId: Long?,
    val take: Int?,
) {
    companion object {
        fun fromDomain(sentence: Sentence) = SentenceDB(sentence.range.lower, sentence.range.upper, sentence.lineId, sentence.take)
    }

    fun toDomain() = Sentence(Range(begin, end), lineId, take)
}
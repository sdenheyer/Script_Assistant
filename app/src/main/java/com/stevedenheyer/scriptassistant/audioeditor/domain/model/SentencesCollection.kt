package com.stevedenheyer.scriptassistant.audioeditor.domain.model

import com.stevedenheyer.scriptassistant.common.domain.model.audio.SentenceAudio

data class SentencesCollection(
    val id: Long,
    val data: Array<SentenceAudio>,
) {
    fun isEmpty(): Boolean = data.isEmpty()
}

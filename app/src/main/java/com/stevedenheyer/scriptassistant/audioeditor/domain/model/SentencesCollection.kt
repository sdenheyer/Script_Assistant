package com.stevedenheyer.scriptassistant.audioeditor.domain.model

data class SentencesCollection(
    val id: Long,
    val data: Array<Sentence>,
) {
    fun isEmpty(): Boolean = data.isEmpty()
}

package com.stevedenheyer.scriptassistant.audioeditor.domain.model

data class SentencesCollection(
    val id: Long,
    val data: List<Sentence>,
) {
    fun isEmpty(): Boolean = data.isEmpty()
}

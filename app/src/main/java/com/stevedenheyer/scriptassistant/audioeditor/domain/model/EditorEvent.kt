package com.stevedenheyer.scriptassistant.audioeditor.domain.model

sealed class EditorEvent {
    data class RequestSentenceUpdate(val completed: Boolean): EditorEvent()
    data class RequestRecyclerUpdate(val sentences: SentencesCollection): EditorEvent()
    data class RequestScriptUpdate(val completed: Boolean): EditorEvent()
}

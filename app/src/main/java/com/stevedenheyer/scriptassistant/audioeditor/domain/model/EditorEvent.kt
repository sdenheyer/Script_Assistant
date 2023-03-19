package com.stevedenheyer.scriptassistant.audioeditor.domain.model

sealed class EditorEvent {
    class RequestSentenceUpdate(val completed: Boolean): EditorEvent()
    class RequestScriptUpdate(val completed: Boolean): EditorEvent()
}

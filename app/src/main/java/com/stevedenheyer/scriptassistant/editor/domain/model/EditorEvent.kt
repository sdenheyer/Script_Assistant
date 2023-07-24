package com.stevedenheyer.scriptassistant.editor.domain.model

sealed class EditorEvent {

    sealed class Focus {
        data class WaveformFocus(val audioId: Long): Focus()
    }
    data class RequestSentenceUpdate(val completed: Boolean): EditorEvent()
    data class RequestRecyclerUpdate(val focus: Focus): EditorEvent()
    data class RequestScriptUpdate(val completed: Boolean): EditorEvent()

}



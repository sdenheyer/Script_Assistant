package com.stevedenheyer.scriptassistant.editor.domain.model

data class Waveform(
    val id: Long,
    val data: ByteArray,
    val size: Int?,
    val isLoading: Boolean
        )
    {

    fun isEmpty(): Boolean = data.isEmpty()

}
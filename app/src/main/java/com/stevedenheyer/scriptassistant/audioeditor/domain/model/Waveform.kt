package com.stevedenheyer.scriptassistant.audioeditor.domain.model

data class Waveform(
    val id: Long,
    val data: ByteArray,
    val isLoading: Boolean
        )
    {

    fun isEmpty(): Boolean = data.isEmpty()

}
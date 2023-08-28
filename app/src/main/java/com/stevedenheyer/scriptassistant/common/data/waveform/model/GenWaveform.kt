package com.stevedenheyer.scriptassistant.common.data.waveform.model

data class GenWaveform(
    val id: Long,
    val projectedSize: Int?,
    val data: ByteArray,
)

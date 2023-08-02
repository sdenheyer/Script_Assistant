package com.stevedenheyer.scriptassistant.common.domain.model.audio

import android.util.Range

data class SentenceAudio(
    val waveformRange: Range<Int>,
    val isLocked:Boolean = false,
    val scriptLineId: Long?,
    val scriptTake: Int?,
)

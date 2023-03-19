package com.stevedenheyer.scriptassistant.audioeditor.domain.model

import android.util.Range

data class Sentence(
    val range: Range<Int>,
    val lineId: Long?,
    val take: Int?,
)

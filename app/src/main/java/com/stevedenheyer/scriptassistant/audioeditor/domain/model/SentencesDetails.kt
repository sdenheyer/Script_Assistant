package com.stevedenheyer.scriptassistant.audioeditor.domain.model

import com.stevedenheyer.scriptassistant.common.domain.model.audio.Settings

data class SentencesDetails(
    val id: Long,
    val sentences: Array<Sentence>,
    val settings: Settings,
)

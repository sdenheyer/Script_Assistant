package com.stevedenheyer.scriptassistant.editor.domain.model

import com.stevedenheyer.scriptassistant.common.domain.model.audio.SentenceAudio
import com.stevedenheyer.scriptassistant.common.domain.model.audio.StartingSettings

data class SentencesDetails(
    val id: Long,
    val sentences: Array<SentenceAudio>,
    val startingSettings: StartingSettings,
)

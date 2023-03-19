package com.stevedenheyer.scriptassistant.common.domain.model.audio

import com.stevedenheyer.scriptassistant.audioeditor.domain.model.Sentence
import java.io.File

data class AudioDetails (
    val id: Long,
    val audioOwnerId: Long,  //TODO: Eliminate & re-factor
    val name: String,
    val audioFile: File,
    val settings: Settings,
    val sentences: Array<Sentence>,
        )
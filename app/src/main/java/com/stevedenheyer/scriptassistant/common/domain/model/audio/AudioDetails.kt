package com.stevedenheyer.scriptassistant.common.domain.model.audio

import java.io.File

data class AudioDetails (
    val id: Long,
    val audioOwnerId: Long,  //TODO: Eliminate & re-factor
    val name: String,
    val audioFile: File,
    val startingSettings: StartingSettings,
    val sentences: Array<SentenceAudio>,
        )
package com.stevedenheyer.scriptassistant.common.domain.model.project

data class Project(
    val id: Long,
    val name: String,
    val selectedAudioId: Long?
)
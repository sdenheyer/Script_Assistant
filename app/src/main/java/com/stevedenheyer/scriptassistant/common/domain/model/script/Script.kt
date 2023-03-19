package com.stevedenheyer.scriptassistant.common.domain.model.script

data class Script(
    val id: Long,
    val projectOwnerId: Long,    //TODO:  eliminate & re-factor
    val lines: List<Line>
)
package com.stevedenheyer.scriptassistant.common.domain.model.script

data class Line(
    val id: Long,
    val scriptOwnerId: Long,   //TODO:  Eliminate these & refactor
    val index: Int,
    val text:String
)

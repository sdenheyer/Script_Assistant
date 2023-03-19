package com.stevedenheyer.scriptassistant.common.domain.model.project

import com.stevedenheyer.scriptassistant.common.domain.model.script.Script

data class Project(
    val id: Long,
    val name: String,
) {

    override fun toString(): String {     //For Arrayadapter
        return name
    }

}

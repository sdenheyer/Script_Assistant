package com.stevedenheyer.scriptassistant.common.data.room.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.stevedenheyer.scriptassistant.common.domain.model.script.Script

@Entity
data class ScriptDB (@PrimaryKey(autoGenerate = true) var scriptId: Long = 0,
                     val projectOwnerId: Long,)

data class ScriptWithLines(
    @Embedded val script: ScriptDB,
    @Relation(
        parentColumn = "scriptId",
        entityColumn = "scriptOwnerId"
    )
    val lines: List<LineDB>
){

    companion object {
        fun fromScriptDomain(script: Script): ScriptDB {
            return ScriptDB(
                scriptId = script.id,
                projectOwnerId = script.projectOwnerId,
            )
        }
    }

    fun toScriptDomain(): Script {
        val domainLines = lines.map { line ->
            line.toDomain()
        }
        return Script(id = script.scriptId, projectOwnerId = script.projectOwnerId, domainLines)
    }

}
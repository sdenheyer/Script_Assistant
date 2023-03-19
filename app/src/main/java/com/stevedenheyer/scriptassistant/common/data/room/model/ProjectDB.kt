package com.stevedenheyer.scriptassistant.common.data.room.model

import androidx.room.*
import com.stevedenheyer.scriptassistant.common.domain.model.project.Project
import com.stevedenheyer.scriptassistant.common.domain.model.script.Line
import com.stevedenheyer.scriptassistant.common.domain.model.script.Script


@Entity
data class ProjectDB(
    val name: String,
    @PrimaryKey(autoGenerate = true) var projectId: Long = 0,
) {
       companion object {
        fun fromProjectDomain(project: Project): ProjectDB {
            return ProjectDB(
                projectId = project.id,
                name = project.name,
            )
        }
    }

    fun toProjectDomain(): Project {
        return Project(id = projectId, name = name)
    }

}

data class ProjectAndScript(
    @Embedded val project: ProjectDB,
    @Relation(
        parentColumn = "projectId",
        entityColumn = "projectOwnerId"
    )
    val script: ScriptDB
)
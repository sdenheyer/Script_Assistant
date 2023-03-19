package com.stevedenheyer.scriptassistant.common.domain.repositories

import com.stevedenheyer.scriptassistant.common.data.room.model.ProjectAndScript
import com.stevedenheyer.scriptassistant.common.domain.model.project.Project
import com.stevedenheyer.scriptassistant.common.domain.model.script.Line
import com.stevedenheyer.scriptassistant.common.domain.model.script.Script
import kotlinx.coroutines.flow.Flow

interface ProjectRepository {

    suspend fun insertProject(name: String) : Long

    suspend fun insertScriptLine(line: Line) : Long

    fun getAll(): Flow<List<Project>>

    fun getProjectFlow(id: Long?): Flow<Project>

    suspend fun projectAndScript(id: Long?): ProjectAndScript

    fun getScriptFlow(id: Long?) : Flow<Script>

    suspend fun deleteProject(project: Project)

    suspend fun deleteScriptLine(line: Line)

    suspend fun updateProject(project: Project)

    suspend fun updateScriptLine(line: Line): Int


}
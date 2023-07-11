package com.stevedenheyer.scriptassistant.common.data.room.repositories

import com.stevedenheyer.scriptassistant.common.data.room.daos.ProjectDao
import com.stevedenheyer.scriptassistant.common.data.room.model.*
import com.stevedenheyer.scriptassistant.common.domain.model.project.Project
import com.stevedenheyer.scriptassistant.common.domain.model.script.Line
import com.stevedenheyer.scriptassistant.common.domain.model.script.Script
import com.stevedenheyer.scriptassistant.common.domain.repositories.ProjectRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.transform
import javax.inject.Inject

class ProjectRepositoryImpl @Inject constructor(private val projectDao: ProjectDao) : ProjectRepository {

    override suspend fun insertProject(name: String) : Long {
        val projectId = projectDao.insertProject(ProjectDB(name = name, selectedAudioId = null))
        projectDao.insertScript(ScriptDB(projectOwnerId = projectId))
        return projectId
    }

    override suspend fun insertScriptLine(line: Line): Long {
        val lineId = projectDao.insertScriptLine(lineDB = LineDB.fromDomain(line))
        return lineId
    }

    override fun getAll() = projectDao.getAll().map { projectDB ->
        projectDB.map { it.toProjectDomain() }
    }

    override fun getProjectFlow(id: Long?) = projectDao.getProjectById(id).map { project ->
        project.toProjectDomain() }

    override suspend fun projectAndScript(id: Long?) = projectDao.getProjectAndScript(id)

    override fun getScriptFlow(id: Long?): Flow<Script> = projectDao.getScriptWithLines(id).map { script ->
        script.toScriptDomain()
    }

        override suspend fun deleteProject(project: Project) = projectDao.delete(ProjectDB.fromProjectDomain(project))

    override suspend fun deleteScriptLine(line: Line) {
        TODO("Not yet implemented")
    }

    override suspend fun updateProject(project: Project) = projectDao.updateProject(ProjectDB.fromProjectDomain(project))

    override suspend fun updateScriptLine(line: Line): Int = projectDao.updateScriptLine(LineDB.fromDomain(line))

   // override suspend fun updateScript(script: Script) = projectDao.updateScript(ScriptWithLines.fromScriptDomain(script))
    }
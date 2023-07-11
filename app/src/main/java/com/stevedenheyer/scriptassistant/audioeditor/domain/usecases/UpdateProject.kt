package com.stevedenheyer.scriptassistant.audioeditor.domain.usecases

import com.stevedenheyer.scriptassistant.common.domain.model.project.Project
import com.stevedenheyer.scriptassistant.common.domain.repositories.ProjectRepository
import javax.inject.Inject

class UpdateProject @Inject constructor(private val projectRepository: ProjectRepository) {
    suspend operator fun invoke(project: Project) = projectRepository.updateProject(project)
}
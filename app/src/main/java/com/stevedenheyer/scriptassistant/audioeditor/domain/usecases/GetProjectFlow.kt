package com.stevedenheyer.scriptassistant.audioeditor.domain.usecases

import com.stevedenheyer.scriptassistant.common.domain.repositories.ProjectRepository
import javax.inject.Inject

class GetProjectFlow @Inject constructor(private val projectRepository: ProjectRepository) {
    operator fun invoke(id: Long) = projectRepository.getProjectFlow(id)
}
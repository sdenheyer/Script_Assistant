package com.stevedenheyer.scriptassistant.editor.domain.usecases

import com.stevedenheyer.scriptassistant.common.domain.repositories.ProjectRepository
import javax.inject.Inject

class GetProjectFlow @Inject constructor(private val projectRepository: ProjectRepository) {
    operator fun invoke(id: Long) = projectRepository.getProjectFlow(id)
}
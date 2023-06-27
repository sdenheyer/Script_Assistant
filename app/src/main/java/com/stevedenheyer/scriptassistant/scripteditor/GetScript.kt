package com.stevedenheyer.scriptassistant.scripteditor

import com.stevedenheyer.scriptassistant.common.domain.repositories.ProjectRepository
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetScript @Inject constructor(private val projectRepository: ProjectRepository) {
    operator fun invoke(scriptId: Long) = projectRepository.getScriptFlow(scriptId).map { it.lines.sortedBy { it.index } }
}
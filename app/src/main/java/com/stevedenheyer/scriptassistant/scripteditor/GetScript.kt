package com.stevedenheyer.scriptassistant.scripteditor

import android.util.Log
import com.stevedenheyer.scriptassistant.common.domain.repositories.ProjectRepository
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetScript @Inject constructor(private val projectRepository: ProjectRepository) {
    operator fun invoke(projectId: Long) = projectRepository.getScriptFlow(projectId)
}
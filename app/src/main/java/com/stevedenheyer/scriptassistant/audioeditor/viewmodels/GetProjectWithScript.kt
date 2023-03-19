package com.stevedenheyer.scriptassistant.audioeditor.viewmodels

import com.stevedenheyer.scriptassistant.common.data.room.model.ProjectAndScript
import com.stevedenheyer.scriptassistant.common.domain.repositories.ProjectRepository
import com.stevedenheyer.scriptassistant.di.IoDispatcher
import kotlinx.coroutines.*
import javax.inject.Inject

class GetProjectWithScript @Inject constructor(private val projectRepository: ProjectRepository,
                                        @IoDispatcher private val ioDispatcher: CoroutineDispatcher) {
    operator suspend fun invoke(projectId: Long):ProjectAndScript {
        return coroutineScope { withContext(ioDispatcher) { projectRepository.projectAndScript(projectId) }}
    }
}
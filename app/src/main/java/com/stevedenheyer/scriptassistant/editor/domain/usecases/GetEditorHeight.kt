package com.stevedenheyer.scriptassistant.editor.domain.usecases

import android.util.Log
import com.stevedenheyer.scriptassistant.common.domain.repositories.AudioRepository
import com.stevedenheyer.scriptassistant.common.domain.repositories.ProjectRepository
import kotlinx.coroutines.flow.map
import java.io.File
import javax.inject.Inject

class GetEditorHeight @Inject constructor(private val projectRepository: ProjectRepository) {
    operator fun invoke(projectId: Long) = projectRepository.getProjectFlow(projectId).map {
        it.editorHeight
    }
}
package com.stevedenheyer.scriptassistant.scripteditor

import com.stevedenheyer.scriptassistant.common.data.room.model.LineDB
import com.stevedenheyer.scriptassistant.common.domain.model.script.Line
import com.stevedenheyer.scriptassistant.common.domain.repositories.ProjectRepository
import com.stevedenheyer.scriptassistant.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import javax.inject.Inject

class InsertLine @Inject constructor(private val projectRepository: ProjectRepository,
                                    @IoDispatcher private val ioDispatcher: CoroutineDispatcher) {
    operator suspend fun invoke(line: Line): Long {
        return coroutineScope { withContext(ioDispatcher) {projectRepository.insertScriptLine(line) }}
    }
}
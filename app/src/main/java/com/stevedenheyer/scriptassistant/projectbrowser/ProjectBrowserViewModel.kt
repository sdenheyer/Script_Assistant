package com.stevedenheyer.scriptassistant.projectbrowser

import android.util.Log
import androidx.lifecycle.*
import com.stevedenheyer.scriptassistant.common.domain.model.project.Project
import com.stevedenheyer.scriptassistant.common.domain.repositories.ProjectRepository
import com.stevedenheyer.scriptassistant.utils.EventHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ProjectBrowserViewModel @Inject internal constructor(private val projectRepository: ProjectRepository,
                                                            private val eventHandler: EventHandler<ProjectBrowserEvent>
) : ViewModel() {

    private val _selectedProject = MutableLiveData<Long>(-1)

    val selectedProject = _selectedProject

    val requestOpen = eventHandler.getEventFlow().asStateFlow()

    val projectList = projectRepository.getAll().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun setSelectedProject(projectId: Long) { _selectedProject.value = projectId }

    fun createNewProject(name: String) = viewModelScope.launch {
        val id = async { projectRepository.insertProject(name) }
        eventHandler.onEvent(ProjectBrowserEvent.requestOpenProject(id.await()))
    }

    fun openProject() {
        if (selectedProject.value != null && selectedProject.value!! > -1) {
                eventHandler.onEvent(ProjectBrowserEvent.requestOpenProject(selectedProject.value!!))
        }
    }

    fun deleteProject() = viewModelScope.launch {
            Log.d("PBVM", "Deleting...")
            var project:Project? = null
            projectList.value?.forEach {
                if (it.id == selectedProject.value) {
                    Log.d("PBVM", "Found it!")
                    project = it
                }
            }
            if (project != null) {
                projectRepository.deleteProject(project!!)
            }
        }
}
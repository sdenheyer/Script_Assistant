package com.stevedenheyer.scriptassistant.projectbrowser

import android.util.Log
import androidx.lifecycle.*
import com.stevedenheyer.scriptassistant.common.domain.model.project.Project
import com.stevedenheyer.scriptassistant.common.domain.repositories.ProjectRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ProjectBrowserViewModel @Inject internal constructor(private val projectRepository: ProjectRepository,
) : ViewModel() {

    //TODO:  Usecases?

    private val _selectedProject = MutableLiveData<Long>(-1)

    val selectedProject = _selectedProject

    val projectList = projectRepository.getAll().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun setSelectedProject(projectId: Long) { _selectedProject.value = projectId }

    suspend fun createNewProject(name: String):Long {
        val id = projectRepository.insertProject(name)
        return id
    }

    fun deleteProject() = viewModelScope.launch {
            Log.d("PBVM", "Deleting...")
            val project = projectList.value.find { it.id == selectedProject.value }

            if (project != null) {
                projectRepository.deleteProject(project)
            }
        }
}
package com.stevedenheyer.scriptassistant.editor.viewmodels

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stevedenheyer.scriptassistant.common.domain.model.project.Project
import com.stevedenheyer.scriptassistant.editor.domain.usecases.GenerateWaveform
import com.stevedenheyer.scriptassistant.editor.domain.usecases.GetEditorHeight
import com.stevedenheyer.scriptassistant.editor.domain.usecases.GetFileNames
import com.stevedenheyer.scriptassistant.editor.domain.usecases.GetProjectFlow
import com.stevedenheyer.scriptassistant.editor.domain.usecases.UpdateProject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProjectViewModel @Inject constructor(
    state: SavedStateHandle,
    private val getFileNames: GetFileNames,
    private val generateWaveform: GenerateWaveform,
    private val getProjectFlow: GetProjectFlow,
    private val updateProject: UpdateProject,
    private val getEditorHeight: GetEditorHeight,

    ):ViewModel() {

    private val projectId = state.get<Long>("projectId")!!

    private val projectFlow = getProjectFlow(projectId).stateIn(viewModelScope, SharingStarted.Eagerly, Project(-1, "", null, null))

    private val _editorHeight: MutableStateFlow<Float?> = MutableStateFlow(null)
    val editorHeight = _editorHeight.filter { it != null } as Flow<Float>
    init {
        viewModelScope.launch {
            getFileNames(projectId).collect {
                generateWaveform(it)
            }
        }

        viewModelScope.launch {
            _editorHeight.update {
                getEditorHeight(projectId).first()
            }
        }

    }

    fun setEditorHeight(height: Float) {
        _editorHeight.update {
            height
        }
    }

    fun updateEditorHeight() {
        viewModelScope.launch {
            val project = projectFlow.value.copy(editorHeight = _editorHeight.value)
            updateProject(project)
        }
    }
}
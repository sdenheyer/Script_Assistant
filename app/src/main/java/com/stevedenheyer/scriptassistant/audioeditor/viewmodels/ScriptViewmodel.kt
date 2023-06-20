package com.stevedenheyer.scriptassistant.audioeditor.viewmodels

import android.util.Log
import androidx.lifecycle.*
import com.stevedenheyer.scriptassistant.di.IoDispatcher
import com.stevedenheyer.scriptassistant.scripteditor.GetScript
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScriptViewmodel @Inject constructor(
    private val state: SavedStateHandle,
    private val getProjectWithScript: GetProjectWithScript,
    private val getScript: GetScript) : ViewModel() {

    private val projectId = state.get<Long>("projectId")!!

    private val _scriptId = MutableStateFlow<Long?>(null)
    val scriptId = _scriptId.asStateFlow()

    init {
        viewModelScope.launch {
            _scriptId.value = getProjectWithScript(projectId).script.scriptId
        }
    }
    }
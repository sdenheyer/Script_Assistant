package com.stevedenheyer.scriptassistant.audioeditor.viewmodels

import androidx.lifecycle.*
import com.stevedenheyer.scriptassistant.di.IoDispatcher
import com.stevedenheyer.scriptassistant.scripteditor.GetScript
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScriptViewmodel @Inject constructor(
    private val state: SavedStateHandle,
    private val getProjectWithScript: GetProjectWithScript,
    private val getScript: GetScript) : ViewModel() {

    private val projectId = state.get<Long>("projectId")!!

    val scriptId = MutableLiveData<Long?>(null)

    init {
        viewModelScope.launch {
            scriptId.value = getProjectWithScript(projectId).script.scriptId
        }
    }
    }
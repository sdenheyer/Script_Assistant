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
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScriptViewmodel @Inject constructor(
    private val state: SavedStateHandle,
   // private val getProjectWithScript: GetProjectWithScript,
    private val getScript: GetScript) : ViewModel() {

    private val scriptId = state.get<Long>("scriptId")!!

    val _script = MutableStateFlow<List<ScriptLineItemView>>(emptyList())
    val script = _script.asStateFlow()

    init {
        viewModelScope.launch {
            getScript(scriptId).collect { scriptLines ->
                val linesView = scriptLines.map { scriptLine ->
                    ScriptLineItemView(id = scriptLine.id, text = scriptLine.text)
                }
                _script.value = linesView
            }
        }
    }
}

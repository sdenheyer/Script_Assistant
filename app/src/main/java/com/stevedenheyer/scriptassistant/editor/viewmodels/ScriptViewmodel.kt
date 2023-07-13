package com.stevedenheyer.scriptassistant.editor.viewmodels

import androidx.lifecycle.*
import com.stevedenheyer.scriptassistant.scripteditor.GetScript
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
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

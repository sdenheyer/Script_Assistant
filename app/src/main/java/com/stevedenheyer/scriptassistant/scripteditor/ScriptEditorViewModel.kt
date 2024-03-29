package com.stevedenheyer.scriptassistant.scripteditor

import android.util.Log
import androidx.lifecycle.*
import com.stevedenheyer.scriptassistant.editor.domain.model.EditorEvent
import com.stevedenheyer.scriptassistant.common.domain.model.script.Line
import com.stevedenheyer.scriptassistant.utils.EventHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScriptEditorViewModel @Inject constructor(
    private val state: SavedStateHandle,
    private val getScript: GetScript,
    private val insertLine: InsertLine,
    private val eventHandler: EventHandler<EditorEvent>,
) : ViewModel() {

    private val scriptId = state.get<Long>("scriptId")!!

    private val scriptFlow = MutableStateFlow<List<ScriptLineRecyclerItemView>>(emptyList())

    val scriptLines = scriptFlow.asStateFlow()

    val lineEditor = MutableStateFlow("")

    val currentLineIndex = MutableStateFlow(0)

    init {
        viewModelScope.launch {
            getScript(scriptId).collect { script ->
                val lines = ArrayList<ScriptLineRecyclerItemView>()
                script.forEachIndexed { index, line ->
                    lines.add(ScriptLineRecyclerItemView(line.id, index, line.text))
                    if (line.text.isEmpty()) {
                        currentLineIndex.value = line.index
                    }
                }

                if (lines.isEmpty() || lines.last().text.isNotBlank()) {

                    lines.add(ScriptLineRecyclerItemView(0, lines.lastIndex + 1, ""))
                    currentLineIndex.value = lines.size - 1
                }

                Log.d("LD", "script updating...")
                scriptFlow.value = lines

            }
        }

        viewModelScope.launch {
            combine(eventHandler.getEventFlow(), scriptFlow) { event, script ->
                Log.d("SVM", "Event: ${event.toString()}")
                if (event is EditorEvent.RequestScriptUpdate && !event.completed) {
                    val index = currentLineIndex.value!!
                    val lineToUpdate = script.get(index)
                    val newLine = Line(lineToUpdate.id, scriptId, lineToUpdate.index, lineToUpdate.text)

                    Log.d("LD", "Script DB update: ${lineToUpdate} size: ${script.size} index: $index")

                    insertLine(newLine)

                    eventHandler.onEvent(EditorEvent.RequestScriptUpdate(true))

                }
            }.collect()
        }
    }

    fun editTextWatcher(string: String) {
        Log.d("SVM", "text: $string")
        if (string != lineEditor.value) {
            lineEditor.value = string
            val lines = scriptLines.value!!.toMutableList()
            val line = lines!!.get(currentLineIndex.value ?: 0)
            lines.remove(line)
            lines.add(currentLineIndex.value ?: 0, line.copy(text = string.toString()))
            scriptFlow.value = lines
        }
    }

    fun onEditorAction() {
       // if (actionId == EditorInfo.IME_ACTION_DONE) {
            Log.d("SVM", "Done pressed")
            eventHandler.onEvent(EditorEvent.RequestScriptUpdate(false))
         //   return true
       // return false
    }

    fun onItemSelected(key: Long, selected: Boolean) {
        Log.d("SVM", "Item selected: $key, $selected")
        if (selected) {
            scriptLines.value.forEach { line ->
                if (line.id == key) {
                    currentLineIndex.value = line.index
                    lineEditor.value = line.text
                }
            }
        }
    }
}
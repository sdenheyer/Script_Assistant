package com.stevedenheyer.scriptassistant.scripteditor

import android.util.Log
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.lifecycle.*
import com.stevedenheyer.scriptassistant.audioeditor.domain.model.EditorEvent
import com.stevedenheyer.scriptassistant.common.domain.model.script.Line
import com.stevedenheyer.scriptassistant.common.domain.model.script.Script
import com.stevedenheyer.scriptassistant.utils.EventHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
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

    val scriptLines = scriptFlow.asLiveData()

    val lineEditor = MutableLiveData<String>()

    val currentLineIndex = MutableLiveData<Int>()

    init {
        viewModelScope.launch {
            getScript(scriptId).collect { script ->
                val lines = ArrayList<ScriptLineRecyclerItemView>()
                script.lines.forEachIndexed { index, line ->
                    lines.add(ScriptLineRecyclerItemView(line.id, index, line.text))
                    if (line.text.isEmpty()) {
                        currentLineIndex.value = line.index
                    }
                }

                if (lines.isEmpty() || lines.last().text.isNotBlank()) {

                    lines.add(ScriptLineRecyclerItemView(0, lines.lastIndex + 1, ""))
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

    fun editTextWatcher(string: CharSequence, start: Int, before: Int, count: Int) {
        Log.d("SVM", "text: $string")
        val lines = scriptLines.value!!.toMutableList()
        val line = lines!!.get(currentLineIndex.value ?: 0)
        lines.remove(line)
        lines.add(currentLineIndex.value ?: 0, line.copy(text = string.toString()))
        scriptFlow.value = lines
    }

    fun onEditorAction(view: TextView, actionId: Int, event: KeyEvent?): Boolean {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            Log.d("SVM", "Done pressed")
            eventHandler.onEvent(EditorEvent.RequestScriptUpdate(false))
            return true
        }
        return false
    }

    fun onItemSelected(key: Long, selected: Boolean) {
        Log.d("SVM", "Item selected: $key, $selected")
        if (selected) {
            scriptLines.value!!.forEach { line ->
                if (line.id == key) {
                    currentLineIndex.value = line.index
                    lineEditor.value = line.text
                }
            }
        }
    }
}
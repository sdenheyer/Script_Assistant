package com.stevedenheyer.scriptassistant.editor.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.stevedenheyer.scriptassistant.editor.components.WaveformCanvas
import com.stevedenheyer.scriptassistant.editor.viewmodels.ScriptLineItemView
import com.stevedenheyer.scriptassistant.editor.viewmodels.ScriptViewmodel
import com.stevedenheyer.scriptassistant.editor.viewmodels.WaveformRecyclerItemView
import com.stevedenheyer.scriptassistant.editor.viewmodels.WaveformRecyclerViewModel
import com.stevedenheyer.scriptassistant.editor.viewmodels.WaveformEditorViewModel
import com.stevedenheyer.scriptassistant.common.components.Draggable
import com.stevedenheyer.scriptassistant.common.components.DropTarget
import com.stevedenheyer.scriptassistant.editor.viewmodels.WaveformGeneratorViewModel

@Composable
fun AudioEditorScreen(
    waveformGeneratorVM: WaveformGeneratorViewModel,
    waveformRecyclerVM: WaveformRecyclerViewModel,
    waveformUniverseVM: WaveformEditorViewModel,
    scriptVM: ScriptViewmodel,
    onNavigateToImport: () -> Unit,
    onNavigateToScriptEditor: () -> Unit
) {
    Column{
        Draggable(modifier = Modifier.weight(1f)) {
            Row(modifier = Modifier) {
                WaveformRecycler(
                    modifier = Modifier.weight(3f),
                    waveformVM = waveformRecyclerVM)

                Script(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    scriptVM = scriptVM,
                    onNavigateToScriptEditor = onNavigateToScriptEditor
                )
            }
        }
        WaveformEditor(modifier = Modifier.weight(1f), waveformVM = waveformUniverseVM, onNavigateToImport = onNavigateToImport )
    }
}

@Composable
fun WaveformRecycler(modifier: Modifier, waveformVM: WaveformRecyclerViewModel) {
    val waveformItems:Array<WaveformRecyclerItemView> by waveformVM.recyclerItems.collectAsStateWithLifecycle(initialValue = emptyArray())
    LazyColumn(modifier) {
        items(waveformItems) { item ->
            DropTarget<ScriptLineItemView>(modifier = Modifier) {
                isInBound, lineItem ->
                val bgColor = if (isInBound) Color.Blue else Color.White

                lineItem?.let {line ->
                    waveformVM.onScriptDropped(item, line)
                }

                Box(modifier = Modifier
                    .horizontalScroll(rememberScrollState())
                    .background(bgColor)) {
                    Text(text = item.text)
                    WaveformCanvas(
                        modifier = Modifier.height(90.dp),
                        waveform = item.waveform,
                        color = Color.Gray
                    )
                }

            }
        }

    }
}


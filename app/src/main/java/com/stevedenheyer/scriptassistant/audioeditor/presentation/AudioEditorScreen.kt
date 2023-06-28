package com.stevedenheyer.scriptassistant.audioeditor.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.stevedenheyer.scriptassistant.audioeditor.components.WaveformCanvas
import com.stevedenheyer.scriptassistant.audioeditor.viewmodels.ScriptLineItemView
import com.stevedenheyer.scriptassistant.audioeditor.viewmodels.ScriptViewmodel
import com.stevedenheyer.scriptassistant.audioeditor.viewmodels.WaveformRecyclerItemView
import com.stevedenheyer.scriptassistant.audioeditor.viewmodels.WaveformRecyclerViewModel
import com.stevedenheyer.scriptassistant.audioeditor.viewmodels.WaveformUniverseViewModel
import com.stevedenheyer.scriptassistant.common.components.DragTarget
import com.stevedenheyer.scriptassistant.common.components.Draggable
import com.stevedenheyer.scriptassistant.common.components.DropTarget
import com.stevedenheyer.scriptassistant.common.domain.model.script.Line

@Composable
fun AudioEditorScreen(
    waveformRecyclerVM: WaveformRecyclerViewModel,
    waveformUniverseVM: WaveformUniverseViewModel,
    scriptVM: ScriptViewmodel,
    onNavigateToImport: () -> Unit,
    onNavigateToScriptEditor: (scriptId: Long) -> Unit
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
    val waveformItems:Array<WaveformRecyclerItemView> by waveformVM.getRecyclerItems().collectAsStateWithLifecycle(initialValue = emptyArray())
    LazyColumn(modifier) {
        items(waveformItems) { item ->
            DropTarget<ScriptLineItemView>(modifier = Modifier) {
                isInBound, lineItem ->
                val bgColor = if (isInBound) Color.Blue else Color.White

                lineItem?.let {line ->
                    //TODO:  Update line
                }

                Box(modifier = Modifier
                    .horizontalScroll(rememberScrollState())
                    .background(bgColor)) {
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


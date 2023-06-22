package com.stevedenheyer.scriptassistant.audioeditor.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.stevedenheyer.scriptassistant.audioeditor.components.WaveformCanvas
import com.stevedenheyer.scriptassistant.audioeditor.viewmodels.ScriptViewmodel
import com.stevedenheyer.scriptassistant.audioeditor.viewmodels.WaveformRecyclerItemView
import com.stevedenheyer.scriptassistant.audioeditor.viewmodels.WaveformRecyclerViewModel
import com.stevedenheyer.scriptassistant.audioeditor.viewmodels.WaveformUniverseViewModel

@Composable
fun AudioEditorScreen(
    waveformRecyclerVM: WaveformRecyclerViewModel,
    waveformUniverseVM: WaveformUniverseViewModel,
    scriptVM: ScriptViewmodel,
    onNavigateToImport: () -> Unit,
    onNavigateToScriptEditor: (scriptId: Long) -> Unit
) {
    Column{
        Row (modifier = Modifier.weight(1f)){
            WaveformRecycler(modifier = Modifier.weight(3f), waveformVM = waveformRecyclerVM)
            Script(scriptVM = scriptVM, modifier = Modifier.weight(1f).fillMaxHeight(), onNavigateToScriptEditor = onNavigateToScriptEditor )
        }
        WaveformEditor(modifier = Modifier.weight(1f), waveformVM = waveformUniverseVM, onNavigateToImport = onNavigateToImport )
    }
}

@Composable
fun WaveformRecycler(modifier: Modifier, waveformVM: WaveformRecyclerViewModel) {
    val waveformItems:Array<WaveformRecyclerItemView> by waveformVM.getRecyclerItems().collectAsStateWithLifecycle(initialValue = emptyArray())
    LazyColumn(modifier) {
        items(waveformItems) { item ->
            WaveformCanvas(modifier = Modifier.height(30.dp), waveform = item.waveform, color = Color.Gray)
        }

    }
}


package com.stevedenheyer.scriptassistant.editor.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.stevedenheyer.scriptassistant.editor.viewmodels.ScriptViewmodel
import com.stevedenheyer.scriptassistant.common.components.DragTarget

@Composable
fun Script(scriptVM: ScriptViewmodel, modifier: Modifier, onNavigateToScriptEditor: () -> Unit) {

    val script by scriptVM.script.collectAsStateWithLifecycle(initialValue = emptyList())

    Column(modifier = modifier, verticalArrangement = Arrangement.SpaceBetween) {
     //   Draggable(modifier = Modifier.fillMaxSize()) {
            LazyColumn {
                items(script) { line ->
                    DragTarget(modifier = Modifier, dataToDrop = line) {
                        Text(modifier = Modifier, text = line.text, fontSize = 34.sp)
                    }
                }
            }
      //  }
        Button(modifier = Modifier.align(Alignment.CenterHorizontally),
            onClick = { onNavigateToScriptEditor() },
            content = { Text("Editor") })
    }
}

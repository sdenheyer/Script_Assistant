package com.stevedenheyer.scriptassistant.audioeditor.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.stevedenheyer.scriptassistant.audioeditor.viewmodels.ScriptViewmodel

@Composable
fun Script(scriptVM: ScriptViewmodel, modifier: Modifier, onNavigateToScriptEditor: (scriptId: Long) -> Unit) {

    val scriptId by scriptVM.scriptId.collectAsStateWithLifecycle(initialValue = null)
    val script by scriptVM.script.collectAsStateWithLifecycle(initialValue = emptyList())

    Column(modifier = modifier, verticalArrangement = Arrangement.SpaceBetween) {
        LazyColumn {
            items(script) { line ->
                Text(modifier = Modifier, text = line.text)
            }
        }

        Button(modifier = Modifier.align(Alignment.CenterHorizontally),
            onClick = { if (scriptId != null) onNavigateToScriptEditor(scriptId!!) },
            content = { Text("Editor") })
    }
}

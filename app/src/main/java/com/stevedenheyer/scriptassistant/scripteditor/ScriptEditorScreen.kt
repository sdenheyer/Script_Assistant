package com.stevedenheyer.scriptassistant.scripteditor

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun ScriptEditorScreen(scriptEditorVM: ScriptEditorViewModel = hiltViewModel()) {
    val editLine by scriptEditorVM.lineEditor.collectAsStateWithLifecycle(initialValue = "")
    val scriptLines by scriptEditorVM.scriptLines.collectAsStateWithLifecycle()
    Column(verticalArrangement = Arrangement.SpaceBetween, modifier = Modifier) {
        LazyColumn {
            items(scriptLines) { line ->
                Row {
                    Text(text = line.index.toString(), fontSize = 34.sp, modifier = Modifier.size(60.dp))
                    Text(text = line.text, fontSize = 34.sp, modifier = Modifier.selectable(true, onClick = { scriptEditorVM.onItemSelected(line.id, true) }))
                }

            }

        }
        TextField(value = editLine, singleLine = true, keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = {scriptEditorVM.onEditorAction()}), onValueChange = { scriptEditorVM.editTextWatcher(it) })
    }
}

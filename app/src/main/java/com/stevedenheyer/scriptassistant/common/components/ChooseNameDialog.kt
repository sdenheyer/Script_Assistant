package com.stevedenheyer.scriptassistant.common.components

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChooseNameDialog(modifier: Modifier, createDialogOpen: MutableState<Boolean>, createProject: (String) -> Unit) {
    val scope = rememberCoroutineScope()
    val projectName = remember { mutableStateOf("") }
    if (createDialogOpen.value) {
        AlertDialog(
            modifier = modifier,
            onDismissRequest = { createDialogOpen.value = false },
            title = { Text ("Enter a project name:") },
            text = { OutlinedTextField(value = projectName.value, onValueChange = { projectName.value = it }) },
            confirmButton = { Button(onClick = {
                scope.launch {
                   createProject(projectName.value)
                }
            } ) {
                Text(text = "Done")
            }
            }
        )
    }
}

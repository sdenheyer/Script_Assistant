package com.stevedenheyer.scriptassistant.filebrowser.presentation

import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun FileBrowserScreen(startingDir: File, onFileSelected: (File) -> Unit) {

    val _fileListFlow = MutableStateFlow<List<File>>(emptyList())
    val fileList by _fileListFlow.collectAsStateWithLifecycle(initialValue = emptyList())

    fun open(file: File) {
        Log.d("TEMP", "File: ${file.absolutePath}")
        if (file.isFile) {  //TODO:  Globalscope
            onFileSelected(file)
        }

        if (file.isDirectory) {
            val fileList = file.listFiles()?.filter { !it.isHidden }?.toList() ?: emptyList()

            _fileListFlow.value = fileList
        }
    }

    open(startingDir)

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        itemsIndexed(fileList) { index, file ->
            Text(text = file.name, fontSize = 20.sp, modifier = Modifier
                .padding(vertical = 8.dp)
                .selectable(true, onClick = { open(file) }))
        }
    }
}




package com.stevedenheyer.scriptassistant.filebrowser.presentation

import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.stevedenheyer.scriptassistant.R
import com.stevedenheyer.scriptassistant.filebrowser.domain.usecases.CreateNewAudioData
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@AndroidEntryPoint
class FileBrowserFragment : Fragment() {

    companion object {
        fun newInstance() = FileBrowserFragment()
    }

    val args: FileBrowserFragmentArgs by navArgs()

    @Inject
    lateinit var createNewAudioData: CreateNewAudioData

    //private lateinit var currentDir: File
  //  private var fileList = emptyList<File>()
  //  private lateinit var fileListAdapter: ArrayAdapter<String>
    private val fileListFlow = MutableStateFlow<List<File>>(emptyList())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
      /*  val binding = FileBrowserFragmentBinding.inflate(inflater, container, false)

        val fileListView = binding.root.findViewById<ListView>(R.id.file_list)
        fileListAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_expandable_list_item_1,
            mutableListOf<String>()
        )

        fileListView.adapter = fileListAdapter
        fileListView.setOnItemClickListener { _, _, position, _ ->
            open(fileList.get(position))
        }
*/
        open(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS))  //Deprecated - don't care cause android doesn't offer a viable alt

        Log.d("TEMP", "Browser: ${args.projectId}")

        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent{
                FileList(fileListFlow = fileListFlow)
            }
        }
        //binding.root
    }

    private fun open(file: File) {
        Log.d("TEMP", "File: ${file.absolutePath}")
        if (file.isFile) {  //TODO:  Globalscope
            GlobalScope.launch {
                createNewAudioData(args.projectId, file.absolutePath)
            }

            //setFragmentResult("selected_file", bundleOf("file_path" to file))
            findNavController().navigateUp()
            return
        }

        if (file.isDirectory) {
            //currentDir = file
            val fileList = file.listFiles()?.filter { !it.isHidden }?.toList() ?: emptyList()

            fileListFlow.value = fileList
/*
            fileListAdapter.clear()
            fileListAdapter.addAll(fileList.map {
                it.name
            })
*/
        }
    }

    @Composable
    fun FileList(fileListFlow: Flow<List<File>>) {
        val fileList by fileListFlow.collectAsStateWithLifecycle(initialValue = emptyList())
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            itemsIndexed(fileList) { index, file ->
                Text(text = file.name, fontSize = 20.sp, modifier = Modifier
                    .padding(vertical = 8.dp)
                    .selectable(true, onClick = { open(file) }))
            }
        }
    }
}
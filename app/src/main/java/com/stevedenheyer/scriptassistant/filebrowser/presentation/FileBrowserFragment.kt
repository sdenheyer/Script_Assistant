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
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.stevedenheyer.scriptassistant.R
import com.stevedenheyer.scriptassistant.databinding.FileBrowserFragmentBinding
import com.stevedenheyer.scriptassistant.filebrowser.domain.usecases.CreateNewAudioData
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.GlobalScope
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
    private var fileList = emptyList<File>()
    private lateinit var fileListAdapter: ArrayAdapter<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FileBrowserFragmentBinding.inflate(inflater, container, false)

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

        open(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS))  //Deprecated - don't care cause android doesn't offer a viable alt

        Log.d("TEMP", "Browser: ${args.projectId}")

        return binding.root
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
            fileList = file.listFiles()?.filter { !it.isHidden }?.toList() ?: emptyList()

            fileListAdapter.clear()
            fileListAdapter.addAll(fileList.map {
                it.name
            })
        }
    }
}
package com.stevedenheyer.scriptassistant.audioeditor.presentation

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import com.stevedenheyer.scriptassistant.R
import com.stevedenheyer.scriptassistant.audioeditor.components.WaveformCanvas
import com.stevedenheyer.scriptassistant.audioeditor.viewmodels.WaveformRecyclerItemView
import com.stevedenheyer.scriptassistant.audioeditor.viewmodels.WaveformRecyclerViewModel
import com.stevedenheyer.scriptassistant.audioeditor.viewmodels.WaveformUniverseViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AudioEditorFragment : Fragment() {

    companion object {
        fun newInstance() = AudioEditorFragment()
    }

    lateinit var navController: NavController

    private val args:AudioEditorFragmentArgs by navArgs()

    private val waveformRecyclerVM: WaveformRecyclerViewModel by navGraphViewModels(R.id.audioEditorFragment) {defaultViewModelProviderFactory}
    private val waveformUniverseVM: WaveformUniverseViewModel by navGraphViewModels(R.id.audioEditorFragment) {defaultViewModelProviderFactory}

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

     /*   val binding = AudioEditorFragmentBinding.inflate(inflater, container, false)

       // binding.recyclerModel = waveformRecyclerVM

       // binding.lifecycleOwner = viewLifecycleOwner

        val waveformNavHostFragment = childFragmentManager.findFragmentById(R.id.waveform_editor) as NavHostFragment

        waveformNavHostFragment.navController.navigate(R.id.waveformEditorFragment, args.toBundle())

        val scriptNavHostFragment = childFragmentManager.findFragmentById(R.id.script) as NavHostFragment

        scriptNavHostFragment.navController.navigate(R.id.scriptFragment, args.toBundle())

        binding.composeView

        return view*/
        navController = requireParentFragment().findNavController()

        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent{
                AudioEditor()
            }
        }
    }

    @Composable
    fun AudioEditor(
        //navController: NavHostController = rememberNavController()
    ) {
        Column{
            WaveformRecycler(modifier = Modifier.weight(1f), waveformVM = waveformRecyclerVM)
            WaveformEditor(modifier = Modifier.weight(1f), waveformVM = waveformUniverseVM, onNavigateToImport = { navController.navigate(R.id.fileBrowserFragment, bundleOf("projectId" to args.projectId))})
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
}




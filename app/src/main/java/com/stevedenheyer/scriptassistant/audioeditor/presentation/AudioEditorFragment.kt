package com.stevedenheyer.scriptassistant.audioeditor.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import com.stevedenheyer.scriptassistant.R
import com.stevedenheyer.scriptassistant.audioeditor.components.WaveformCanvas
import com.stevedenheyer.scriptassistant.audioeditor.viewmodels.WaveformRecyclerViewModel
import com.stevedenheyer.scriptassistant.audioeditor.viewmodels.WaveformUniverseViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AudioEditorFragment : Fragment() {

    companion object {
        fun newInstance() = AudioEditorFragment()
    }

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

        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent{
                AudioEditor()
            }
        }
    }

    @Composable
    fun AudioEditor(
        navController: NavHostController = rememberNavController()
    ) {
        Column{
            WaveformRecycler(waveformVM = waveformRecyclerVM)
            WaveformEditor(waveformVM = waveformUniverseVM, onNavigateToImport = { navController.navigate(R.id.fileBrowserFragment, bundleOf("projectId" to args.projectId))})
        }
    }

    @Composable
    fun WaveformRecycler(waveformVM: WaveformRecyclerViewModel) {
        val waveformItems by waveformVM.getRecyclerItems().collectAsStateWithLifecycle(initialValue = emptyList())
        LazyColumn {
            items(waveformItems) { item ->
                WaveformCanvas(modifier = Modifier, waveform = item.waveform, color = Color.Gray)
            }

        }
    }
}
package com.stevedenheyer.scriptassistant.audioeditor.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.*
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import com.stevedenheyer.scriptassistant.R
import com.stevedenheyer.scriptassistant.audioeditor.viewmodels.WaveformRecyclerViewModel
import com.stevedenheyer.scriptassistant.databinding.AudioEditorFragmentBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AudioEditorFragment : Fragment() {

    companion object {
        fun newInstance() = AudioEditorFragment()
    }

    private val args:AudioEditorFragmentArgs by navArgs()

    private val waveformRecyclerVM: WaveformRecyclerViewModel by navGraphViewModels(R.id.audioEditorFragment) {defaultViewModelProviderFactory}

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val binding: AudioEditorFragmentBinding = DataBindingUtil.inflate(inflater, R.layout.audio_editor_fragment, container, false)

        binding.recyclerModel = waveformRecyclerVM

        binding.lifecycleOwner = viewLifecycleOwner

        val waveformNavHostFragment = childFragmentManager.findFragmentById(R.id.waveform_editor) as NavHostFragment

        waveformNavHostFragment.navController.navigate(R.id.waveformEditorFragment, args.toBundle())

        val scriptNavHostFragment = childFragmentManager.findFragmentById(R.id.script) as NavHostFragment

        scriptNavHostFragment.navController.navigate(R.id.scriptFragment, args.toBundle())

        return binding.root
    }
}
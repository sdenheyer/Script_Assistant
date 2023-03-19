package com.stevedenheyer.scriptassistant.scripteditor

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import com.stevedenheyer.scriptassistant.R
import com.stevedenheyer.scriptassistant.databinding.ScriptEditorFragmentBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ScriptEditorFragment : Fragment() {

    companion object {
        fun newInstance() = ScriptEditorFragment()
    }

    private val scriptEditorVM: ScriptEditorViewModel by navGraphViewModels(R.id.scriptEditorFragment) {defaultViewModelProviderFactory}

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: ScriptEditorFragmentBinding = DataBindingUtil.inflate(inflater, R.layout.script_editor_fragment, container, false)

        binding.scriptModel = scriptEditorVM

        binding.lifecycleOwner = viewLifecycleOwner

        return binding.root

    }


}
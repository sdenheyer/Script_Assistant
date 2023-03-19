package com.stevedenheyer.scriptassistant.audioeditor.presentation

import android.content.ClipData
import android.content.ClipDescription
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.DragEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import com.stevedenheyer.scriptassistant.R
import com.stevedenheyer.scriptassistant.audioeditor.viewmodels.ScriptViewmodel
import com.stevedenheyer.scriptassistant.databinding.ScriptFragmentBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ScriptFragment : Fragment() {

    private val args: ScriptFragmentArgs by navArgs()

    private val scriptVM: ScriptViewmodel by navGraphViewModels(R.id.scriptFragment) {defaultViewModelProviderFactory}

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding:ScriptFragmentBinding = DataBindingUtil.inflate(inflater, R.layout.script_fragment, container, false)

        val text = binding.root.findViewById<TextView>(R.id.script_text)

        scriptVM.scriptId.observe(viewLifecycleOwner) { scriptId ->
            binding.root.findViewById<Button>(R.id.open_script_editor).setOnClickListener {
                val hostNavController = requireActivity().supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
                hostNavController.navController.navigate(R.id.scriptEditorFragment, bundleOf(Pair("scriptId", scriptId)))
            }
        }



        text.setOnLongClickListener { view ->
            val v = view as TextView
            val item = ClipData.Item(view.text)
            val mimeTypes = arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN)
            val dragData = ClipData(view.text, mimeTypes, item)
            val shadow = View.DragShadowBuilder(text)

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N)
            {
                @Suppress("DEPRECATION")
                view.startDrag(dragData, shadow, view, 0)
            } else {
                view.startDragAndDrop(dragData,shadow,view,0)
            }
            true
        }

        text.setOnDragListener { view, event ->
            when (event.action) {
                DragEvent.ACTION_DRAG_STARTED ->
                {
                    Log.d("TAG", "Dragging...")
                true
                    }
                else ->
                {
                    true
                }
            }
        }

        return binding.root
    }
}
package com.stevedenheyer.scriptassistant.scripteditor

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import com.stevedenheyer.scriptassistant.R
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
        /*  val binding: ScriptEditorFragmentBinding = DataBindingUtil.inflate(inflater, R.layout.script_editor_fragment, container, false)

        binding.scriptModel = scriptEditorVM

        binding.lifecycleOwner = viewLifecycleOwner
*/
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                ScriptEditor()
            }

            //binding.root


        }
    }

    @Composable
    fun ScriptEditor() {
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


}
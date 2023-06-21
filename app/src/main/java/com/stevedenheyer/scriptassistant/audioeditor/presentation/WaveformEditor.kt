package com.stevedenheyer.scriptassistant.audioeditor.presentation

import android.util.Log
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Button
import androidx.compose.material3.Slider
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.os.bundleOf
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.fragment.findNavController
import com.stevedenheyer.scriptassistant.R
import com.stevedenheyer.scriptassistant.audioeditor.components.HorizontalSlider
import com.stevedenheyer.scriptassistant.audioeditor.components.SentenceMarkerCanvas
import com.stevedenheyer.scriptassistant.audioeditor.components.WaveformCanvas
import com.stevedenheyer.scriptassistant.audioeditor.domain.model.Sentence
import com.stevedenheyer.scriptassistant.audioeditor.domain.model.Waveform
import com.stevedenheyer.scriptassistant.audioeditor.viewmodels.WaveformUniverseViewModel

@Composable
fun WaveformEditor(modifier: Modifier, waveformVM: WaveformUniverseViewModel, onNavigateToImport: () -> Unit) {

    val waveform by waveformVM.waveform.collectAsStateWithLifecycle(initialValue = Waveform(id = 0, data = emptyArray<Byte>().toByteArray(), isLoading = true))
    val sentences by waveformVM.sentences.collectAsStateWithLifecycle(initialValue = emptyList())
    val pause by waveformVM.pause.collectAsStateWithLifecycle(initialValue = 0F)
    val threshold by waveformVM.threshold.collectAsStateWithLifecycle(initialValue = 0F)
    val currentAudioIndex by waveformVM.currentAudioIndex.collectAsStateWithLifecycle(initialValue = 0)
    val tabs by waveformVM.audioFileTabUiState.collectAsStateWithLifecycle(initialValue = emptyList())

    Column(modifier = modifier, verticalArrangement = Arrangement.SpaceBetween) {
        Row {
            Button(modifier = Modifier, onClick = {
                onNavigateToImport()
                //findNavController().navigate(R.id.fileBrowserFragment, bundleOf("projectId" to args.projectId))
            }) {
                Text(text = "Import")
            }
            if (tabs.isNotEmpty()) {
                TabRow(selectedTabIndex = currentAudioIndex) {
                    tabs.forEachIndexed { index, tab ->
                        Tab(selected = currentAudioIndex == tab.id.toInt(),
                            onClick = { waveformVM.setCurrentAudioId(tab.id) },
                            text = { Text(tab.name) })
                    }
                }
            }
        }

        Row(modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.SpaceBetween) {
            HorizontalSlider(
                modifier = Modifier,
                threshold = threshold,
                onValueChanged = { value -> waveformVM.setThreshold(value)},
                onValueChangedFinished = { waveformVM.setUserIsDoneChangingSettings() })

            WaveformPageView(modifier = Modifier
                , waveform = waveform, sentences ?: emptyList()
            )
        }

        Row(modifier = Modifier) {
            Spacer(modifier = Modifier
                .width(20.dp)
                .height(0.dp))
            Slider(modifier = Modifier, value = pause, valueRange = 10F..1000F, onValueChange = { value -> waveformVM.setPause(value) },
                onValueChangeFinished = { waveformVM.setUserIsDoneChangingSettings() })
        }
    }
}

@Composable
fun WaveformPageView(modifier: Modifier, waveform: Waveform, sentences: List<Sentence>) {
    Log.d("EDFRG", "Item: ${waveform.data.size}")
    Box(
        modifier = Modifier
            .horizontalScroll(rememberScrollState())
    ) {
        val color = if (waveform.isLoading) Color.Gray else Color.Green
        WaveformCanvas(modifier = Modifier.fillMaxHeight(), waveform = waveform.data, color = color)
        SentenceMarkerCanvas(
            modifier = Modifier,
            sentences = sentences,
            updateSentence = { index, sentence -> }) {
        }
    }
}

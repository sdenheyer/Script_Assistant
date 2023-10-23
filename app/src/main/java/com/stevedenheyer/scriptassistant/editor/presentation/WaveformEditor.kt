package com.stevedenheyer.scriptassistant.editor.presentation

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.DraggableState
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Slider
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.stevedenheyer.scriptassistant.R
import com.stevedenheyer.scriptassistant.editor.components.HorizontalSlider
import com.stevedenheyer.scriptassistant.editor.components.WaveformCanvas
import com.stevedenheyer.scriptassistant.common.domain.model.audio.SentenceAudio
import com.stevedenheyer.scriptassistant.editor.domain.model.Waveform
import com.stevedenheyer.scriptassistant.editor.viewmodels.WaveformEditorViewModel
import java.lang.Float.max
import java.lang.Float.min

@Composable
fun WaveformEditor(modifier: Modifier, waveformVM: WaveformEditorViewModel, onNavigateToImport: () -> Unit, draggableState: DraggableState, onDragFinished: () -> Unit) {

   // val sentences by waveformVM.sentences.collectAsStateWithLifecycle(initialValue = emptyList())
    val pause by waveformVM.pause.collectAsStateWithLifecycle(initialValue = 0F)
    val threshold by waveformVM.threshold.collectAsStateWithLifecycle(initialValue = 0F)
    val currentAudioIndex by waveformVM.currentAudioIndex.collectAsStateWithLifecycle(initialValue = 0)
    val tabs by waveformVM.audioFileTabUiState.collectAsStateWithLifecycle(initialValue = emptyList())

    Column(modifier = modifier, verticalArrangement = Arrangement.SpaceBetween) {
        Row (horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Button(modifier = Modifier, onClick = {
                onNavigateToImport()
            }) {
                Text(text = "Import")
            }
            if (tabs.isNotEmpty()) {
                TabRow(selectedTabIndex = currentAudioIndex, modifier = Modifier.weight(1f)) {
                    tabs.forEachIndexed { index, tab ->
                        Tab(selected = currentAudioIndex == index,
                            onClick = { waveformVM.setCurrentAudioId(tab.id) },
                            text = { Text(tab.name) })
                    }
                }
            }
            Image(
                modifier = Modifier
                    .draggable(draggableState, Orientation.Vertical, reverseDirection = true, onDragStopped = { onDragFinished() }),
                painter = painterResource(R.drawable.baseline_unfold_more_24),
                contentDescription = "",
                alignment = Alignment.Center)
        }

        Row(modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.SpaceBetween) {
            val waveform by waveformVM.waveform.collectAsStateWithLifecycle(initialValue = Waveform(id = 0, data = emptyArray<Byte>().toByteArray(), size = 0, isLoading = true))
            val sentences by waveformVM.sentences.collectAsStateWithLifecycle(initialValue = emptyList())

            HorizontalSlider(
                modifier = Modifier,
                threshold = threshold,
                onValueChanged = { value -> waveformVM.setThreshold(value)},
                onValueChangedFinished = { waveformVM.setUserIsDoneChangingSettings() })

            WaveformView(modifier = Modifier,
                waveform = { waveform },
                sentences = { sentences },
                updateSentence = { index, sentence -> waveformVM.setMark(index, sentence) },
                dragStopped = { waveformVM.setUserIsDoneChangingSettings() }
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
/*

@Composable
fun WaveformPageView(modifier: Modifier, waveformVM: WaveformEditorViewModel,
                     //sentences: List<SentenceAudio>,
                     updateSentence: (Int, SentenceAudio) -> Unit, dragStopped: () -> Unit) {
    //Log.d("EDR", "Item: ${waveform.data.size}")
        val waveform by waveformVM.waveform.collectAsStateWithLifecycle(initialValue = Waveform(id = 0, data = emptyArray<Byte>().toByteArray(), size = 0, isLoading = true))
        var maxScale = 1f
        var width = 1f
        var scale by remember { mutableStateOf(1f) }
        var offsetX by remember { mutableStateOf(0f) }
        val state = rememberTransformableState { scaleChange, offsetChange, _ ->
            scale = max(scaleChange * scale, maxScale)
            offsetX = max(min(offsetX + offsetChange.x, 0f), -(waveform.size ?: 0) * scale + width)
           // Log.d("EDR", "scale $scale offset $offsetX")
        }
        val color = if (waveform.isLoading) Color.Gray else Color.Green
        BoxWithConstraints(
            modifier = modifier
                .clipToBounds()
                .transformable(state = state)
                // .offset { IntOffset(offsetX.roundToInt(), 0) }
                .graphicsLayer {

                    transformOrigin = TransformOrigin(0f, 0f)
                    scaleX = scale
                    translationX = offsetX
                }

            //.horizontalScroll(rememberScrollState())
        ) {
            width = constraints.maxWidth.toFloat()
            maxScale = constraints.maxWidth / (waveform.size ?: 0).toFloat()
            scale = maxScale
            Log.d("EDR", "minscale $maxScale width ${constraints.maxWidth}")

            val waveform_ by waveformVM.waveform.collectAsStateWithLifecycle(initialValue = Waveform(id = 0, data = emptyArray<Byte>().toByteArray(), size = 0, isLoading = true))
            val sentences by waveformVM.sentences.collectAsStateWithLifecycle(initialValue = emptyList())

            WaveformCanvas(
                modifier = Modifier,
                waveform = waveform_.data,
                color = color
            )
            SentenceMarkers(
                modifier = Modifier,
                sentences = sentences,
                updateSentence = { index, sentence -> updateSentence(index, sentence) },
                dragStopped = { dragStopped() })
    }
}
*/

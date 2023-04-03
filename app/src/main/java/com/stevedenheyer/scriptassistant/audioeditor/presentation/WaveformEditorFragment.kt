package com.stevedenheyer.scriptassistant.audioeditor.presentation

import android.os.Bundle
import android.util.Log
import android.util.Range
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.core.os.bundleOf
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import com.stevedenheyer.scriptassistant.R
import com.stevedenheyer.scriptassistant.audioeditor.components.SentenceMarkerCanvas
import com.stevedenheyer.scriptassistant.audioeditor.components.WaveformCanvas
import com.stevedenheyer.scriptassistant.audioeditor.domain.model.Sentence
import com.stevedenheyer.scriptassistant.audioeditor.domain.model.Waveform
import com.stevedenheyer.scriptassistant.audioeditor.viewmodels.WaveformUniverseViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlin.random.Random

@AndroidEntryPoint
class WaveformEditorFragment : Fragment() {

    companion object {
        fun newInstance() = WaveformEditorFragment()
    }

    private val args:WaveformEditorFragmentArgs by navArgs()

    private val waveformUniverseVM: WaveformUniverseViewModel by navGraphViewModels(R.id.waveformEditorFragment) {defaultViewModelProviderFactory}
   // private val settingsVM: SettingsViewModel by navGraphViewModels(R.id.waveformEditorFragment) { defaultViewModelProviderFactory }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent{
                WaveformEditor(waveformVM = waveformUniverseVM)
            }
        }
    }

    @OptIn(ExperimentalLifecycleComposeApi::class)
    @Composable
    fun WaveformEditor(waveformVM: WaveformUniverseViewModel) {

        val waveform by waveformVM.waveform.collectAsStateWithLifecycle(initialValue = Waveform(id = 0, data = emptyArray<Byte>().toByteArray(), isLoading = true))
        val sentences by waveformVM.sentences.collectAsStateWithLifecycle(initialValue = emptyList())
        val pause by waveformVM.pause.collectAsStateWithLifecycle(initialValue = 0F)
        val threshold by waveformVM.threshold.collectAsStateWithLifecycle(initialValue = 0F)
        val currentAudioIndex by waveformVM.currentAudioIndex.collectAsStateWithLifecycle(initialValue = 0)
        val tabs by waveformUniverseVM.audioFileTabUiState.collectAsStateWithLifecycle(initialValue = emptyList())
        Column(verticalArrangement = Arrangement.SpaceBetween) {
            Row {
                Button(modifier = Modifier, onClick = {
                    findNavController().navigate(R.id.fileBrowserFragment, bundleOf("projectId" to args.projectId))
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
                    onValueChanged = { value -> waveformVM.setThreshold(value)})

                WaveformPageView(modifier = Modifier
                    , waveform = waveform, sentences ?: emptyList()
                )
            }

            Row(modifier = Modifier) {
                Spacer(modifier = Modifier
                    .width(20.dp)
                    .height(0.dp))
                Slider(modifier = Modifier, value = pause, valueRange = 0F..1000F, onValueChange = { value -> waveformVM.setPause(value) })
            }
        }
    }

    @Composable
    fun HorizontalSlider(modifier: Modifier, threshold: Float, onValueChanged: (Float) -> Unit) {
            Slider(
                modifier = modifier
                    .graphicsLayer {
                        rotationZ = 270f
                        transformOrigin = TransformOrigin(0f, 0f)
                    }
                    .layout { measurable, constraints ->
                        val placeable = measurable.measure(
                            Constraints(
                                minWidth = constraints.minHeight,
                                maxWidth = constraints.maxHeight,
                                minHeight = constraints.minWidth,
                                maxHeight = constraints.maxWidth,
                            )
                        )
                        layout(placeable.height, placeable.width) {
                            placeable.placeRelative(-placeable.width, 0)
                        }

                    }
                    ,
                value = threshold, onValueChange = { threshold -> onValueChanged(threshold) }, valueRange = 0f..100f
            )
    }

    @Composable
    fun WaveformPageView(modifier: Modifier, waveform: Waveform, sentences: List<Sentence>) {
                    Log.d("EDFRG", "Item: ${waveform.data.size}")
                    Box(
                        modifier = Modifier
                            .horizontalScroll(rememberScrollState())
                    ) {
                        WaveformCanvas(modifier = Modifier, waveform = waveform)
                        SentenceMarkerCanvas(
                            modifier = Modifier,
                            sentences = sentences,
                            updateSentence = { index, sentence -> }) {
                        }
                    }
        }

    @Preview(showBackground = true)
    @Composable
    fun WaveformPagePreview() {
        val dummyData = ByteArray(10000) { (Random.nextInt(-64, 64)).toByte() }
        val waveform = Waveform(id = 0, data = dummyData, isLoading = false)
        val sentences = listOf(Sentence(lineId = 0, range = Range(0, 300), take = 0))
        WaveformPageView(modifier = Modifier, waveform = waveform, sentences = sentences)
    }

    @Preview(heightDp = 400, widthDp = 200)
    @Composable
    fun Editor() {
        val dummyData = ByteArray(10000) { (Random.nextInt(-64, 64)).toByte() }
        val waveform = Waveform(id = 0, data = dummyData, isLoading = false)
        val sentences = listOf(Sentence(lineId = 0, range = Range(0, 300), take = 0))

        Column(verticalArrangement = Arrangement.SpaceBetween) {
            Button(modifier = Modifier, onClick = {}) {
                Text(text = "Import")
            }
            Row(modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.SpaceBetween) {
                HorizontalSlider(
                    modifier = Modifier,
                    threshold = 0f,
                    onValueChanged = {})

                WaveformPageView(modifier = Modifier
                    , waveform, sentences)
            }
            Row(modifier = Modifier) {
                Spacer(modifier = Modifier
                    .width(20.dp)
                    .height(0.dp))
                Slider(modifier = Modifier, value = 0f, onValueChange = {})
            }
        }
    }

    @Preview(backgroundColor = 0, heightDp = 400, widthDp = 200)
    @Composable
    fun EditorConstraint() {
        ConstraintLayout(modifier = Modifier.fillMaxSize()) {

            val (thresholdSlider, pauseSlider, waveformView, placeholderButton) = createRefs()

            Button(modifier = Modifier.constrainAs(placeholderButton) {
                //top.linkTo(parent.top, 2.dp)
                start.linkTo(parent.start, 6.dp)
                bottom.linkTo(thresholdSlider.top)
            }, onClick = {}) {
                Text(text = "Placeholder")
            }

            /*  Slider(modifier = Modifier
              .constrainAs(pauseSlider) {
                  absoluteLeft.linkTo(thresholdSlider.absoluteRight, 2.dp)
                  bottom.linkTo(parent.bottom, 4.dp)
              }
              , value = pause, onValueChange = {})
*/
            HorizontalSlider(
                modifier = Modifier
                    .constrainAs(thresholdSlider) {
                        top.linkTo(placeholderButton.bottom)
                        absoluteLeft.linkTo(parent.absoluteLeft)
                        bottom.linkTo(parent.bottom)
                        height = Dimension.fillToConstraints
                    },
                threshold = 0F,
                onValueChanged = {}
            )
        }
    }
}
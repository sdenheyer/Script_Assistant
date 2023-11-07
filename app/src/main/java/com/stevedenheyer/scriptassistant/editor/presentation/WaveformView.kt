package com.stevedenheyer.scriptassistant.editor.presentation

import android.util.Log
import android.util.Range
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import com.stevedenheyer.scriptassistant.common.domain.model.audio.SentenceAudio
import com.stevedenheyer.scriptassistant.editor.components.drawWaveform
import com.stevedenheyer.scriptassistant.editor.domain.model.Waveform
import kotlin.math.roundToInt

@Composable
fun WaveformView(
    modifier: Modifier,
    waveform: () -> Waveform,
    sentences: () -> List<SentenceAudio>,
    updateSentence: (Int, SentenceAudio) -> Unit,
    dragStopped: () -> Unit
) {
    var scale by remember { mutableStateOf(1f) }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .clipToBounds(),
        propagateMinConstraints = true
    ) {

        val widthDp = LocalDensity.current.run { (waveform().size ?: constraints.minWidth).toDp() }
        val width = constraints.minWidth.toFloat()
        val maxScale = width / widthDp.value
        //scale = maxScale

        Log.d(
            "WFMV",
            "Markers recompose width: $widthDp window: $width maxscale: $maxScale scale: $scale"
        )
        var offsetX by remember { mutableStateOf(0f) }
        val state = rememberTransformableState { scaleChange, offsetChange, _ ->
            scale = java.lang.Float.max(scaleChange * scale, maxScale)
            offsetX = java.lang.Float.max(
                java.lang.Float.min(offsetX + offsetChange.x, 0f),
                -((waveform().size ?: 0) * scale + width)
            )
            Log.d("EDR", "scale $scale offset $offsetX")
        }

        val markIn = remember {
            MarkIn(maxHeight)
        }
        val markOut = remember {
            MarkOut(maxHeight)
        }

        Box(modifier = Modifier
            .width(widthDp)
            .fillMaxHeight()
            .graphicsLayer {
                transformOrigin = TransformOrigin(0f, 0f)
                scaleX = scale
                translationX = offsetX
            }
            .transformable(state = state)
            // .offset { IntOffset(offsetX.roundToInt(), 0) }

     /*   ) {
            Box(
                modifier = Modifier*/
                    .drawWaveform(
                        waveform = waveform().data,
                        color = if (waveform().isLoading) Color.Gray else Color.Green
                    )
            )

            /* .drawWithCache {
                 Log.d("WFMV", "Waveform recached")
                 scale = maxScale
                 val height = size.height / 2
                 val centerY = size.center.y

                 val waveformFloats = waveform().data.map { byte ->
                     centerY + (byte * height / 128F)
                 }

                 onDrawBehind {
                     Log.d("WFMV", "Waveform redraw")
                     waveformFloats.forEachIndexed { index, byte ->
                         val x = index.toFloat() / 2
                         drawLine(
                             start = Offset(x = x, y = centerY),
                             end = Offset(x = x, y = byte),
                             color = if (waveform().isLoading) Color.Gray else Color.Green
                         )
                     }
                 }*/

            //   }

            /*    .graphicsLayer {
                    transformOrigin = TransformOrigin(0f, 0f)
                    scaleX = scale
                    translationX = offsetX
                }*/


            // )
            {
                sentences().forEachIndexed { index, sentence ->
                    Marker(
                        modifier = Modifier,
                        painter = markIn,
                        horizontalOffset = sentence.waveformRange.lower,
                        onDrag = { offset ->
                            updateSentence(
                                index,
                                sentence.copy(Range(offset, sentence.waveformRange.upper))
                            )
                        },
                        dragStopped = dragStopped
                    )
                    Marker(
                        modifier = Modifier,
                        painter = markOut,
                        horizontalOffset = sentence.waveformRange.upper,
                        onDrag = { offset ->
                            updateSentence(
                                index,
                                sentence.copy(Range(sentence.waveformRange.lower, offset))
                            )
                        },
                        dragStopped = dragStopped
                    )
                }
            }
        }
    }
//}

class MarkIn(val heightInDp: Dp) : Painter() {
    var height: Float = heightInDp.value
    override val intrinsicSize: Size
        get() = Size(12f, height)

    override fun DrawScope.onDraw() {
        height = heightInDp.toPx()
        drawLine(
            start = Offset(0f, height),
            end = Offset(0f, 0f),
            color = Color.Black,
            strokeWidth = 6f
        )
        drawLine(
            start = Offset(0f, 0f),
            end = Offset(12F, 0f),
            color = Color.Black,
            strokeWidth = 6f
        )
        drawLine(
            start = Offset(0f, height),
            end = Offset(12F, height),
            color = Color.Black,
            strokeWidth = 6f
        )
    }
}

class MarkOut(val heightInDp: Dp) : Painter() {
    var height: Float = heightInDp.value
    override val intrinsicSize: Size
        get() = Size(12f, height)

    override fun DrawScope.onDraw() {
        height = heightInDp.toPx()
        drawLine(
            start = Offset(12f, height),
            end = Offset(12f, 0f),
            color = Color.Black,
            strokeWidth = 6f
        )
        drawLine(
            start = Offset(0f, 0f),
            end = Offset(12F, 0f),
            color = Color.Black,
            strokeWidth = 6f
        )
        drawLine(
            start = Offset(0f, height),
            end = Offset(12F, height),
            color = Color.Black,
            strokeWidth = 6f
        )
    }
}

@Composable
fun Marker(
    modifier: Modifier,
    painter: Painter,
    horizontalOffset: Int,
    onDrag: (Int) -> Unit,
    dragStopped: () -> Unit
) {
    Image(
        painter = painter,
        contentDescription = "Marker",
        modifier = modifier
            .graphicsLayer {
                translationX = horizontalOffset.toFloat()
            }
            .wrapContentWidth(align = Alignment.Start)
            //.fillMaxHeight()
            .draggable(rememberDraggableState(onDelta = { delta ->
                Log.d("MRKR", "Drag...")
                val offset = horizontalOffset + delta.roundToInt()
                onDrag(offset)
            }), orientation = Orientation.Horizontal,
                onDragStopped = { dragStopped() }),
        // contentScale = ContentScale.FillBounds
    )
}
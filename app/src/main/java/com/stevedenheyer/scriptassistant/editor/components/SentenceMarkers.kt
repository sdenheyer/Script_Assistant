package com.stevedenheyer.scriptassistant.editor.components

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.stevedenheyer.scriptassistant.common.domain.model.audio.SentenceAudio
import android.util.Range
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.Dp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlin.math.roundToInt

/*
@Composable
fun SentenceMarkers(modifier: Modifier, sentences:() -> List<SentenceAudio>, updateSentence: (Int, SentenceAudio) -> Unit, dragStopped: () -> Unit) {

    BoxWithConstraints(
        modifier = modifier
            .fillMaxHeight(),
        propagateMinConstraints = true) {

        val markIn = remember {
            MarkIn(minHeight)
        }
        val markOut = remember {
            MarkOut(minHeight)
        }
        sentences().forEachIndexed { index, sentence ->
                Marker(modifier = Modifier,
                    painter = markIn,
                    horizontalOffset = sentence.waveformRange.lower ,
                    onDrag = { offset -> updateSentence(index, sentence.copy(Range(offset, sentence.waveformRange.upper))) },
                    dragStopped = dragStopped)
                Marker(modifier = Modifier,
                    painter = markOut,
                    horizontalOffset = sentence.waveformRange.upper,
                    onDrag = { offset -> updateSentence(index, sentence.copy(Range(sentence.waveformRange.lower, offset))) },
                    dragStopped = dragStopped)
        }
    }
}


class MarkIn (val heightInDp: Dp) :Painter() {
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

    class MarkOut (val heightInDp: Dp) :Painter() {
        var height: Float = heightInDp.value
        override val intrinsicSize: Size
            get() = Size(12f, height)
        override fun DrawScope.onDraw() {
            height = heightInDp.toPx()
            drawLine(start = Offset(12f, height),
                end = Offset(12f, 0f),
                color = Color.Black,
                strokeWidth = 6f
            )
            drawLine(start = Offset(0f, 0f),
                end = Offset(12F, 0f),
                color = Color.Black,
                strokeWidth = 6f)
            drawLine(start = Offset(0f, height),
                end = Offset(12F, height),
                color = Color.Black,
                strokeWidth = 6f)
        }

}

@Composable
fun Marker(modifier: Modifier, painter: Painter, horizontalOffset: Int, onDrag: (Int) -> Unit, dragStopped: () -> Unit) {
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
*/


/*@Composable
fun MarkIn(modifier: Modifier, horizontalOffset: Int, onDrag: (Int) -> Unit, dragStopped: () -> Unit) {
    Marker(
        modifier = modifier,
        painter = key(R.drawable.ic_baseline_mark_in_24) { painterResource(id = R.drawable.ic_baseline_mark_in_24) },
        horizontalOffset = horizontalOffset,
        onDrag = onDrag,
        dragStopped = dragStopped)
}

@Composable
fun MarkOut(modifier: Modifier, horizontalOffset: Int, onDrag: (Int) -> Unit, dragStopped: () -> Unit) {
    Marker(
        painter = key(R.drawable.ic_baseline_mark_out_24) { painterResource(id = R.drawable.ic_baseline_mark_out_24) },
        modifier = modifier,
        horizontalOffset = horizontalOffset,
        onDrag = onDrag,
        dragStopped = dragStopped)
}*/
/*

@Preview
@Composable
fun SentenceMarkerPreview() {
    val _sentences = MutableStateFlow(listOf(SentenceAudio(scriptLineId = 0, waveformRange = Range(5, 50), scriptTake = 0)))
    val sentences by _sentences.collectAsState()

    Box(modifier = Modifier
        .fillMaxWidth()
        .height(100.dp)) {
        SentenceMarkers(modifier = Modifier.fillMaxSize(), sentences = sentences,
            updateSentence = { index, sentence ->
                _sentences.update { sentences ->
                    sentences
                }
        },
            dragStopped = {})
    }
}*/

package com.stevedenheyer.scriptassistant.audioeditor.components

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.stevedenheyer.scriptassistant.R
import com.stevedenheyer.scriptassistant.audioeditor.domain.model.Sentence
import android.util.Range
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.*
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import com.stevedenheyer.scriptassistant.audioeditor.domain.model.SentencesCollection
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlin.math.roundToInt


@Composable
fun SentenceMarkerCanvas(modifier: Modifier, sentences:List<Sentence>, updateSentence: (Int, Sentence) -> Unit, dragStopped: () -> Unit) {
  //  Log.d("MRKR", "Composing...")
    val width = try {
        sentences.last().range.upper * 2
    } catch (e: NoSuchElementException) {
        0
    }
    LazyRow(modifier.width(width.dp)) {
        itemsIndexed(items = sentences) { index, sentence ->
                MarkIn(modifier = Modifier,
                    horizontalOffset = sentence.range.lower ,
                    onDrag = { offset -> updateSentence(index, sentence.copy(Range(offset, sentence.range.upper))) },
                    dragStopped = dragStopped)
                MarkOut(modifier = Modifier,
                    horizontalOffset = sentence.range.upper,
                    onDrag = { offset -> updateSentence(index, sentence.copy(Range(sentence.range.lower, offset))) },
                    dragStopped = dragStopped)
        }
    }
}

@Composable
fun Marker(modifier: Modifier, painter: Painter, horizontalOffset: Int, onDrag: (Int) -> Unit, dragStopped: () -> Unit) {
    Image(
        painter = painter,
        contentDescription = "Marker",
        modifier = modifier
            .absoluteOffset(horizontalOffset.dp, 0.dp)
            .fillMaxHeight()
            .scale(1.5F)
            .draggable(rememberDraggableState(onDelta = { delta ->
                Log.d("MRKR", "Drag...")
                val offset = horizontalOffset + delta.roundToInt()
                onDrag(offset)
            }), orientation = Orientation.Horizontal,
                onDragStopped = { dragStopped() }),
        contentScale = ContentScale.FillBounds
    )
}


@Composable
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
}

@Preview
@Composable
fun SentenceMarkerPreview() {
    val _sentences = MutableStateFlow(listOf(Sentence(lineId = 0, range = Range(5, 50), take = 0)))
    val sentences by _sentences.collectAsState()

    Box(modifier = Modifier
        .fillMaxWidth()
        .height(100.dp)) {
        SentenceMarkerCanvas(modifier = Modifier.fillMaxSize(), sentences = sentences,
            updateSentence = { index, sentence ->
                _sentences.update { sentences ->
                  //  sentences[index] = sentence
                    sentences
                }
        },
            dragStopped = {})
    }
}
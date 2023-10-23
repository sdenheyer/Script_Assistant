package com.stevedenheyer.scriptassistant.editor.components

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.stevedenheyer.scriptassistant.editor.domain.model.Waveform
import kotlin.random.Random

@Composable
fun WaveformCanvas(modifier: Modifier, waveform: ByteArray, color: Color) {

    Box(modifier = modifier.fillMaxHeight().width((waveform.size / 2).dp).drawWithCache {
        val height = size.height / 2
        val centerY = size.center.y

        val waveformFloats = waveform.map {byte ->
            centerY + (byte * height / 128F)
        }

            onDrawBehind {
                waveformFloats.forEachIndexed { index, byte ->
                    val x = index.toFloat() / 2
                    drawLine(
                                    start = Offset(x = x, y = centerY),
                                    end = Offset(x = x, y = byte),
                                    color = color
                                )
                }
            }
    })
}

@Preview
@Composable
fun waveformPreview() {
    val dummyData = ByteArray(100) { (Random.nextInt(-128, 128)).toByte() }
    val waveform = Waveform(id = 0, data = dummyData, size = dummyData.size / 2, isLoading = true)

    Box(modifier = Modifier.height(100.dp).wrapContentWidth()) {
        WaveformCanvas(modifier = Modifier, waveform = dummyData, color = Color.Gray)
    }
}
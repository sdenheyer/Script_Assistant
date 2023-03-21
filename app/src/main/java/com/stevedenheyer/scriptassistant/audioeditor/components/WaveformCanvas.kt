package com.stevedenheyer.scriptassistant.audioeditor.components

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.stevedenheyer.scriptassistant.audioeditor.domain.model.Waveform
import kotlin.random.Random

@Composable
fun WaveformCanvas(modifier: Modifier, waveform: Waveform) {

    androidx.compose.foundation.Canvas(modifier = modifier.fillMaxHeight().width((waveform.data.size / 2).dp)) {
        val height = size.height / 2
        val centerY = size.center.y

        //Log.d("CVS", "Canvas size: $height ${size.width}")

        waveform.data.forEachIndexed { index, byte ->
            val x = (index / 2).toFloat()
            val y = centerY + (byte * height / 128F)
            drawLine(
                start = Offset(x = x, y = centerY),
                end = Offset(x = x, y = y),
                color = if (waveform.isLoading) Color.Green else Color.Gray
            )
        }
    }
}

@Preview
@Composable
fun waveformPreview() {
    val dummyData = ByteArray(100) { (Random.nextInt(-128, 128)).toByte() }
    val waveform = Waveform(id = 0, data = dummyData, isLoading = true)

    Box(modifier = Modifier.height(100.dp).wrapContentWidth()) {
        WaveformCanvas(modifier = Modifier, waveform = waveform)
    }
}
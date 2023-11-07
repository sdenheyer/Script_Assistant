package com.stevedenheyer.scriptassistant.editor.components

import android.util.Log
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Color

@Stable
fun Modifier.drawWaveform(waveform: ByteArray, color: Color): Modifier = composed {
    drawWithCache {
      //  Log.d("WFMV", "Waveform recached")
        val height = size.height / 2
        val centerY = size.center.y

        val waveformFloats = waveform.map { byte ->
            centerY + (byte * height / 128F)
        }

        onDrawBehind {
          //  Log.d("WFMV", "Waveform redraw")
            waveformFloats.forEachIndexed { index, byte ->
                val x = index.toFloat() / 2
                drawLine(
                    start = Offset(x = x, y = centerY),
                    end = Offset(x = x, y = byte),
                    color = color
                )
            }
        }
    }
}
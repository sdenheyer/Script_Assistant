package com.stevedenheyer.scriptassistant.common.data.waveform

import android.util.Log
import com.stevedenheyer.scriptassistant.common.data.waveform.model.GenWaveform
import com.stevedenheyer.scriptassistant.common.data.waveform.utils.WaveformState
import com.stevedenheyer.scriptassistant.di.ApplicationScope
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

const val WFM_WINDOW_SIZE = 90

class WaveformGenerator  {
    private lateinit var fileOutputStream: FileOutputStream
    private val byteList = ArrayList<Byte>()

    operator fun invoke(id: Long, projectedSamples: Long, wfmFile: File, sampleInputFlow: Flow<ShortArray?>):Flow<WaveformState> {

        fileOutputStream = FileOutputStream(wfmFile)

        val projectedSize = (projectedSamples / WFM_WINDOW_SIZE).toInt()

        return sampleInputFlow.transform { samples ->

                var counter = 0
                var maxSample: Short = 0
                var minSample: Short = 0

                if (samples != null) {
                    samples.forEach { sample ->
                        counter++
                        if (sample > maxSample) {
                            maxSample = sample
                        } else if (sample < minSample) {
                            minSample = sample
                        }
                        if (counter > WFM_WINDOW_SIZE) {
                            byteList.add((maxSample / 256).toByte())
                            byteList.add((minSample / 256).toByte())

                            counter = 0
                            maxSample = 0
                            minSample = 0
                        }
                    }
                   // Log.d("TEMP", "wfm emitted $byteList")
                    emit(WaveformState.Loading(GenWaveform(id, projectedSize, byteList.toByteArray())))
                } else {
                    emit(WaveformState.Success(GenWaveform(id, projectedSize, byteList.toByteArray())))
                    close()
                    //cancel()
                }
    }
}

fun close() {
    Log.d("TEMP", "writing file....")
    fileOutputStream.write(byteList.toByteArray())
    fileOutputStream.close()
    Log.d("TEMP", "Closed file....")
}

}
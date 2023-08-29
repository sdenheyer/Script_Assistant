package com.stevedenheyer.scriptassistant.common.data.waveform

import android.util.Log
import com.stevedenheyer.scriptassistant.common.data.waveform.model.GenWaveform
import com.stevedenheyer.scriptassistant.common.data.waveform.utils.WaveformState
import java.io.BufferedInputStream
import java.io.DataInputStream
import java.io.File
import java.io.FileInputStream
import kotlin.math.roundToInt

class GetWaveformFromFile {
    operator fun invoke(id: Long, wfmFile: File) : WaveformState {
        if (wfmFile.exists()) {
            DataInputStream(BufferedInputStream(FileInputStream(wfmFile))).use {
                val bytes = ByteArray(wfmFile.length().toInt())
                it.readFully(bytes)
                it.close()
                Log.d("TEMP", "sending cache file: ${bytes.size}")
                return WaveformState.Success(GenWaveform(id, (bytes.size / 2f).roundToInt(), bytes))
            }
        } else {
            return WaveformState.Failure("File not found")
        }
    }
}
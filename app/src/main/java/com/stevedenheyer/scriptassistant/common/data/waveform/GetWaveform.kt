package com.stevedenheyer.scriptassistant.common.data.waveform

import android.content.Context
import com.stevedenheyer.scriptassistant.audio.Decoder
import com.stevedenheyer.scriptassistant.common.data.waveform.utils.WaveformState
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.io.File

class GetWaveform constructor(private val context: Context,
                              private val ioDispatcher: CoroutineDispatcher) {

    operator fun invoke(id: Long, audioFile: File) = flow {
        val getWaveformFile = GetWaveformFromFile()
        val wfmFile = File(context.externalCacheDir, id.toString() + ".wfm")
        val state = withContext(ioDispatcher) { getWaveformFile(id, wfmFile) }

        when (state) {
            is WaveformState.Success -> emit(state)
            else -> {
                val waveformGenerator = WaveformGenerator()
                val decoder = Decoder()
                    val projectedSamples = decoder(audioFile)
                    emitAll(waveformGenerator(id, projectedSamples, wfmFile, decoder.getSampleFlow()))
            }
        }
    }
}
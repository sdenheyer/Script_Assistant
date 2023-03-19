package com.stevedenheyer.scriptassistant.common.data.waveform

import android.content.Context
import com.stevedenheyer.scriptassistant.common.data.waveform.utils.WaveformState
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.io.File
import javax.inject.Inject

class GetWaveform constructor(private val context: Context,
                              private val ioDispatcher: CoroutineDispatcher) {

    operator fun invoke(id: Long, audioFile: File) = flow {
        val getWaveformFile = GetWaveformFromFile()
        val wfmFile = File(context.externalCacheDir, id.toString() + ".wfm")
        val state: WaveformState
        withContext(ioDispatcher) { state = getWaveformFile(id, wfmFile) }

        when (state) {
            is WaveformState.Success -> emit(state)
            else -> {
                val waveformGenerator = WaveformGenerator()
                val decoder = Decoder()
                    val wfmFlow = decoder(audioFile)
                    emitAll(waveformGenerator(id, wfmFile, wfmFlow))
            }
        }
    }
}
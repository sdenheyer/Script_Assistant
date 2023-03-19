package com.stevedenheyer.scriptassistant.common.data.waveform

import android.content.Context
import com.stevedenheyer.scriptassistant.common.data.waveform.utils.WaveformState
import com.stevedenheyer.scriptassistant.audioeditor.domain.model.Waveform
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

class WaveformsCollector @Inject constructor(
    private val scope: CoroutineScope,
    private val context: Context,
    private val ioDispatcher: CoroutineDispatcher
) {

    private val waveformsMap = HashMap<Long, Waveform>()

    private val waveformsChannel = Channel<Waveform>()

    private val waveformsFlow =
        MutableSharedFlow<Map<Long, Waveform>>(replay = 1)

    init {
        scope.launch {  while (true) {
            val waveform = waveformsChannel.receive()
            if (waveform.id >= 0) {
                waveformsMap[waveform.id] = waveform
            }
            waveformsFlow.emit(waveformsMap)
        } }
    }

    fun generateWaveforms(files: Map<Long, File>):SharedFlow<Map<Long, Waveform>> {
     //   Log.d("WFMCOL", "Files: ${files.size}")
        files.forEach { file ->
            if (!waveformsMap.containsKey(file.key)) {
                waveformsMap[file.key] = Waveform(file.key, data = emptyArray<Byte>().toByteArray(), true)
             //   Log.d("WFMCOL", "Starting thread..")
                scope.launch {
                    val getWaveform = GetWaveform(context, ioDispatcher)
                    getWaveform(file.key, file.value).collect { state ->
                        waveformsChannel.send(
                            when (state) {
                                is WaveformState.Success -> {
                               // Log.d("WFMCOL", "Success!")
                                        Waveform (state.data.id, state.data.data, false)
                            }
                                is WaveformState.Loading ->
                                    Waveform(state.data.id, state.data.data, true)
                                else ->
                                    Waveform(-1, emptyArray<Byte>().toByteArray(), false)
                            })
                    }
                }

            }
        }
        return waveformsFlow
    }

    fun getWaveformsMapFlow() = waveformsFlow
}
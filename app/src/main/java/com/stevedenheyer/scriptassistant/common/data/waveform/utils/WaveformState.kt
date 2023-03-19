package com.stevedenheyer.scriptassistant.common.data.waveform.utils

import com.stevedenheyer.scriptassistant.common.data.waveform.model.GenWaveform

sealed class WaveformState {
    data class Loading(val data:GenWaveform) : WaveformState()
    data class Failure(val msg:String) : WaveformState()
    data class Success(val data: GenWaveform) : WaveformState()
}

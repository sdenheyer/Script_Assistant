package com.stevedenheyer.scriptassistant.utils

import com.stevedenheyer.scriptassistant.common.data.waveform.WFM_WINDOW_SIZE

var sampleRate: Int = 48000

fun waveformXtoMilliseoncds (x: Int): Int {
return (x.toFloat() / (sampleRate.toFloat() / WFM_WINDOW_SIZE / 1000)).toInt()
}

fun millisecondsToWavformX(ms: Int): Int {
    return (ms * (sampleRate.toFloat() / WFM_WINDOW_SIZE / 1000)).toInt()
}

fun millisecondsToIndex (ms: Int): Int {
    return ((sampleRate.toFloat() / 1000 / WFM_WINDOW_SIZE / 2) * ms).toInt()
}
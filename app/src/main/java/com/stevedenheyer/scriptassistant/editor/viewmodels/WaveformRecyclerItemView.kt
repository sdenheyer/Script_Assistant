package com.stevedenheyer.scriptassistant.editor.viewmodels

import android.util.Range

data class WaveformRecyclerItemView(
    val audioOwnerId: Long,
    val text: String,
    val range: Range<Int>,
    val waveform: ByteArray
) {
}
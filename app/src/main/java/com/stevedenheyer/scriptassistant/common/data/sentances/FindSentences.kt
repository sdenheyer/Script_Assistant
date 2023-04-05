package com.stevedenheyer.scriptassistant.common.data.sentances

import android.util.Range
import com.stevedenheyer.scriptassistant.audioeditor.domain.model.Waveform
import com.stevedenheyer.scriptassistant.utils.millisecondsToIndex
import com.stevedenheyer.scriptassistant.utils.sampleRate
import kotlinx.coroutines.flow.*
import kotlin.math.roundToInt

class FindSentences() {
    private val syllabelLength = millisecondsToIndex(150)

    private val sentenceList = ArrayList<Range<Int>>()
    operator fun invoke(threshold: Byte, pauseLength: Int, waveform: ByteArray):ArrayList<Range<Int>>

    {
       // val pauseLength = millisecondsToIndex(pauseLength_)

        val iterator = waveform.iterator().withIndex()
        var candidate: Int? = null
        var sentanceBegin: Int? = null

        while (iterator.hasNext()) {
            val byte = iterator.next()
            if (byte.index % 2 == 0) {
                if (sentanceBegin == null) {
                    if (byte.value > threshold) {
                        if (candidate == null) {
                            candidate = byte.index
                        } else if ((byte.index - candidate) > syllabelLength) {
                            sentanceBegin = candidate
                            candidate = null
                        }
                    }
                } else if (byte.value < threshold) {
                    if (candidate == null) {
                        candidate = byte.index
                    } else if ((byte.index - candidate) > pauseLength) {
                        sentenceList.add(Range(sentanceBegin / 2, candidate / 2))
                        candidate = null
                        sentanceBegin = null
                    }
                } else if (byte.value > threshold){
                    candidate = null
                }
            }
        }
       // Log.d("TEMP", "Sentence find triggered ${sentanceList.size}")
        return sentenceList
    }
}
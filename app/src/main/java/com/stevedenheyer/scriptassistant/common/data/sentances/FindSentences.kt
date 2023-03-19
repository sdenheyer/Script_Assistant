package com.stevedenheyer.scriptassistant.common.data.sentances

import android.util.Range
import com.stevedenheyer.scriptassistant.utils.millisecondsToIndex
import com.stevedenheyer.scriptassistant.utils.sampleRate
import kotlinx.coroutines.flow.*
import kotlin.math.roundToInt

class FindSentences(val waveform: Flow<ByteArray>) {
    private val syllabelLength = millisecondsToIndex(150)

    private val userIsChanging = MutableStateFlow(false)

    private val _threshold = MutableStateFlow(0F)

    private val threshold = _threshold.map { it.roundToInt().toByte() }.distinctUntilChanged()

    private val _pauseLength = MutableStateFlow(0F)

    private val pauseLength = _pauseLength.map { millisecondsToIndex(it.roundToInt()) }.distinctUntilChanged()

    private val sentenceList = ArrayList<Range<Int>>()

    private val sentenceListFlow = combineTransform(userIsChanging, threshold, pauseLength, waveform) { userIsChanging, threshold, pauseLength, waveform ->

       // val pauseLength = millisecondsToIndex(pauseLength_)

        sentenceList.clear()

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
                        sentenceList.add(Range(sentanceBegin, candidate))
                        candidate = null
                        sentanceBegin = null
                    }
                } else if (byte.value > threshold){
                    candidate = null
                }
            }
        }
       // Log.d("TEMP", "Sentence find triggered ${sentanceList.size}")
        emit(sentenceList)
    }

    fun getSentanceListFlow() = sentenceListFlow

    fun setThreshold(level: Float) {
        _threshold.tryEmit(level)
    }

    fun setPauseLength(length: Float) {
        _pauseLength.tryEmit(length)
    }

    fun setSampleRate(rate: Int) {
        sampleRate = rate
    }

    fun getPauseLength() = _pauseLength

    fun getThreshold() = _threshold
}
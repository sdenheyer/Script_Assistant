package com.stevedenheyer.scriptassistant

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
/*
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }
*/

    val waveformGen = WaveformGenerator()

    @Test
    fun waveformGenTest() {

        GlobalScope.launch {         waveformGen.getWaveformFlow().collect { value -> assertEquals(value, 5.toByte()) }
        }

        val samples:ShortArray = shortArrayOf(0, 0, 0, 0, 5, 5, 0, 0, 0, 0, 5)
        waveformGen.setSamples(samples)
    }
}


package com.stevedenheyer.scriptassistant.audio

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import com.stevedenheyer.scriptassistant.utils.sampleRate
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.launch
import kotlin.math.abs

private const val AudioChannels = AudioFormat.CHANNEL_IN_MONO
private const val AudioCodec = AudioFormat.ENCODING_PCM_16BIT
private const val AudioInput = MediaRecorder.AudioSource.VOICE_RECOGNITION

class Recorder {

    //private val waveformGen = WaveformGenerator(null)   //TODO:  Fix this

    private lateinit var encoder: Encoder

    private val peakFlow = MutableStateFlow(ArrayList<Short>())

    private lateinit var audioRecord: AudioRecord

    fun startListening() {
        val bufferSize = AudioRecord.getMinBufferSize(sampleRate, AudioChannels, AudioCodec)
        audioRecord = AudioRecord(AudioInput,
            sampleRate,
            AudioChannels,
            AudioCodec,
            bufferSize * 2)

        val frameLength = sampleRate / 1000
        val audioBuffer = ShortArray(bufferSize)

        encoder = Encoder()

        audioRecord.startRecording()

        GlobalScope.launch {

            while (true) {
                val size = audioRecord.read(audioBuffer, 0, bufferSize)

                if (encoder.isRecording()) {
                    encoder.addBuffer(audioBuffer.copyOf(size))
                }

                peakFlow.tryEmit(ArrayList(audioBuffer.toList()))
            }
        }
    }

    fun getPeakFlow() = peakFlow.transform { buffer ->
            var peak:Byte = 0
            buffer.forEach { sample ->
                if (abs(sample.toInt()) > peak) {
                    peak = abs((sample/256)).toByte()
                }
            }
            emit(peak)
        }

    fun startRecording(file: String) = encoder.start(file)

    fun stopRecording() =  encoder.stopRecording()

    fun isRecording() = encoder.isRecording()

}
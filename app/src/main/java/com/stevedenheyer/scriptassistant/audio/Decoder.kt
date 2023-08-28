package com.stevedenheyer.scriptassistant.audio

import android.media.*
import android.util.Log
import com.stevedenheyer.scriptassistant.utils.sampleRate
import kotlinx.coroutines.flow.*
import java.io.*
import java.lang.Exception
import java.nio.ByteOrder
import kotlin.math.roundToInt
import kotlin.math.roundToLong

class Decoder  {
    private val sampleOutputFlow = MutableStateFlow<ShortArray?>(ShortArray(0))
    private lateinit var extractor: MediaExtractor
    private lateinit var decoder: MediaCodec
    var format: MediaFormat? = null

    var extractorDone = false

    operator fun invoke(file: File):Long {
        var sampleCount = 0
        extractorDone = false
        extractor = MediaExtractor()

        try {
            val fis = FileInputStream(file)
            val fd = fis.fd
            extractor.setDataSource(fd)
            fis.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        var channel = 0  //TODO:  Make decoder handle stereo files properly, reject higher channels
        (0 until extractor.trackCount).forEach { trackNumber ->
            format = extractor.getTrackFormat(trackNumber)
            Log.d("TEMP", format!!.getString(MediaFormat.KEY_MIME)!!)
            format!!.getString(MediaFormat.KEY_MIME).takeIf { it?.startsWith("audio/") == true }?.let {
                extractor.selectTrack(trackNumber)
                Log.d("TEMP", "format : $format")
                format!!.getByteBuffer("csd-0")?.let { csd ->
                    (0 until csd.capacity()).forEach {
                        Log.e("TEMP", "csd : ${csd.array()[it]}")
                    }
                }

                sampleRate = format!!.getInteger(MediaFormat.KEY_SAMPLE_RATE)
                channel = format!!.getInteger(MediaFormat.KEY_CHANNEL_COUNT)
                return@forEach
            }
        }

        if (format == null) {
            Log.wtf("TEMP", "format is null??")
        }

        val projectedSize = (format!!.getLong(MediaFormat.KEY_DURATION) / 1e-6 * sampleRate).roundToLong()

        decoder = MediaCodec.createByCodecName(MediaCodecList(MediaCodecList.ALL_CODECS).findDecoderForFormat(format))

        decoder.setCallback(object: MediaCodec.Callback() {
            override fun onInputBufferAvailable(codec: MediaCodec, index: Int) {
                val inputBuffer = codec.getInputBuffer(index)
                while (!extractorDone) {
                    val size = extractor.readSampleData(inputBuffer!!, 0)
                    val presentationTime = extractor.sampleTime
                    if (size >= 0) {
                        codec.queueInputBuffer(index, 0, size, presentationTime, extractor.sampleFlags)
                    }
                    extractorDone = !extractor.advance()
                    if (extractorDone) {
                        codec.queueInputBuffer(index, 0, 0, 0, MediaCodec.BUFFER_FLAG_END_OF_STREAM)
                    }
                    if (size >= 0)
                        break
                }
            }

            override fun onOutputBufferAvailable(
                codec: MediaCodec,
                index: Int,
                bufferInfo: MediaCodec.BufferInfo
            ) {
                val shortArray = ShortArray(bufferInfo.size/2)

                codec.getOutputBuffer(index)?.order(ByteOrder.LITTLE_ENDIAN)?.asShortBuffer()?.get(shortArray)

                if (shortArray.isNotEmpty()) {
                  //  Log.d("TEMP", "decoder emitting")
                    val emitted = sampleOutputFlow.tryEmit(shortArray)
                    if (!emitted) {
                        Log.d("TEMP", "Decoder BufferOverFlow")
                    }
                }
                sampleCount += shortArray.size
                codec.releaseOutputBuffer(index, false)
                if ((bufferInfo.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                    extractorDone = true
                    decoder.stop()
                    decoder.release()
                    sampleOutputFlow.tryEmit(null)
                }
            }

            override fun onError(p0: MediaCodec, p1: MediaCodec.CodecException) {
                Log.d("TEMP", "Decoder error: $p1")
            }

            override fun onOutputFormatChanged(codec: MediaCodec, p1: MediaFormat) {
                format = codec.outputFormat
            }

        })

        decoder.configure(format, null, null, 0)

        decoder.start()

        return projectedSize
    }

    fun getSampleFlow() = sampleOutputFlow
}
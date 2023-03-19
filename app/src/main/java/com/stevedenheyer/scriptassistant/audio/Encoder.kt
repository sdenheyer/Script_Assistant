package com.stevedenheyer.scriptassistant.audio

import android.media.*
import android.util.Log
import com.stevedenheyer.scriptassistant.utils.sampleRate
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.TimeUnit

class Encoder() {

    private val format = MediaFormat.createAudioFormat("audio/mp4a-latm", sampleRate, 1)

    private var encoder: MediaCodec

    private val sampleQueue = ArrayBlockingQueue<ShortArray>(20)

    private var isRecording = false

    private var presentationTime: Long = 0
    private var isDone = false

    private lateinit var muxer: MediaMuxer
    var muxerAudioTrack = 0

    init {
        format.setInteger(MediaFormat.KEY_SAMPLE_RATE, sampleRate)
        format.setInteger(MediaFormat.KEY_AAC_PROFILE, MediaCodecInfo.CodecProfileLevel.AACObjectLC)
        format.setInteger(MediaFormat.KEY_CHANNEL_COUNT, 1)
        format.setInteger(MediaFormat.KEY_BIT_RATE, 96000)
        encoder = MediaCodec.createByCodecName(
            MediaCodecList(MediaCodecList.ALL_CODECS).findEncoderForFormat(format)
        )
        encoder.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)

        encoder.setCallback(object : MediaCodec.Callback() {
            override fun onInputBufferAvailable(codec: MediaCodec, index: Int) {
                if (!isDone) {
                    val buffer = codec.getInputBuffer(index)

                    val samples = sampleQueue.poll(500, TimeUnit.MILLISECONDS)
                    if (samples != null) {
                        samples.forEach { sample ->
                            buffer!!.putShort(sample)
                        }
                    } else if (!isRecording) {
                        Log.d("TEMP", "received stop")
                        isDone = true
                    }

                    if (!isDone) {
                        codec.queueInputBuffer(index, 0, samples.size * 2, presentationTime, 0)
                    } else {
                        Log.d("TEMP", "Done")
                        codec.queueInputBuffer(
                            index,
                            0,
                            0,
                            presentationTime,
                            MediaCodec.BUFFER_FLAG_END_OF_STREAM
                        )
                    }
                    try {
                        presentationTime += (((samples.size.toFloat()) / sampleRate) * 1e6F).toLong()
                    } catch (npe: NullPointerException) {

                    }
                }
            }

            override fun onOutputBufferAvailable(
                codec: MediaCodec,
                index: Int,
                info: MediaCodec.BufferInfo
            ) {
                if ((info.flags and MediaCodec.BUFFER_FLAG_CODEC_CONFIG != 0)) {
                    info.size = 0
                }
                if (info.size != 0) {
                    val buffer = codec.getOutputBuffer(index)!!
                    muxer.writeSampleData(muxerAudioTrack, buffer, info)
                }
                codec.releaseOutputBuffer(index, false)
                if ((info.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                    Log.d("TEMP", "releasing encoder")
                    muxer.stop()
                    muxer.release()
                    encoder.stop()
                    encoder.release()
                }

            }

            override fun onError(p0: MediaCodec, p1: MediaCodec.CodecException) {
                Log.d("TEMP", "codec: $p1")
            }

            override fun onOutputFormatChanged(p0: MediaCodec, format: MediaFormat) {
                muxerAudioTrack = muxer.addTrack(format)
                muxer.start()
            }

        })

    }

    fun start(file: String) {
        muxer = MediaMuxer(file, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)

        presentationTime = 0
        isDone = false
        isRecording = true
        encoder.start()
    }

    fun stopRecording() {
        Log.d("TEMP", "Encoder stop")
        isRecording = false
    }

    fun addBuffer(samples: ShortArray) {
        sampleQueue.put(samples)
    }

    fun isRecording() = isRecording
}
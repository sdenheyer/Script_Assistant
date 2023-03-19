package com.stevedenheyer.scriptassistant.audio

import android.media.AudioAttributes
import android.media.MediaFormat
import android.media.MediaPlayer
import android.util.Log
import com.stevedenheyer.scriptassistant.utils.millisecondsToIndex
import com.stevedenheyer.scriptassistant.utils.sampleRate
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import java.io.File
import java.lang.NullPointerException

class Player {
    private val mediaPlayer = MediaPlayer()

    fun open(file: File) {
        mediaPlayer.setDataSource(file.absolutePath)
        mediaPlayer.setAudioAttributes(AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_MEDIA).setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build())
        mediaPlayer.prepare()

        val trackinfo = mediaPlayer.trackInfo
        if (trackinfo.size > 0) {
            var rate = 48000
            trackinfo.forEach { try { rate = it.format.getInteger(MediaFormat.KEY_SAMPLE_RATE) }
            catch (npe: NullPointerException) {

            }
            Log.d("TEMP", "sr found: $rate")}
            sampleRate = rate
        } else {
            Log.d("TEMP", "No track info")
        }
    }

    fun play() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.stop()
            val position = mediaPlayer.currentPosition
            mediaPlayer.prepare()
            mediaPlayer.seekTo(position)
        } else {
            mediaPlayer.start()
        }
    }

    fun getCurrentPositionFlow():Flow<Int> = flow {
        while (true) {
        emit(mediaPlayer.currentPosition)
        delay(100)
    }}

    fun seekTo(ms: Int) {
        mediaPlayer.seekTo(ms)
    }
}
package com.stevedenheyer.scriptassistant.views

import android.content.Context
import android.graphics.*
import android.text.TextPaint
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.MotionEvent.*
import android.view.View
import androidx.annotation.CallSuper
import com.stevedenheyer.scriptassistant.R
import com.stevedenheyer.scriptassistant.utils.millisecondsToWavformX
import com.stevedenheyer.scriptassistant.utils.waveformXtoMilliseoncds
import android.util.Range


open class WaveformView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    interface OnUserChangePlayheadListener {
        fun onPlayheadChanged(positionInMS: Int)
    }

    private var onUserChangePlayheadListener: OnUserChangePlayheadListener? = null

    protected var bytes: ByteArray = emptyArray<Byte>().toByteArray()
    protected var range = Range(0, 0)
    protected var text: String = "TEST!"

    protected var playHeadPosition: Float = 0F

    protected var waveFormPaint = Paint()
    protected var playHeadPaint = Paint()
    protected var textPaint = TextPaint()
    protected var textBGPaint = Paint()

    protected var wfmColor: Int = 0
    protected var wfmColorAlt: Int = 0

    private var downPosX: Float? = null

    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.WaveformView,
            0, 0
        ).apply {
            try {
                wfmColor = getColor(R.styleable.WaveformView_waveFormColor, Color.BLACK)
                wfmColorAlt = getColor(R.styleable.WaveformView_waveFormColorAlt, Color.GRAY)
                setWaveformColor(wfmColor)
                textPaint.color = Color.BLUE
                textPaint.typeface = Typeface.defaultFromStyle(R.styleable.ActionBar_titleTextStyle)
                textBGPaint.color = Color.WHITE
                setPlayheadColor(getColor(R.styleable.WaveformView_playHeadColor, Color.RED))
                setPlayHeadPosition(getInt(R.styleable.WaveformView_playHeadPosition, 0))
                waveFormPaint.strokeWidth = 1F
                playHeadPaint.strokeWidth = 2F
            } finally {
                recycle()
            }
        }
    }

    @CallSuper
    override fun onDraw(canvas: Canvas?) {
        val lines = ArrayList<Float>()

        if (bytes.isNotEmpty()) {
            //TODO:  Optimize by moving object creation elsewhere - probably onMeasure
            //TODO:  Optimize by moving this entire codeblock out of onDraw
           // lines.clear()
            val center = height / 2F
            val scale = 0.5F
            var x = 0F
            var stopY: Float
            bytes.forEach { byte ->
                x += scale
                stopY = center + (byte * height / 128F)
                lines.apply {
                    add(x)
                    add(center)
                    add(x)
                    add(stopY)
                }
              //  lines.addAll(listOf(x, center, x, stopY))
            }
        }

        canvas!!.apply {
            drawLines(lines.toFloatArray(), waveFormPaint)
            if (range.contains(playHeadPosition.toInt())) {
                drawLine(playHeadPosition, 0F, playHeadPosition, height.toFloat(), playHeadPaint)
            }
            if (text.isNotEmpty()) {
                drawRect(
                    18F,
                    18F - textPaint.textSize,
                    22F + textPaint.measureText(text),
                    22F,
                    textBGPaint
                )
                drawText(text, 20F, 20F, textPaint)
            }
        }

    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            ACTION_DOWN -> {
                downPosX = event.x
                Log.d("TEMP", "ACTION DOWN: $downPosX")

            }

            ACTION_SCROLL -> {
                return false           //hand off event to ScrollView
            }

            ACTION_UP -> {
                if (downPosX != null) {
                    playHeadPosition = downPosX as Float
                    invalidate()
                    if (onUserChangePlayheadListener != null) {
                        onUserChangePlayheadListener!!.onPlayheadChanged(waveformXtoMilliseoncds(playHeadPosition.toInt()))
                    }
                }

            }
        }
        return true
    }

    fun setWaveform(waveform: ByteArray) {
        if (waveform.isNotEmpty() && waveform != bytes) {
            bytes = waveform
            invalidate()
            //requestLayout()
        }
    }

    fun getWaveformSize() = bytes.size

    fun setPlayHeadPosition(position: Int) {
        playHeadPosition = millisecondsToWavformX(position).toFloat()
        Log.d("TEMP","PH: $playHeadPosition")
        invalidate()
    }

    protected fun setWaveformColor(color: Int) {
        Log.d("VIEW", "Setting color...")
        waveFormPaint.color = color
    }

    private fun setPlayheadColor(color: Int) {
        playHeadPaint.color = color
    }

    fun setOnUserChangePlayheadListener(listener: OnUserChangePlayheadListener) {
        onUserChangePlayheadListener = listener
    }

    fun setTitle(value: String) {
        if (value != text) {
            text = value
            invalidate()
        }
    }

    fun setAudioRange(value: Range<Int>) {
        range = value
    }
}
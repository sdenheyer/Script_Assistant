package com.stevedenheyer.scriptassistant.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.DragEvent
import com.stevedenheyer.scriptassistant.R
import com.stevedenheyer.scriptassistant.audioeditor.domain.model.Sentence
import kotlin.math.max

class WaveformUniverseView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : WaveformView(context, attrs, defStyleAttr) {

    private var sentences: Array<Sentence> = emptyArray()
    private var sentancePaintIn = Paint()
    private var sentancePaintOut = Paint()
    private var loadingWFM = false

    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.WaveformView,
            0, 0).apply {
                try {
                    sentancePaintIn.color = Color.BLUE
                    sentancePaintIn.strokeWidth = 1F
                    sentancePaintOut.color = Color.RED
                    sentancePaintOut.strokeWidth = 1F
                } finally {
                    recycle()
                }
        }
    }

    override fun onDraw(canvas: Canvas?) {

       // canvas!!.drawLine(playHeadPosition, 0F, playHeadPosition, height.toFloat(), playHeadPaint)
        super.onDraw(canvas)

            sentences.forEach { sentance ->
                canvas!!.drawLine(sentance.range.lower / 2F, 0F, sentance.range.lower / 2F, height.toFloat(), sentancePaintIn)
                canvas.drawLine(sentance.range.upper / 2F, 0F, sentance.range.upper / 2F, height.toFloat(), sentancePaintOut)}
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val height = getDefaultSize(suggestedMinimumHeight, heightMeasureSpec)

        var canvasWidth = 0
        var width = 0

        if (bytes.isNotEmpty()) {
          canvasWidth = bytes.size / 2
        }

        when (MeasureSpec.getMode(widthMeasureSpec)) {
            MeasureSpec.EXACTLY -> {
                width = getDefaultSize(suggestedMinimumWidth, widthMeasureSpec)
            }
            MeasureSpec.AT_MOST -> {
                width = max(getDefaultSize(suggestedMinimumWidth, widthMeasureSpec), canvasWidth)
            }
            MeasureSpec.UNSPECIFIED -> {
                width = canvasWidth
            }
        }

        setMeasuredDimension(width, height)
    }

    override fun onDragEvent(event: DragEvent?): Boolean {
        return super.onDragEvent(event)
    }

    fun setSentanceMarkers(sentenceArray: Array<Sentence>) {
        sentences = sentenceArray
        invalidate()
    }

    fun setLoadingState(loading: Boolean) {
        if (loading != loadingWFM) {
            Log.d("VIEW", "Loading called: $loading")
            loadingWFM = loading
            if (loadingWFM) {
                setWaveformColor(wfmColorAlt)
            } else {
                setWaveformColor(wfmColor)
            }
            invalidate()
        }

    }

}
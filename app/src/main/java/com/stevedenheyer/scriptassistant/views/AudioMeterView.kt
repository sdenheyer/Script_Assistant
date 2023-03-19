package com.stevedenheyer.scriptassistant.views

import android.content.Context
import android.graphics.drawable.LayerDrawable
import android.util.AttributeSet
import com.stevedenheyer.scriptassistant.R

private const val MAX_LEVEL = 10000

class AudioMeterView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : androidx.appcompat.widget.AppCompatSeekBar(context, attrs) {

    override fun setSecondaryProgress(secondaryProgress: Int) {
        val scale:Float = if (max > 0) {secondaryProgress.toFloat() / max
        } else {0F}
        var d = progressDrawable
        if (d is LayerDrawable) {
            d = d.findDrawableByLayerId((R.id.exceededProgress))
            if (d == null) {
                super.setSecondaryProgress(secondaryProgress)
                return
            } else {
                d.level = 0
            }
        }
        if (secondaryProgress > progress) {
            if (d != null) {
                val level:Int = (scale * MAX_LEVEL).toInt()
                d.level = level
            } else {
                invalidate()
            }
            super.setSecondaryProgress(progress)
        } else {
            super.setSecondaryProgress(secondaryProgress)
        }
    }
}
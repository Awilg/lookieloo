package com.lookieloo.utils

import android.view.View
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import java.lang.Math.round

object Utils {
    fun View.dpToPx(dp: Float) = round(dp * context.resources.displayMetrics.density)
    fun View.getColor(@ColorRes color: Int) = ContextCompat.getColor(context, color)
}
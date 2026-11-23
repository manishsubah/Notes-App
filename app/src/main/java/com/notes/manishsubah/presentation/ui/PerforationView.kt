package com.notes.manishsubah.presentation.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.notes.manishsubah.R

/**
 * The tear line between the pass and its stub: a dashed rule with a semicircular notch
 * bitten out of the top and bottom edges. Each notch is painted in the colour of whatever
 * sits behind the pass at that edge (the band above, the ground below).
 */
class PerforationView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
) : View(context, attrs) {

    private val density = resources.displayMetrics.density

    private val dashPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 1.5f * density
        color = ContextCompat.getColor(context, R.color.perforation)
        pathEffect = DashPathEffect(floatArrayOf(5f * density, 4f * density), 0f)
    }
    private val topNotchPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.band)
    }
    private val bottomNotchPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.ground)
    }

    init {
        importantForAccessibility = IMPORTANT_FOR_ACCESSIBILITY_NO
    }

    override fun onDraw(canvas: Canvas) {
        val cx = width / 2f
        val r = width / 2f
        canvas.drawLine(cx, r + 4 * density, cx, height - r - 4 * density, dashPaint)
        canvas.drawCircle(cx, 0f, r, topNotchPaint)
        canvas.drawCircle(cx, height.toFloat(), r, bottomNotchPaint)
    }
}

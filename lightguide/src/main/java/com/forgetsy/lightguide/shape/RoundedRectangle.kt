package com.forgetsy.lightguide.shape

import android.animation.TimeInterpolator
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import com.forgetsy.lightguide.shape.Shape.Companion.DEFAULT_DURATION
import com.forgetsy.lightguide.shape.Shape.Companion.DEFAULT_INTERPOLATOR

/**
 * [Shape] of RoundedRectangle with customizable height, width, and radius.
 */
class RoundedRectangle @JvmOverloads constructor(
    override val margin: Float = 10f,
    private val radius: Float = 20f,
    override val duration: Long = DEFAULT_DURATION,
    override val interpolator: TimeInterpolator = DEFAULT_INTERPOLATOR
) : Shape {
  private var rectF : RectF? = null
  var centerX: Float = 0f
  var centerY: Float = 0f
  private var height: Float = 0f
  private var width: Float = 0f

  override fun setAnchorRect(anchor: Rect) {
    width = anchor.width().toFloat() + margin
    height = anchor.height().toFloat() + margin
    centerX = anchor.centerX().toFloat()
    centerY = anchor.centerY().toFloat()
    rectF = RectF(centerX- width * 0.5f, centerY - height * 0.5f, centerX + width * 0.5f, centerY + height * 0.5f)
  }

  override fun draw(canvas: Canvas, value: Float, paint: Paint) {
    val halfWidth = width / 2 * value
    val halfHeight = height / 2 * value
    val left = centerX - halfWidth
    val top = centerY - halfHeight
    val right = centerX + halfWidth
    val bottom = centerY + halfHeight
    val rect = RectF(left, top, right, bottom)
    canvas.drawRoundRect(rect, radius, radius, paint)
  }

  override fun getRectF(): RectF {
    return rectF?: RectF()
  }

}


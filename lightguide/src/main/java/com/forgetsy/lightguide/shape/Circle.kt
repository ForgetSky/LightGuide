package com.forgetsy.lightguide.shape

import android.animation.TimeInterpolator
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import com.forgetsy.lightguide.shape.Shape.Companion.DEFAULT_DURATION
import com.forgetsy.lightguide.shape.Shape.Companion.DEFAULT_INTERPOLATOR
import kotlin.math.sqrt

/**
 * [Shape] of Circle with customizable radius.
 */
class Circle @JvmOverloads constructor(
    override val margin: Float = 20f,
    override val duration: Long = DEFAULT_DURATION,
    override val interpolator: TimeInterpolator = DEFAULT_INTERPOLATOR
) : Shape {
  private var radius: Float = 0f
  private var rectF : RectF? = null
  var centerX: Float = 0f
  var centerY: Float = 0f

  override fun setAnchorRect(anchor: Rect) {
    radius = sqrt((anchor.width() * anchor.width() + anchor.height() * anchor.height()).toDouble())
        .toFloat() * 0.5f + margin
    centerX = anchor.centerX().toFloat()
    centerY = anchor.centerY().toFloat()
    rectF = RectF(centerX- radius, centerY - radius, centerX + radius, centerY + radius)
  }

  override fun draw(canvas: Canvas, value: Float, paint: Paint) {
    canvas.drawCircle(centerX, centerY, value * radius, paint)
  }

  override fun getRectF(): RectF {
    return rectF?: RectF()
  }
}

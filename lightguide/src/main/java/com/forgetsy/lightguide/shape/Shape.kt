package com.forgetsy.lightguide.shape

import android.animation.TimeInterpolator
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.view.animation.DecelerateInterpolator
import com.forgetsy.lightguide.Target
import java.util.concurrent.TimeUnit

/**
 * Shape of a [Target] that would be drawn by LightGuide View.
 * For any shape of target, this Shape class need to be implemented.
 */
interface Shape {

  val margin: Float

  /**
   * [duration] to draw Shape.
   */
  val duration: Long

  /**
   * [interpolator] to draw Shape.
   */
  val interpolator: TimeInterpolator

  /**
   * Draws the Shape.
   *
   * @param value the animated value from 0 to 1.
   */
  fun draw(canvas: Canvas, value: Float, paint: Paint)

  fun setAnchorRect(anchor : Rect)

  fun getRectF() : RectF

  companion object {

    val DEFAULT_DURATION = TimeUnit.MILLISECONDS.toMillis(300)

    val DEFAULT_INTERPOLATOR = DecelerateInterpolator(1.5f)
  }
}

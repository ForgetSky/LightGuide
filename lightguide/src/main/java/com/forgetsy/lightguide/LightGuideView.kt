package com.forgetsy.lightguide

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.animation.TimeInterpolator
import android.animation.ValueAnimator
import android.animation.ValueAnimator.AnimatorUpdateListener
import android.animation.ValueAnimator.ofFloat
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Point
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.RectF
import android.util.AttributeSet
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.ColorInt
import kotlin.math.abs
import kotlin.math.max

/**
 * [LightGuideView] starts/finishes [LightGuide], and starts/finishes a current [Target].
 */
internal class LightGuideView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    @ColorInt backgroundColor: Int
) : FrameLayout(context, attrs, defStyleAttr) {

  private val backgroundPaint by lazy {
    Paint().apply { color = backgroundColor }
  }

  private val shapePaint by lazy {
    Paint().apply { xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR) }
  }

  private val invalidator = AnimatorUpdateListener { invalidate() }

  private var shapeAnimator: ValueAnimator? = null
  private var target: Target? = null
  private var downX = 0f
  private var downY = 0f
  private var touchSlop = 0

  init {
    setWillNotDraw(false)
    setLayerType(View.LAYER_TYPE_HARDWARE, null)
    touchSlop = ViewConfiguration.get(context).scaledTouchSlop
  }

  override fun onDraw(canvas: Canvas) {
    super.onDraw(canvas)
    canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), backgroundPaint)
    val currentTarget = target
    val currentShapeAnimator = shapeAnimator

    if (currentTarget != null && currentShapeAnimator != null) {
      currentTarget.shape.draw(
          canvas = canvas,
          value = currentShapeAnimator.animatedValue as Float,
          paint = shapePaint
      )
    }
  }

  /**
   * Starts [LightGuide].
   */
  fun startLightGuide(
      duration: Long,
      interpolator: TimeInterpolator,
      listener: Animator.AnimatorListener
  ) {
    val objectAnimator = ObjectAnimator.ofFloat(this, "alpha", 0f, 1f).apply {
      setDuration(duration)
      setInterpolator(interpolator)
      addListener(listener)
    }
    objectAnimator.start()
  }

  /**
   * Finishes [LightGuide].
   */
  fun finishLightGuide(
      duration: Long,
      interpolator: TimeInterpolator,
      listener: Animator.AnimatorListener
  ) {
    val objectAnimator = ObjectAnimator.ofFloat(this, "alpha", 1f, 0f).apply {
      setDuration(duration)
      setInterpolator(interpolator)
      addListener(listener)
    }
    objectAnimator.start()
  }

  /**
   * Starts the provided [Target].
   */
  fun startTarget(target: Target, listener: Animator.AnimatorListener) {
    removeAllViews()
    this.target = target.apply {
      // adjust anchor in case where custom container is set.
      val location = IntArray(2)
      getLocationInWindow(location)
      val offset = Point(location[0], location[1])
      if (offset.x != 0 || offset.y != 0) {
        anchor.offset(-offset.x, -offset.y)
        target.shape.setAnchorRect(anchor)
      }
    }
    this.shapeAnimator?.removeAllListeners()
    this.shapeAnimator?.removeAllUpdateListeners()
    this.shapeAnimator?.cancel()
    this.shapeAnimator = ofFloat(0f, 1f).apply {
      duration = target.shape.duration
      interpolator = target.shape.interpolator
      addUpdateListener(invalidator)
      addListener(listener)
      addListener(object : AnimatorListenerAdapter() {
        override fun onAnimationEnd(animation: Animator) {
          removeAllListeners()
          removeAllUpdateListeners()
        }

        override fun onAnimationCancel(animation: Animator) {
          removeAllListeners()
          removeAllUpdateListeners()
        }
      })
    }
    shapeAnimator?.start()
    target.overlay?.let {
      addOverlayView(it)
    }
  }

  private fun addOverlayView(view: View) {
    if (true == target?.autoPosition) {
      addViewAuto(view)
    } else {
      addView(view)
    }
  }

  private fun addViewAuto(view: View) {
    val lp = FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
        ViewGroup.LayoutParams.WRAP_CONTENT)
    addView(view, lp)
  }

  /**
   * Finishes the current [Target].
   */
  fun finishTarget(listener: Animator.AnimatorListener) {
    val currentTarget = target ?: return
    val currentAnimatedValue = shapeAnimator?.animatedValue ?: return
    shapeAnimator?.removeAllListeners()
    shapeAnimator?.removeAllUpdateListeners()
    shapeAnimator?.cancel()
    shapeAnimator = ofFloat(currentAnimatedValue as Float, 0f).apply {
      duration = currentTarget.shape.duration
      interpolator = currentTarget.shape.interpolator
      addUpdateListener(invalidator)
      addListener(listener)
      addListener(object : AnimatorListenerAdapter() {
        override fun onAnimationEnd(animation: Animator) {
          removeAllListeners()
          removeAllUpdateListeners()
        }

        override fun onAnimationCancel(animation: Animator) {
          removeAllListeners()
          removeAllUpdateListeners()
        }
      })
    }
    shapeAnimator?.start()
  }

  fun cleanup() {
    shapeAnimator?.removeAllListeners()
    shapeAnimator?.removeAllUpdateListeners()
    shapeAnimator?.cancel()
    shapeAnimator = null
    removeAllViews()
  }

  override fun onTouchEvent(event: MotionEvent?): Boolean {
    when (event!!.action) {
      MotionEvent.ACTION_DOWN -> {
        downX = event.x
        downY = event.y
      }
      MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
        val upX = event.x
        val upY = event.y
        target?.let {
          if (abs(upX - downX) < touchSlop && abs(
                  upY - downY) < touchSlop) {
            val rectF: RectF = it.shape.getRectF()
            if (rectF.contains(upX, upY)) {
              it.listener?.onClick()
              return true
            }
            performClick()
          }
        }
      }
    }
    return super.onTouchEvent(event)
  }

  override fun onLayout(
      changed: Boolean, parentLeft: Int, parentTop: Int, parentRight: Int, parentBottom: Int
  ) {
    if (true == target?.autoPosition) {
      val child = getChildAt(0)
      if (child.visibility != View.GONE) {
        var gravity: Int

        val width = child.measuredWidth
        val height = child.measuredHeight
        target?.let {
          val highLight = target!!.shape.getRectF()

          val horizontalInterval = highLight.height() * 0.5f
          val verticalInterval = highLight.height() * 0.5f
          val bottomSpare = parentBottom - highLight.bottom - verticalInterval
          val topSpare = highLight.top - verticalInterval
          val startSpare = highLight.left - horizontalInterval
          val endSpare = parentRight - highLight.right - horizontalInterval
          if (bottomSpare >= height) {
            gravity = Gravity.BOTTOM
          } else if (topSpare >= height) {
            gravity = Gravity.TOP
          } else if (endSpare >= width) {
            gravity = Gravity.END
          } else if (startSpare >= width) {
            gravity = Gravity.START
          } else if (max(bottomSpare, topSpare) > max(startSpare, endSpare)) {
            gravity = if (bottomSpare >= topSpare) {
              Gravity.BOTTOM
            } else {
              Gravity.TOP
            }
          } else {
            gravity = if (endSpare >= startSpare) {
              Gravity.END
            } else {
              Gravity.START
            }
          }

          val childLeft : Int = when (gravity) {
            Gravity.END -> (highLight.right + (parentRight - highLight.right - width) / 2).toInt()
            Gravity.START -> (parentLeft + (highLight.left - parentLeft - width) / 2).toInt()
            else -> (parentLeft + (parentRight - parentLeft - width) / 2)
          }

          val childTop = when (gravity) {
            Gravity.TOP -> (parentTop + (highLight.top - parentTop - height) / 2).toInt()
            Gravity.BOTTOM -> (highLight.bottom + (parentBottom - highLight.bottom - height) / 2).toInt()
            else -> parentTop + (parentBottom - parentTop - height) / 2
          }
          child.layout(childLeft, childTop, childLeft + width, childTop + height)
        }
      }
    } else {
      super.onLayout(changed, parentLeft, parentTop, parentRight, parentBottom)
    }
  }
}

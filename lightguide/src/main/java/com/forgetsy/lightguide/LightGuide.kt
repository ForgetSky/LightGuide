package com.forgetsy.lightguide

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.TimeInterpolator
import android.app.Activity
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.animation.DecelerateInterpolator
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat

/**
 * Holds all of the [Target]s and [LightGuideView] to show/hide [Target], [LightGuideView] properly.
 * [LightGuideView] can be controlled with [start]/[finish].
 * All of the [Target]s can be controlled with [next]/[previous]/[show].
 *
 * Once you finish the current [LightGuide] with [finish], you can not start the [LightGuide] again
 * unless you create a new [LightGuide] to start again.
 */
class LightGuide private constructor(
    private val lightGuideView: LightGuideView,
    private val targets: Array<Target>,
    private val duration: Long,
    private val interpolator: TimeInterpolator,
    private val container: ViewGroup,
    private val lightGuideListener: OnLightGuideListener?
) {

  private var currentIndex = NO_POSITION

  init {
    container.addView(lightGuideView, MATCH_PARENT, MATCH_PARENT)
  }

  /**
   * Starts [LightGuideView] and show the first [Target].
   */
  fun start() {
    startLightGuide()
  }

  /**
   * Closes the current [Target] if exists, and shows a [Target] at the specified [index].
   * If target is not found at the [index], it will throw an exception.
   */
  fun show(index: Int) {
    showTarget(index)
  }

  /**
   * Closes the current [Target] if exists, and shows the next [Target].
   * If the next [Target] is not found, LightGuide will finish.
   */
  fun next() {
    showTarget(currentIndex + 1)
  }

  /**
   * Closes the current [Target] if exists, and shows the previous [Target].
   * If the previous target is not found, it will throw an exception.
   */
  fun previous() {
    showTarget(currentIndex - 1)
  }

  /**
   * Closes LightGuide and [LightGuideView] will remove all children and be removed from the [container].
   */
  fun finish() {
    finishLightGuide()
  }

  /**
   * Starts LightGuide.
   */
  private fun startLightGuide() {
    lightGuideView.startLightGuide(duration, interpolator, object : AnimatorListenerAdapter() {
      override fun onAnimationStart(animation: Animator) {
        lightGuideListener?.onStarted()
      }

      override fun onAnimationEnd(animation: Animator) {
        showTarget(0)
      }
    })
    lightGuideView.setOnClickListener { lightGuideListener?.onClick() }
  }

  /**
   * Closes the current [Target] if exists, and show the [Target] at [index].
   */
  private fun showTarget(index: Int) {
    if (currentIndex == NO_POSITION) {
      val target = targets[index]
      currentIndex = index
      lightGuideView.startTarget(target, object : AnimatorListenerAdapter() {
        override fun onAnimationStart(animation: Animator) {
          target.listener?.onStarted()
        }
      })
    } else {
      lightGuideView.finishTarget(object : AnimatorListenerAdapter() {
        override fun onAnimationEnd(animation: Animator) {
          val previousIndex = currentIndex
          val previousTarget = targets[previousIndex]
          previousTarget.listener?.onEnded()
          if (index < targets.size) {
            val target = targets[index]
            currentIndex = index
            lightGuideView.startTarget(target, object : AnimatorListenerAdapter() {
              override fun onAnimationStart(animation: Animator) {
                target.listener?.onStarted()
              }
            })
          } else {
            finishLightGuide()
          }
        }
      })
    }
  }

  /**
   * Closes LightGuide.
   */
  private fun finishLightGuide() {
    lightGuideView.finishLightGuide(duration, interpolator, object : AnimatorListenerAdapter() {
      override fun onAnimationEnd(animation: Animator) {
        lightGuideView.cleanup()
        container.removeView(lightGuideView)
        lightGuideListener?.onEnded()
      }
    })
  }

  companion object {

    private const val NO_POSITION = -1
  }

  /**
   * Builder to build [LightGuide].
   * All parameters should be set in this [Builder].
   */
  class Builder(private val activity: Activity) {

    private var targets: Array<Target>? = null
    private var duration: Long = 100L
    private var interpolator: TimeInterpolator = DecelerateInterpolator(1.5f)
    @ColorInt private var backgroundColor: Int = 0x6000000
    private var container: ViewGroup? = null
    private var listener: OnLightGuideListener? = null

    /**
     * Sets [Target]s to show on [LightGuide].
     */
    fun setTargets(vararg targets: Target): Builder = apply {
      require(targets.isNotEmpty()) { "targets should not be empty. " }
      this.targets = arrayOf(*targets)
    }

    /**
     * Sets [Target]s to show on [LightGuide].
     */
    fun setTargets(targets: List<Target>): Builder = apply {
      require(targets.isNotEmpty()) { "targets should not be empty. " }
      this.targets = targets.toTypedArray()
    }

    /**
     * Sets [duration] to start/finish [LightGuide].
     */
    fun setDuration(duration: Long): Builder = apply {
      this.duration = duration
    }

    /**
     * Sets [backgroundColor] resource on [LightGuide].
     */
    fun setBackgroundColorRes(@ColorRes backgroundColorRes: Int): Builder = apply {
      this.backgroundColor = ContextCompat.getColor(activity, backgroundColorRes)
    }

    /**
     * Sets [backgroundColor] on [LightGuide].
     */
    fun setBackgroundColor(@ColorInt backgroundColor: Int): Builder = apply {
      this.backgroundColor = backgroundColor
    }

    /**
     * Sets [interpolator] to start/finish [LightGuide].
     */
    fun setAnimation(interpolator: TimeInterpolator): Builder = apply {
      this.interpolator = interpolator
    }

    /**
     * Sets [container] to hold [LightGuideView]. DecoderView will be used if not specified.
     */
    fun setContainer(container: ViewGroup) = apply {
      this.container = container
    }

    /**
     * Sets [OnLightGuideListener] to notify the state of [LightGuide].
     */
    fun setOnLightGuideListener(listener: OnLightGuideListener): Builder = apply {
      this.listener = listener
    }

    fun build(): LightGuide {

      val lightGuideView = LightGuideView(activity, null, 0, backgroundColor)
      val targets = requireNotNull(targets) { "targets should not be null. " }
      val container = container ?: activity.window.decorView as ViewGroup

      return LightGuide(
          lightGuideView = lightGuideView,
          targets = targets,
          duration = duration,
          interpolator = interpolator,
          container = container,
          lightGuideListener = listener
      )
    }
  }
}

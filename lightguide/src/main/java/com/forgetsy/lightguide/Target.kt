package com.forgetsy.lightguide

import android.graphics.Rect
import android.view.View
import com.forgetsy.lightguide.shape.Circle
import com.forgetsy.lightguide.shape.Shape

/**
 * Target represents the highLight that LightGuide will cast.
 */
class Target(
    var anchorView: View?,
    val anchor: Rect?,
    val shape: Shape,
    val overlay: View?,
    val autoPosition: Boolean,
    val listener: OnTargetListener?
) {

  /**
   * [Builder] to build a [Target].
   * All parameters should be set in this [Builder].
   */
  class Builder {
    private var anchorView: View? = null
    private var anchor: Rect? = null
    private var shape: Shape = DEFAULT_SHAPE
    private var overlay: View? = null
    private var autoPosition: Boolean = true
    private var listener: OnTargetListener? = null

    constructor()
    constructor(target: Target) {
      if (target.anchorView != null) {
        this.anchorView = target.anchorView
      } else {
        this.anchor = target.anchor
      }
      this.shape = target.shape
      this.overlay = target.overlay
      this.autoPosition = target.autoPosition
      this.listener = target.listener
    }
    /**
     * Sets a pointer to start a [Target].
     */
    fun setAnchor(view: View): Builder = apply {
      this.anchorView = view
    }

    /**
     * Sets an anchor point to start [Target].
     */
    fun setAnchor(rect: Rect): Builder = apply {
      this.anchor = rect
    }

    /**
     * Sets [shape] of the highLight of [Target].
     */
    fun setShape(shape: Shape): Builder = apply {
      this.shape = shape
    }

    /**
     * Sets [overlay] to be laid out to describe [Target].
     */
    fun setOverlay(overlay: View): Builder = apply {
      this.overlay = overlay
    }

    /**
     * Sets [autoPosition] to be laid out to describe [Target].
     */
    fun autoPosition(autoPosition: Boolean): Builder = apply {
      this.autoPosition = autoPosition
    }

    /**
     * Sets [OnTargetListener] to notify the state of [Target].
     */
    fun setOnTargetListener(listener: OnTargetListener?): Builder = apply {
      this.listener = listener
    }

    fun build() : Target {
      val location = IntArray(2)
      anchorView?.let {
        it.getLocationInWindow(location)
        val x = location[0]
        val y = location[1]
        this.anchor = Rect(x, y, x + it.width, y + it.height)
      }

      anchor?.let {
        shape.setAnchorRect(it)
      }
      return Target(
          anchorView = anchorView,
          anchor = anchor,
          shape = shape,
          overlay = overlay,
          autoPosition = autoPosition,
          listener = listener
      )
    }

    companion object {
      private val DEFAULT_SHAPE = Circle()
    }
  }
}
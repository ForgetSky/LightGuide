package com.forgetsy.lightguide

/**
 * Listener to notify the state of LightGuide.
 */
interface OnLightGuideListener {

  /**
   * Called when LightGuide is started
   */
  fun onStarted(){}

  /**
   * Called when LightGuide is ended
   */
  fun onEnded(){}

  /**
   * Called when LightGuide is clicked, except for the highlight area and the overlay area
   */
  fun onClick(){}
}

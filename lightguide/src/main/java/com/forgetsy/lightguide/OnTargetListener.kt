package com.forgetsy.lightguide

/**
 * Listener to notify the state of Target.
 */
interface OnTargetListener {

  /**
   * Called when Target is started
   */
  fun onStarted(){}

  /**
   * Called when Target is started
   */
  fun onEnded(){}

  /**
   * Called when Target is clicked
   */
  fun onClick(){}
}

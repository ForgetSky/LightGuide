package com.forgetsy.lightguidesample

import android.os.Bundle
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import android.widget.Toast.makeText
import androidx.appcompat.app.AppCompatActivity
import com.forgetsy.lightguide.LightGuide
import com.forgetsy.lightguide.OnLightGuideListener
import com.forgetsy.lightguide.OnTargetListener
import com.forgetsy.lightguide.Target
import com.forgetsy.lightguide.shape.Circle
import com.forgetsy.lightguide.shape.RoundedRectangle

class SampleActivity : AppCompatActivity(R.layout.activity_activity_sample) {

  private var currentToast: Toast? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    findViewById<View>(R.id.start).setOnClickListener { startButton ->
      val targets = ArrayList<Target>()

      // first target
      val first = layoutInflater.inflate(R.layout.layout_target, null)
      val firstView = findViewById<View>(R.id.one)
      val firstTarget = Target.Builder()
          .setAnchor(firstView)
          .setShape(Circle())
          .setOverlay(first)
          .autoPosition(true)
          .setOnTargetListener(object : OnTargetListener {
            override fun onStarted() {
              currentToast?.cancel()
              currentToast = makeText(
                  this@SampleActivity,
                  "first target is started",
                  LENGTH_SHORT
              )
              currentToast?.show()
            }

            override fun onEnded() {
              currentToast?.cancel()
              currentToast = makeText(
                  this@SampleActivity,
                  "first target is ended",
                  LENGTH_SHORT
              )
              currentToast?.show()
            }

            override fun onClick() {
              currentToast?.cancel()
              currentToast = makeText(
                  this@SampleActivity,
                  "first target is onClick",
                  LENGTH_SHORT
              )
              currentToast?.show()
            }
          })
          .build()

      targets.add(firstTarget)

      // second target
      val second = layoutInflater.inflate(R.layout.layout_target, null)
      val secondTarget = Target.Builder()
          .setAnchor(findViewById<View>(R.id.two))
          .setShape(Circle(50f))
          .setOverlay(second)
          .setOnTargetListener(object : OnTargetListener {
            override fun onStarted() {
              currentToast?.cancel()
              currentToast = makeText(
                  this@SampleActivity,
                  "second target is started",
                  LENGTH_SHORT
              )
              currentToast?.show()
            }

            override fun onEnded() {
              currentToast?.cancel()
              currentToast = makeText(
                  this@SampleActivity,
                  "second target is ended",
                  LENGTH_SHORT
              )
              currentToast?.show()
            }

            override fun onClick() {
              currentToast?.cancel()
              currentToast = makeText(
                  this@SampleActivity,
                  "second target is onClick",
                  LENGTH_SHORT
              )
              currentToast?.show()
            }
          })
          .build()

      targets.add(secondTarget)

      // third target
      val third = layoutInflater.inflate(R.layout.layout_target, null)
      val anchorView = findViewById<View>(R.id.three)
      val thirdTarget = Target.Builder()
          .setAnchor(anchorView)
          .setShape(RoundedRectangle())
          .setOverlay(third)
          .setOnTargetListener(object : OnTargetListener {
            override fun onStarted() {
              currentToast?.cancel()
              currentToast = makeText(
                  this@SampleActivity,
                  "third target is started",
                  LENGTH_SHORT
              )
              currentToast?.show()
            }

            override fun onEnded() {
              currentToast?.cancel()
              currentToast = makeText(
                  this@SampleActivity,
                  "third target is ended",
                  LENGTH_SHORT
              )
              currentToast?.show()
            }

            override fun onClick() {
              currentToast?.cancel()
              currentToast = makeText(
                  this@SampleActivity,
                  "third target is onClick",
                  LENGTH_SHORT
              )
              currentToast?.show()
            }
          })
          .build()

      targets.add(thirdTarget)

      // create lightguide
      val lightGuide = LightGuide.Builder(this@SampleActivity)
          .setTargets(targets)
          .setBackgroundColorRes(R.color.lightGuideBackground)
          .setDuration(100L)
          .setAnimation(DecelerateInterpolator(1.2f))
          .setOnLightGuideListener(object : OnLightGuideListener {
            override fun onStarted() {
              currentToast?.cancel()
              currentToast = makeText(
                  this@SampleActivity,
                  "lightguide is started",
                  LENGTH_SHORT
              )
              currentToast?.show()
              startButton.isEnabled = false
            }

            override fun onEnded() {
              currentToast?.cancel()
              currentToast = makeText(
                  this@SampleActivity,
                  "lightguide is ended",
                  LENGTH_SHORT
              )
              currentToast?.show()
              startButton.isEnabled = true
            }

            override fun onClick() {
              currentToast?.cancel()
              currentToast = makeText(
                  this@SampleActivity,
                  "lightguide is onClick",
                  LENGTH_SHORT
              )
              currentToast?.show()
              startButton.isEnabled = true
            }
          })
          .build()

      lightGuide.start()

      val nextTarget = View.OnClickListener { lightGuide.next() }

      first.findViewById<View>(R.id.next_target).setOnClickListener(nextTarget)
      second.findViewById<View>(R.id.next_target).setOnClickListener(nextTarget)
      third.findViewById<View>(R.id.next_target).setOnClickListener(nextTarget)
    }

  }
}

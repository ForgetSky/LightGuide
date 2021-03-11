# LightGuide

An Android library for highlight guide

## Gradle

```groovy
dependencies {
    implementation 'com.forgetsky:lightguide:1.0.0'
}
```


## Usage

```kt
val lightguide = LightGuide.Builder(this)
    .setTargets(firstTarget, secondTarget, thirdTarget ...)
    .setBackgroundColor(R.color.lightguideBackground)
    .setDuration(1000L)
    .setAnimation(DecelerateInterpolator(2f))
    .setContainer(viewGroup)
    .setOnLightGuideListener(object : OnLightGuideListener {
      override fun onStarted() {
        Toast.makeText(this@MainActivity, "lightguide is started", Toast.LENGTH_SHORT).show()
      }
      override fun onEnded() {
        Toast.makeText(this@MainActivity, "lightguide is ended", Toast.LENGTH_SHORT).show()
      }
      override fun onClick() {
        Toast.makeText(this@MainActivity, "lightguide is click", Toast.LENGTH_SHORT).show()
      }
    })
    .build()         
```

If you want to show LightGuide immediately, you have to wait until views are laid out.

```kt
// with core-ktx method.
view.doOnPreDraw { LightGuide.Builder(this)...start() }
```


## Target
Create a Target to add LightGuide.

Target is a highLight to be casted by LightGuide. You can add multiple targets to LightGuide.

```kt
val target = Target.Builder()
    .setAnchor(100f, 100f)
    .setShape(Circle(100f))
    .setOverlay(layout)
    .setOnTargetListener(object : OnTargetListener {
      override fun onStarted() {
        makeText(this@MainActivity, "first target is started", LENGTH_SHORT).show()
      }
      override fun onEnded() {
        makeText(this@MainActivity, "first target is ended", LENGTH_SHORT).show()
      }
      override fun onClick() {
        makeText(this@MainActivity, "first target is onClick", LENGTH_SHORT).show()
      }
    })
    .build()
```
The display position of the overlay view can be adjusted automatically according to the highlighted area,


## Start/Finish LightGuide

```kt
val lightguide = LightGuide.Builder(this)...start()

lightguide.finish()
```

## Next/Previous/Show Target

```kt
val lightguide = LightGuide.Builder(this)...start()

lightguide.next()

lightguide.previous()

lightguide.show(2)
```

## Custom Shape
`Shape` defines how your target will look like.
[Circle](https://github.com/ForgetSky/LightGuide/blob/main/lightguide/src/main/java/com/forgetsky/lightguide/shape/Circle.kt) and [RoundedRectangle](https://github.com/TakuSemba/LightGuide/blob/master/lightguide/src/main/java/com/takusemba/lightguide/shape/RoundedRectangle.kt) shapes are already implemented, but if you want your custom shape, it's arhivable by implementing `Shape` interface.


```kt
override val margin: Float = 20f,
    override val duration: Long = DEFAULT_DURATION,
    override val interpolator: TimeInterpolator = DEFAULT_INTERPOLATOR
) : Shape {

  override fun draw(canvas: Canvas, value: Float, paint: Paint) {
    // draw your shape here.
  }
}
```

## Sample
Clone this repo and check out the [app](https://github.com/ForgetSky/LightGuide/tree/main/app) module.

## Licence
```
Copyright 2017 Taku Semba.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

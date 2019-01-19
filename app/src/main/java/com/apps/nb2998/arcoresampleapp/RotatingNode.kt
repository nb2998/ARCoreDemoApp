package com.apps.nb2998.arcoresampleapp

import android.animation.ObjectAnimator
import android.annotation.TargetApi
import android.os.Build
import android.support.annotation.RequiresApi
import android.view.MotionEvent
import android.view.animation.LinearInterpolator
import com.google.ar.sceneform.FrameTime
import com.google.ar.sceneform.HitTestResult
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.math.Quaternion
import com.google.ar.sceneform.math.QuaternionEvaluator
import com.google.ar.sceneform.math.Vector3

class RotatingNode : Node(), Node.OnTapListener {

    private var rotationAnimation: ObjectAnimator? = null
    private var degreesPerSec = 90.0f
    private var lastSpeedMultiplier = 1.0f
    private var speedMultiplier = 1.0f

    private val animationDuration = (1000 * 360/ degreesPerSec*speedMultiplier).toLong()

    @TargetApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onUpdate(p0: FrameTime?) {
        super.onUpdate(p0)

        if(rotationAnimation == null) return
        speedMultiplier = speedMultiplier

        if(lastSpeedMultiplier == speedMultiplier) return

        if(speedMultiplier == 0.0f) rotationAnimation!!.pause()
        else {
            rotationAnimation!!.resume()
            val animatedFraction = rotationAnimation!!.animatedFraction
            rotationAnimation!!.duration = animationDuration
            rotationAnimation!!.setCurrentFraction(animatedFraction)
        }
        lastSpeedMultiplier = speedMultiplier
    }

    override fun onActivate() {
        startAnimation()
    }

    private fun startAnimation() {
        if(rotationAnimation != null) return
        rotationAnimation = createAnimator()
        rotationAnimation!!.target = this
        rotationAnimation!!.duration = animationDuration
        rotationAnimation!!.start()
    }

    private fun createAnimator(): ObjectAnimator {
        val rotationAnimator = ObjectAnimator()

        val orientation1 = Quaternion.axisAngle(Vector3(0.0f, 1.0f, 0.0f), 0f)
        val orientation2 = Quaternion.axisAngle(Vector3(0.0f, 1.0f, 0.0f), 120f)
        val orientation3 = Quaternion.axisAngle(Vector3(0.0f, 1.0f, 0.0f), 240f)
        val orientation4 = Quaternion.axisAngle(Vector3(0.0f, 1.0f, 0.0f), 360f)

        rotationAnimator.setObjectValues(orientation1, orientation2, orientation3, orientation4)
        rotationAnimator.propertyName = "localRotation"
        rotationAnimator.setEvaluator(QuaternionEvaluator())
        rotationAnimator.repeatCount = ObjectAnimator.INFINITE
        rotationAnimator.repeatMode = ObjectAnimator.RESTART
        rotationAnimator.interpolator = LinearInterpolator()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2)
            rotationAnimator.setAutoCancel(true)

        return rotationAnimator
    }

    override fun onDeactivate() {
        stopAnimation()
    }

    private fun stopAnimation() {
        if(rotationAnimation == null) return
        rotationAnimation!!.cancel()
        rotationAnimation = null
    }

    fun setSpeedOfRotation(degreesPerSec: Float) {
        this.degreesPerSec = degreesPerSec
    }

    override fun onTap(p0: HitTestResult?, p1: MotionEvent?) {

    }
}
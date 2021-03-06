package com.apppoweron.circularrevealbutton.container

import android.animation.Animator
import android.content.Context
import android.os.Build
import android.support.annotation.RequiresApi
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewAnimationUtils
import android.widget.FrameLayout
import com.apppoweron.circularrevealbutton.R
import com.apppoweron.circularrevealbutton.util.DebugUtil

class CircularRevealContainerImpl : FrameLayout, CircularRevealContainer {

    companion object {
        private const val TAG = "CircularRevealContainer"
        private const val ANIM_DURATION = 2000
    }

    private var isAnimInProgress: Boolean = false

    private val animationMask: View
        get() = findViewById(R.id.button_circular_reveal_mask)

    constructor(context: Context) : super(context) {
        init(context, null)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context, attrs)
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        init(context, attrs)
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        View.inflate(context, R.layout.circular_reveal_container_view, this)
    }

    private fun setAnimationColor(color: Int?) {
        if (color != null) {
            animationMask.setBackgroundColor(color)
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    override fun startCircularRevealAnimation(data: ButtonCRevealAnimationData, listener: ((viewId: Int) -> Unit)?) {
        //Assemble animator
        val animator = assembleAnimator(data, listener)

        //Set the color of the animation
        setAnimationColor(data.animColor)

        //Disabling touch events when it does the animation
        isAnimInProgress = true

        animationMask.bringToFront()
        animationMask.visibility = View.VISIBLE

        //Start the animation!
        animator.start()
    }

    override fun removeAnimationMask() {
        isAnimInProgress = false
        animationMask.visibility = View.GONE
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private fun assembleAnimator(data: ButtonCRevealAnimationData, listener: ((viewId: Int) -> Unit)?): Animator {

        val endRadius = Math.hypot(width.toDouble(), height.toDouble()).toInt()
        val anim = ViewAnimationUtils.createCircularReveal(animationMask, data.x, data.y, data.animStartRadius.toFloat(), endRadius.toFloat())


        if (data.animDuration != null) {
            anim.duration = data.animDuration.toLong()
        } else {
            anim.duration = ANIM_DURATION.toLong()
        }

        anim.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animator: Animator) {

            }

            override fun onAnimationEnd(animator: Animator) {
                DebugUtil.debug {
                    Log.d(TAG, "onAnimationEnd AnimatedLoadingButton")
                }
                isAnimInProgress = false
                listener?.invoke(id)
            }

            override fun onAnimationCancel(animator: Animator) {
                isAnimInProgress = false
            }

            override fun onAnimationRepeat(animator: Animator) {

            }
        })
        return anim
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        return if (!isAnimInProgress) {
            super.dispatchTouchEvent(ev)
        } else true
    }

}

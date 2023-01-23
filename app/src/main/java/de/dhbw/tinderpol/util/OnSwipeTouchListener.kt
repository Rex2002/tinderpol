package de.dhbw.tinderpol.util

import android.content.Context
import android.util.Log
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import kotlin.math.abs

internal open class OnSwipeTouchListener (c: Context?) : OnTouchListener {
    private val gestureDetector : GestureDetector
    private var initialX : Float? = null

    override fun onTouch(view: View?, event: MotionEvent?): Boolean {
        return when (event) {
            null -> false
            else -> {
                when (event.action) {
                    MotionEvent.ACTION_MOVE -> onMove(event)
                    MotionEvent.ACTION_UP -> onMoveDone(event)
                }
                return gestureDetector.onTouchEvent(event)
            }
        }
    }

    open fun onMove(event: MotionEvent) {
        var xDiff = 0F
        if (initialX == null) {
            initialX = event.rawX
        } else xDiff = event.rawX - initialX!!

        if (xDiff < 0) onSwipingLeft(xDiff)
        else onSwipingRight(xDiff)
    }

    open fun onMoveDone(event: MotionEvent) {
        initialX = null
    }

    private inner class GestureListener : SimpleOnGestureListener(){
        private val SWIPE_THRESHOLD : Int = 100
        private val SWIPE_VELOCITY_THRESHOLD: Int = 100

        override fun onDown(e: MotionEvent): Boolean {
            return true
        }
        override fun onSingleTapUp(e: MotionEvent): Boolean {
            onClick()
            return super.onSingleTapUp(e)
        }

        override fun onDoubleTap(e: MotionEvent): Boolean {
            onDoubleClick()
            return super.onDoubleTap(e)
        }

        override fun onLongPress(e: MotionEvent) {
            onLongClick()
            super.onLongPress(e)
        }

        override fun onFling(
            e1: MotionEvent,
            e2: MotionEvent,
            velocityX: Float,
            velocityY: Float
        ): Boolean {
            Log.d("onSwipeListener", "$e1, $e2, $velocityX, $velocityY")
            try{
                val diffY = e2.y - e1.y
                val diffX = e2.x - e1.x

                if(abs(diffX) > abs(diffY)){
                    if(abs(diffX) > SWIPE_THRESHOLD && abs(velocityX) > SWIPE_VELOCITY_THRESHOLD){
                        if(diffX > 0){
                            onSwipedRight()
                        }
                        else{
                            onSwipedLeft()
                        }
                    }
                }
                else{
                    if(abs(diffY) > SWIPE_THRESHOLD && abs(velocityY) > SWIPE_VELOCITY_THRESHOLD){
                        if(diffY > 0){
                            onSwipedDown()
                        }
                        else{
                            onSwipedUp()
                        }
                    }
                }
            }catch (exc : Exception){
                exc.printStackTrace()
            }
            return false
        }
    }
    open fun onSwipingRight(xDiff: Float) {}
    open fun onSwipedRight() {}
    open fun onSwipingLeft(xDiff: Float) {}
    open fun onSwipedLeft() {}
    open fun onSwipedDown() {}
    open fun onSwipedUp() {}
    private fun onClick() {}
    private fun onDoubleClick() {}
    private fun onLongClick() {}

    init {
        gestureDetector = GestureDetector(c, GestureListener())
    }
}
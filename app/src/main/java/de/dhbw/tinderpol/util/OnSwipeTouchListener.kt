package de.dhbw.tinderpol.util
import android.content.Context;
import android.util.Log
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import java.lang.Math.abs

internal open class OnSwipeTouchListener (c: Context?) : OnTouchListener {
    private val gestureDetector : GestureDetector
    override fun onTouch(p0: View?, p1: MotionEvent?): Boolean {
        return when (p1) {
            null -> false
            else -> gestureDetector.onTouchEvent(p1)
        }
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
            Log.d("onSwipeListener", "${e1},${e2}, ${velocityX}, ${velocityY}")
            try{
                print("swiping is happening")
                val diffY = e2.y - e1.y
                val diffX = e2.x - e1.x

                if(abs(diffX) > abs(diffY)){
                    if(abs(diffX) > SWIPE_THRESHOLD && abs(velocityX) > SWIPE_VELOCITY_THRESHOLD){
                        if(diffX > 0){
                            onSwipeRight()
                        }
                        else{
                            onSwipeLeft()
                        }
                    }
                }
                else{
                    if(abs(diffY) > SWIPE_THRESHOLD && abs(velocityY) > SWIPE_VELOCITY_THRESHOLD){
                        if(diffY > 0){
                            onSwipeDown()
                        }
                        else{
                            onSwipeUp()
                        }
                    }
                }
            }catch (exc : Exception){
                exc.printStackTrace();
            }
            return false
        }
    }
    open fun onSwipeRight() {}
    open fun onSwipeLeft() {}
    open fun onSwipeDown() {}
    open fun onSwipeUp() {}
    private fun onClick() {}
    private fun onDoubleClick() {}
    private fun onLongClick() {}

    init {
        print("gesture listener called");

        gestureDetector = GestureDetector(c, GestureListener());
    }
}
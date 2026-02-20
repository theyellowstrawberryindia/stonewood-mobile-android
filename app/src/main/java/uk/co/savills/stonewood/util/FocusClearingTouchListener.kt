package uk.co.savills.stonewood.util

import android.annotation.SuppressLint
import android.app.Activity
import android.view.MotionEvent
import android.view.View

class FocusClearingTouchListener : View.OnTouchListener {
    var previousTouchAction: Int? = null

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(view: View, event: MotionEvent): Boolean {
        if (previousTouchAction != MotionEvent.ACTION_MOVE && event.action == MotionEvent.ACTION_UP) {
            val context = view.context
            if (context is Activity) context.currentFocus?.clearFocus()
            view.hideKeyboard()
        }
        previousTouchAction = event.action
        return false
    }
}

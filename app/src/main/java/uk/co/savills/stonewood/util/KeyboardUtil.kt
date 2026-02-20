package uk.co.savills.stonewood.util

import android.content.Context
import android.os.SystemClock
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText

fun EditText.showKeyboard() {
    requestFocus()
    dispatchDummyTouchEvent()
}

fun View.hideKeyboard() {
    if (isFocused) clearFocus()

    val inputMethodManager =
        context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(
        rootView.windowToken,
        0
    )
}

private fun View.dispatchDummyTouchEvent() {
    post {
        dispatchTouchEvent(getDummyMotionEvent(MotionEvent.ACTION_DOWN))
        dispatchTouchEvent(getDummyMotionEvent(MotionEvent.ACTION_UP))
    }
}

private fun getDummyMotionEvent(action: Int): MotionEvent {
    return MotionEvent.obtain(
        SystemClock.uptimeMillis(),
        SystemClock.uptimeMillis(),
        action,
        0f,
        0f,
        0
    )
}

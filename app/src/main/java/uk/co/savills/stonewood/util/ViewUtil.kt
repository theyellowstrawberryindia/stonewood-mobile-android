package uk.co.savills.stonewood.util

import android.annotation.SuppressLint
import android.text.InputType
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import androidx.core.content.ContextCompat
import androidx.core.view.children
import uk.co.savills.stonewood.R

fun Button.setTextVisibility(isVisible: Boolean) {
    textScaleX = if (isVisible) 1f else 0f
}

fun Button.setQuestionButtonStyle(isSelect: Boolean) {
    if (isSelect) {
        setBackgroundResource(R.color.colorPrimary)
        setTextColor(ContextCompat.getColor(this.context, R.color.textWhite))
    } else {
        setBackgroundResource(R.drawable.background_button_deselect)
        setTextColor(ContextCompat.getColor(this.context, R.color.textPrimary))
    }
}

fun Button.setOptionStyle(isSelected: Boolean, isHighlighted: Boolean = false) {
    when {
        isSelected -> {
            setBackgroundResource(R.color.colorPrimary)
            setTextColor(ContextCompat.getColor(this.context, R.color.textWhite))
        }

        isHighlighted -> {
            setBackgroundResource(R.color.colorSecondary)
            setTextColor(ContextCompat.getColor(this.context, R.color.textDefault))
        }

        else -> {
            setBackgroundResource(R.drawable.background_button_deselect)
            setTextColor(ContextCompat.getColor(this.context, R.color.textPrimary))
        }
    }
}

@SuppressLint("ClickableViewAccessibility")
fun EditText.allowNestedScrolling() {
    setOnTouchListener { v, _ ->
        v.parent.requestDisallowInterceptTouchEvent(true)
        false
    }
}

fun EditText.setDoneButton() {
    imeOptions = EditorInfo.IME_ACTION_DONE
    setRawInputType(InputType.TYPE_CLASS_TEXT)
}

fun View.setAllEnabled(enabled: Boolean) {
    isEnabled = enabled
    if (this is ViewGroup) children.forEach { child -> child.setAllEnabled(enabled) }
}

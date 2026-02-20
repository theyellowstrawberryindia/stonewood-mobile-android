package uk.co.savills.stonewood.util.customview

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import uk.co.savills.stonewood.R

class LetterSpacedTextView(context: Context, attrs: AttributeSet) :
    AppCompatTextView(context, attrs) {
    var letterSpacingMultiplier: Float = 0f
        set(value) {
            field = value
            applyLetterSpacing()
        }

    init {
        context.theme.obtainStyledAttributes(attrs, R.styleable.letterSpacedTextView, 0, 0).apply {
            try {
                letterSpacingMultiplier =
                    getFloat(R.styleable.letterSpacedTextView_letterSpacingMultiplier, 0f)
                applyLetterSpacing()
            } finally {
                recycle()
            }
        }
    }

    private fun applyLetterSpacing() {
        val textSizeSp = textSize / resources.displayMetrics.scaledDensity
        letterSpacing = letterSpacingMultiplier / textSizeSp
        invalidate()
    }
}

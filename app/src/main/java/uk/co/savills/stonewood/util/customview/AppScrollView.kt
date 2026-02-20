package uk.co.savills.stonewood.util.customview

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.widget.ScrollView
import uk.co.savills.stonewood.util.FocusClearingTouchListener

@SuppressLint("ClickableViewAccessibility")
class AppScrollView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : ScrollView(context, attrs, defStyleAttr) {

    init {
        setOnTouchListener(FocusClearingTouchListener())
    }
}

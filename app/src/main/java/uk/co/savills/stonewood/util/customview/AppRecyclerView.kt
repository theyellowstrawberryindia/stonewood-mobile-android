package uk.co.savills.stonewood.util.customview

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.RecyclerView
import uk.co.savills.stonewood.util.FocusClearingTouchListener

@SuppressLint("ClickableViewAccessibility")
class AppRecyclerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : RecyclerView(context, attrs, defStyleAttr) {

    init {
        setOnTouchListener(FocusClearingTouchListener())
    }
}

package uk.co.savills.stonewood.util.customview

import android.content.Context
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.content.withStyledAttributes
import uk.co.savills.stonewood.R

class LegendView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    init {
        val view = View.inflate(context, R.layout.custom_view_legend, this)

        val indicator: View = view.findViewById(R.id.statusIndicatorLegend)
        val title: TextView = view.findViewById(R.id.titleLegend)

        context.withStyledAttributes(attrs, R.styleable.legendView) {
            val indicatorColor = getColor(R.styleable.legendView_indicatorColor, 0)
            indicator.background.colorFilter = PorterDuffColorFilter(indicatorColor, PorterDuff.Mode.SRC_ATOP)
            title.text = getString(R.styleable.legendView_title)
        }
    }
}

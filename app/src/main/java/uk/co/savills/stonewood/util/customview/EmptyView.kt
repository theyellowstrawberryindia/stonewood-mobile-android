package uk.co.savills.stonewood.util.customview

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.withStyledAttributes
import androidx.core.view.isVisible
import androidx.databinding.BindingMethod
import androidx.databinding.BindingMethods
import uk.co.savills.stonewood.R

@BindingMethods(
    BindingMethod(
        type = EmptyView::class,
        attribute = "visibility",
        method = "setVisibility"
    )
)
class EmptyView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    init {
        val view = View.inflate(context, R.layout.custom_view_empty_view, this)

        val image: ImageView = view.findViewById(R.id.imageEmptyView)
        val title: TextView = view.findViewById(R.id.headerTextEmptyView)

        context.withStyledAttributes(attrs, R.styleable.emptyView) {
            image.setImageResource(getResourceId(R.styleable.emptyView_image, 0))
            title.text = getString(R.styleable.emptyView_header)
        }
        isVisible = false
    }

    fun setVisibility(list: List<*>?) {
        if (list == null) return
        isVisible = list.isEmpty()
    }
}

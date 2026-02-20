package uk.co.savills.stonewood.util.customview

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.GridLayout
import uk.co.savills.stonewood.databinding.ListItemBandBinding
import uk.co.savills.stonewood.util.setOptionStyle

class BandView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : FrameLayout(context, attrs, defStyleAttr) {

    private val binding = ListItemBandBinding.inflate(
        LayoutInflater.from(context),
        this,
        true
    )

    init {
        val margin = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            19f,
            context.resources.displayMetrics
        ).toInt()
        val screenWidth = binding.root.context.resources.displayMetrics.widthPixels

        layoutParams = GridLayout.LayoutParams().apply {
            width = (((screenWidth * 0.83) - (2 * margin)) / 4f).toInt()
        }
    }

    var text
        get() = binding.buttonBand.text.toString()
        set(value) {
            binding.buttonBand.text = value
        }

    fun setOptionStyle(isSelected: Boolean, isHighlighted: Boolean = false) {
        binding.buttonBand.setOptionStyle(isSelected, isHighlighted)
    }

    override fun setOnClickListener(l: OnClickListener?) {
        binding.buttonBand.setOnClickListener(l)
    }
}

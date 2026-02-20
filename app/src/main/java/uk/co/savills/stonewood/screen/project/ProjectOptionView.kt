package uk.co.savills.stonewood.screen.project

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.core.content.withStyledAttributes
import androidx.core.view.updateLayoutParams
import uk.co.savills.stonewood.R
import uk.co.savills.stonewood.databinding.LayoutProjectOptionBinding
import uk.co.savills.stonewood.util.setProperCase

class ProjectOptionView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : FrameLayout(context, attrs, defStyleAttr) {

    init {
        val binding =
            LayoutProjectOptionBinding.inflate(LayoutInflater.from(context), this, true)

        context.withStyledAttributes(attrs, R.styleable.projectOption) {
            binding.titleProjectOption.setProperCase(getString(R.styleable.projectOption_title))

            binding.separatorProjectOption.updateLayoutParams {
                height = getDimensionPixelSize(R.styleable.projectOption_separatorHeight, 1)
            }
        }
    }
}

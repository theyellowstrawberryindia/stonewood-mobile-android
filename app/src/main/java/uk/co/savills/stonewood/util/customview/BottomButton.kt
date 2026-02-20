package uk.co.savills.stonewood.util.customview

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.core.content.withStyledAttributes
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import uk.co.savills.stonewood.R
import uk.co.savills.stonewood.databinding.CustomViewBottomButtonBinding
import uk.co.savills.stonewood.util.setTextVisibility

class BottomButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : FrameLayout(context, attrs, defStyleAttr) {

    private val binding: CustomViewBottomButtonBinding = DataBindingUtil.inflate(
        LayoutInflater.from(context),
        R.layout.custom_view_bottom_button,
        this,
        true
    )

    init {
        context.withStyledAttributes(attrs, R.styleable.bottomButton) {
            binding.buttonBottomButton.text = getString(R.styleable.bottomButton_text)
            setIsEnabled(getBoolean(R.styleable.bottomButton_isEnabled, true))
            setIsLoading(getBoolean(R.styleable.bottomButton_isLoading, false))
        }
    }

    fun setIsEnabled(isEnabled: Boolean) {
        with(binding.buttonBottomButton) {
            this.isEnabled = isEnabled

            setBackgroundResource(
                if (isEnabled) R.color.buttonEnabled else R.color.buttonDisabled
            )
            setTextColor(
                ContextCompat.getColor(
                    context,
                    if (isEnabled) R.color.textPrimary else R.color.textLight
                )
            )
        }
    }

    override fun setOnClickListener(listener: OnClickListener?) {
        binding.buttonBottomButton.setOnClickListener(listener)
    }

    fun setIsLoading(isLoading: Boolean) {
        binding.buttonBottomButton.setTextVisibility(!isLoading)
        binding.progressBarBottomButton.isVisible = isLoading
    }
}

package uk.co.savills.stonewood.util.customview

import android.app.Activity
import android.content.Context
import android.graphics.drawable.Animatable2
import android.graphics.drawable.AnimatedVectorDrawable
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.core.content.withStyledAttributes
import androidx.core.view.isVisible
import androidx.databinding.BindingMethod
import androidx.databinding.BindingMethods
import androidx.databinding.DataBindingUtil
import uk.co.savills.stonewood.R
import uk.co.savills.stonewood.databinding.CustomViewStandardToolbarBinding
import uk.co.savills.stonewood.util.setProperCase

@BindingMethods(
    BindingMethod(
        type = StandardToolbar::class,
        attribute = "onOptionClick",
        method = "setOptionClickListener"
    )
)
class StandardToolbar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : FrameLayout(context, attrs, defStyleAttr) {
    private val loader
        get() = binding.progressBarStandardToolbar.background as AnimatedVectorDrawable

    private val binding: CustomViewStandardToolbarBinding = DataBindingUtil.inflate(
        LayoutInflater.from(context),
        R.layout.custom_view_standard_toolbar,
        this,
        true
    )

    init {
        with(binding) {
            context.withStyledAttributes(attrs, R.styleable.standardToolbar) {
                setTitle(getString(R.styleable.standardToolbar_title))

                setInfoText(getString(R.styleable.standardToolbar_infoText))

                setIsInfoTextVisible(
                    getBoolean(R.styleable.standardToolbar_isInfoTextVisible, false)
                )

                setIsLoading(getBoolean(R.styleable.standardToolbar_isLoading, false))

                setIsBackButtonVisible(
                    getBoolean(R.styleable.standardToolbar_isBackButtonVisible, true)
                )

                setLogoVisibility(
                    getBoolean(R.styleable.standardToolbar_isLogoVisible, false)
                )

                val optionImageResId = getResourceId(R.styleable.standardToolbar_optionImage, -1)
                if (optionImageResId != -1) optionImageStandardToolbar.setImageResource(
                    optionImageResId
                )

                optionTextStandardToolbar.text = getString(R.styleable.standardToolbar_optionText)
            }

            optionViewStandardToolbar.isVisible = optionTextStandardToolbar.text.isNotBlank()
            setBackButtonClickListener()
        }
    }

    fun setTitle(title: String?) {
        binding.titleStandardToolbar.setProperCase(title)
    }

    fun setInfoText(info: String?) {
        binding.infoTextStandardToolbar.text = info
    }

    fun setIsLoading(isVisible: Boolean) {
        if (isVisible) {
            loader.registerAnimationCallback(loaderAnimationCallback)
            loader.start()
        } else {
            loader.clearAnimationCallbacks()
            loader.stop()
            loader.reset()
        }
    }

    fun setOptionClickListener(listener: () -> Unit) {
        binding.optionViewStandardToolbar.setOnClickListener {
            listener.invoke()
        }
    }

    fun setIsInfoTextVisible(isVisible: Boolean) {
        binding.infoTextStandardToolbar.visibility =
            if (isVisible) View.VISIBLE else View.INVISIBLE
    }

    fun setIsBackButtonVisible(isVisible: Boolean) {
        binding.backButtonStandardToolbar.visibility =
            if (isVisible) View.VISIBLE else View.INVISIBLE
    }

    private fun setLogoVisibility(isVisible: Boolean) {
        binding.logoStandardToolbar.visibility = if (isVisible) View.VISIBLE else View.GONE
    }

    fun setBackButtonClickListener(listener: () -> Unit) {
        binding.backButtonStandardToolbar.setOnClickListener { listener.invoke() }
    }

    private fun setBackButtonClickListener() {
        binding.backButtonStandardToolbar.setOnClickListener {
            (context as Activity).onBackPressed()
        }
    }

    private val loaderAnimationCallback = object : Animatable2.AnimationCallback() {
        override fun onAnimationEnd(drawable: Drawable?) = loader.start()
    }
}

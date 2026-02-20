package uk.co.savills.stonewood.screen.survey.survey.riskassessment

import android.content.Context
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import uk.co.savills.stonewood.R
import uk.co.savills.stonewood.databinding.DialogPhotoRequiredBinding

class AssessmentCompleteDialog private constructor(
    private val dialog: AlertDialog,
    private val builder: Builder,
) {
    fun show() = dialog.show()

    fun setStatus(isComplete: Boolean) = apply {
        builder.setStatus(isComplete)
    }

    fun setMessageVisibility(isVisible: Boolean) = apply {
        builder.setMessageVisibility(isVisible)
    }

    fun setPositiveButton(buttonTextResId: Int, clickListener: (() -> Unit)? = null) = apply {
        builder.setPositiveButton(buttonTextResId, clickListener)
    }

    fun setNegativeButton(buttonTextResId: Int?, clickListener: (() -> Unit)? = null) = apply {
        builder.setNegativeButton(buttonTextResId, clickListener)
    }

    fun dismiss() = dialog.dismiss()

    class Builder(context: Context) {

        private val binding: DialogPhotoRequiredBinding = DialogPhotoRequiredBinding.inflate(
            LayoutInflater.from(context),
            null,
            false
        )

        private val alertDialogBuilder = AlertDialog.Builder(context)
        private lateinit var dialog: AlertDialog

        init {
            binding.positiveButtonPhotoRequired.setOnClickListener {
                dialog.dismiss()
            }
        }

        fun setStatus(isComplete: Boolean) = apply {
            with(binding.assessmentPhotoRequired) {
                setText(if (isComplete) R.string.pass else R.string.fail)
                setTextColor(
                    ContextCompat.getColor(
                        context,
                        if (isComplete) R.color.complete else R.color.incomplete
                    )
                )
            }
        }

        fun setMessageVisibility(isVisible: Boolean) = apply {
            binding.messagePhotoRequired.isVisible = isVisible
        }

        fun setPositiveButton(buttonTextResId: Int, clickListener: (() -> Unit)? = null) = apply {
            binding.positiveButtonPhotoRequired.apply {
                setText(buttonTextResId)
                setOnClickListener {
                    clickListener?.invoke()
                    dialog.dismiss()
                }
            }
        }

        fun setNegativeButton(buttonTextResId: Int?, clickListener: (() -> Unit)? = null) = apply {
            binding.buttonSeparatorPhotoRequired.isVisible = buttonTextResId != null
            binding.negativeButtonPhotoRequired.apply {
                isVisible = buttonTextResId != null

                if (buttonTextResId != null) {
                    setText(buttonTextResId)
                } else {
                    text = null
                }

                setOnClickListener {
                    clickListener?.invoke()
                    dialog.dismiss()
                }
            }
        }

        fun build(): AssessmentCompleteDialog {
            dialog = alertDialogBuilder
                .setView(binding.root)
                .create()

            requireNotNull(dialog.window).setBackgroundDrawableResource(R.color.transparent)

            return AssessmentCompleteDialog(dialog, this)
        }
    }
}

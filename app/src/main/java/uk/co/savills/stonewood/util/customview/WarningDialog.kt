package uk.co.savills.stonewood.util.customview

import android.content.Context
import android.text.Spannable
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import uk.co.savills.stonewood.R
import uk.co.savills.stonewood.databinding.DialogWarningBinding

class WarningDialog private constructor(
    private val dialog: AlertDialog,
    private val builder: Builder,
) {
    fun show() = dialog.show()

    fun setDescription(descriptionResId: Int) = apply {
        builder.setDescription(descriptionResId)
    }

    fun setDescription(description: String) = apply {
        builder.setDescription(description)
    }

    fun setPositiveButton(buttonTextResId: Int, clickListener: (() -> Unit)? = null) = apply {
        builder.setPositiveButton(buttonTextResId, clickListener)
    }

    fun setNegativeButton(buttonTextResId: Int, clickListener: (() -> Unit)? = null) = apply {
        builder.setNegativeButton(buttonTextResId, clickListener)
    }

    fun dismiss() = dialog.dismiss()

    class Builder(context: Context) {

        private val binding: DialogWarningBinding = DialogWarningBinding.inflate(
            LayoutInflater.from(context),
            null,
            false
        )

        private val alertDialogBuilder = AlertDialog.Builder(context)
        private lateinit var dialog: AlertDialog

        init {
            binding.positiveButtonWarning.setOnClickListener {
                dialog.dismiss()
            }
        }

        fun setDescription(descriptionResId: Int) = apply {
            binding.dialogDescriptionWarning.setText(descriptionResId)
        }

        fun setDescription(description: String) = apply {
            binding.dialogDescriptionWarning.text = description
        }

        fun setDescription(description: Spannable) = apply {
            binding.dialogDescriptionWarning.text = description
            binding.dialogDescriptionWarning.movementMethod = LinkMovementMethod.getInstance()
        }

        fun setPositiveButton(buttonTextResId: Int, clickListener: (() -> Unit)? = null) = apply {
            binding.positiveButtonWarning.apply {
                setText(buttonTextResId)
                setOnClickListener {
                    clickListener?.invoke()
                    dialog.dismiss()
                }
            }
        }

        fun setNegativeButton(buttonTextResId: Int?, clickListener: (() -> Unit)? = null) = apply {
            binding.buttonSeparatorWarning.isVisible = buttonTextResId != null
            binding.negativeButtonWarning.apply {
                isVisible = buttonTextResId != null

                if (buttonTextResId != null) setText(buttonTextResId) else text = null

                setOnClickListener {
                    clickListener?.invoke()
                    dialog.dismiss()
                }
            }
        }

        fun build(): WarningDialog {
            dialog = alertDialogBuilder
                .setView(binding.root)
                .create()

            requireNotNull(dialog.window).setBackgroundDrawableResource(R.color.transparent)

            return WarningDialog(dialog, this)
        }
    }
}

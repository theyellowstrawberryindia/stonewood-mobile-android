package uk.co.savills.stonewood.util.customview

import android.annotation.SuppressLint
import android.content.Context
import android.text.Spannable
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import uk.co.savills.stonewood.R

class StandardDialog private constructor(
    private val dialog: AlertDialog,
    private val builder: Builder,
) {
    val isShowing: Boolean
        get() = dialog.isShowing

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

    fun setNegativeButton(buttonTextResId: Int?, clickListener: (() -> Unit)? = null) = apply {
        builder.setNegativeButton(buttonTextResId, clickListener)
    }

    fun dismiss() = dialog.dismiss()

    class Builder(context: Context) {

        private val view = getView(context)
        private val alertDialogBuilder = AlertDialog.Builder(context)
        private lateinit var dialog: AlertDialog

        private val titleTextView by lazy { view.findViewById<TextView>(R.id.dialogTitleStandard) }
        private val descriptionTextView by lazy { view.findViewById<TextView>(R.id.dialogDescriptionStandard) }
        private val positiveButton by lazy { view.findViewById<Button>(R.id.positiveButtonStandard) }
        private val negativeButton by lazy { view.findViewById<Button>(R.id.negativeButtonStandard) }
        private val buttonSeparator by lazy { view.findViewById<View>(R.id.buttonSeparatorStandard) }

        @SuppressLint("InflateParams")
        private fun getView(context: Context): View {
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            return inflater.inflate(R.layout.dialog_standard, null)
        }

        fun setTitle(titleResId: Int) = apply { titleTextView.setText(titleResId) }

        fun setDescription(descriptionResId: Int) = apply { descriptionTextView.setText(descriptionResId) }

        fun setDescription(description: String) = apply {
            descriptionTextView.text = description
        }

        fun setDescription(description: Spannable) = apply {
            descriptionTextView.text = description
            descriptionTextView.movementMethod = LinkMovementMethod.getInstance()
        }

        fun setPositiveButton(buttonTextResId: Int, clickListener: (() -> Unit)? = null) = apply {
            positiveButton.apply {
                setText(buttonTextResId)
                setOnClickListener {
                    clickListener?.invoke()
                    dialog.dismiss()
                }
            }
        }

        fun setNegativeButton(buttonTextResId: Int?, clickListener: (() -> Unit)? = null) = apply {
            buttonSeparator.isVisible = buttonTextResId != null
            negativeButton.apply {
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

        fun setCancellable(isCancellable: Boolean) = apply {
            alertDialogBuilder.setCancelable(isCancellable)
        }

        fun build(): StandardDialog {
            dialog = alertDialogBuilder
                .setView(view)
                .create()

            requireNotNull(dialog.window).setBackgroundDrawableResource(R.color.transparent)

            return StandardDialog(dialog, this)
        }
    }
}

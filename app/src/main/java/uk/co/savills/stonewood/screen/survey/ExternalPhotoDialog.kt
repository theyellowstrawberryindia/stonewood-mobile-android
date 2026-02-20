package uk.co.savills.stonewood.screen.survey

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import uk.co.savills.stonewood.R
import uk.co.savills.stonewood.databinding.DialogExternalPhotoBinding

class ExternalPhotoDialog(
    context: Context,
    onTakePhoto: () -> Unit,
    onUseExisting: () -> Unit
) {
    private val dialog: AlertDialog

    init {
        val binding: DialogExternalPhotoBinding = DialogExternalPhotoBinding.inflate(
            LayoutInflater.from(context),
            null,
            false
        ).apply {
            onTakeNewPhoto = View.OnClickListener {
                onTakePhoto.invoke()
                dismiss()
            }

            this.onUseExisting = View.OnClickListener {
                onUseExisting.invoke()
                dismiss()
            }
        }

        dialog = AlertDialog.Builder(context)
            .setView(binding.root)
            .create()

        requireNotNull(dialog.window).setBackgroundDrawableResource(R.color.transparent)
    }

    fun show() {
        dialog.show()
    }

    fun dismiss() {
        dialog.dismiss()
    }
}

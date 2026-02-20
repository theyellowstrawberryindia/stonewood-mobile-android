package uk.co.savills.stonewood.screen.survey.survey.energy.selectexternalphoto

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.widget.ImageView
import androidx.appcompat.widget.LinearLayoutCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import uk.co.savills.stonewood.R
import uk.co.savills.stonewood.databinding.DialogCommunalDataConfirmationBinding
import uk.co.savills.stonewood.model.survey.entry.ExtBlockPhotoModel

class ConfirmationDialog(
    private val context: Context
) {
    private val binding = DialogCommunalDataConfirmationBinding.inflate(
        LayoutInflater.from(context),
        null,
        false
    )

    private val dialog: AlertDialog = AlertDialog.Builder(context)
        .setView(binding.root)
        .create().also {
            it.window?.setBackgroundDrawableResource(R.color.transparent)
        }

    private val scope = CoroutineScope(Dispatchers.IO)

    init {
        binding.dismissButtonCommunalDataConfirmation.setOnClickListener {
            dismiss()
        }
    }

    fun setData(data: ExtBlockPhotoModel) = apply {
        setText(data)
        setPhotos(data.imagePaths)
    }

    @SuppressLint("SetTextI18n")
    private fun setText(data: ExtBlockPhotoModel) {
        with(binding) {
            titleCommunalDataConfirmation.text = binding.root.context.getString(
                R.string.external_photos_title
            )

            descriptionCommunalDataConfirmation.text = binding.root.context.getString(
                R.string.external_photos_confirmation_message
            )

            val address = data.address
            addressCommunalDataConfirmation.text =
                "${address.number} ${address.line1}\n${address.line2}\n${address.postalCode}"
        }
    }

    private fun setPhotos(filePaths: List<String>) {
        filePaths.forEach { filePath ->
            val view = ImageView(context)
            view.layoutParams = LinearLayoutCompat.LayoutParams(
                100,
                100
            ).apply {
                marginStart = 10
            }

            view.scaleType = ImageView.ScaleType.CENTER_CROP

            scope.launch(Dispatchers.IO) {
                val bitmap = BitmapFactory.decodeFile(filePath)
                if (bitmap != null) {
                    withContext(Dispatchers.Main) {
                        view.setImageBitmap(bitmap)
                    }
                }
            }

            binding.photosCommunalDataConfirmation.addView(view)
        }
    }

    fun setConfirmationListener(listener: () -> Unit) = apply {
        binding.confirmButtonCommunalDataConfirmation.setOnClickListener {
            listener.invoke()
            dismiss()
        }
    }

    fun show() = dialog.show()

    fun dismiss() = dialog.dismiss()
}

package uk.co.savills.stonewood.screen.survey.survey.stocksurvey.selectcommunaldata

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.widget.ImageView
import androidx.annotation.StringRes
import androidx.appcompat.widget.LinearLayoutCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import uk.co.savills.stonewood.R
import uk.co.savills.stonewood.databinding.DialogCommunalDataConfirmationBinding
import uk.co.savills.stonewood.model.survey.entry.CommunalDataModel

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

    fun setData(data: CommunalDataModel) = apply {
        setText(data)
        setPhotos(data.imagePaths)
    }

    @SuppressLint("SetTextI18n")
    private fun setText(data: CommunalDataModel) {
        with(binding) {
            titleCommunalDataConfirmation.text = binding.root.context.getString(
                R.string.existing_data_title,
                data.element
            )

            descriptionCommunalDataConfirmation.text = binding.root.context.getString(
                R.string.communal_data_confirmation_message,
                data.element
            )

            val address = data.address
            addressCommunalDataConfirmation.text =
                "${address.number} ${address.line1}\n${address.line2}\n${address.postalCode}"
        }
    }

    private fun setPhotos(filePaths: List<String>) {
        binding.photosCommunalDataConfirmation.removeAllViews()
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

    private fun getString(@StringRes id: Int, vararg formatArgs: Any?) =
        context.getString(id, formatArgs)
}

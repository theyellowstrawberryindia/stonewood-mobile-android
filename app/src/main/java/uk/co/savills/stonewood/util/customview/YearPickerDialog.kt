package uk.co.savills.stonewood.util.customview

import android.content.Context
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import uk.co.savills.stonewood.R
import uk.co.savills.stonewood.databinding.DialogYearPickerBinding
import java.time.Year

class YearPickerDialog private constructor(
    private val dialog: AlertDialog,
    private val builder: Builder
) {
    var selectedYear
        get() = builder.selectedYear
        set(value) {
            builder.selectedYear = value
        }

    fun show() = dialog.show()

    fun setMinYear(year: Year) = apply { builder.setMinYear(year) }

    fun setMaxYear(year: Year) = apply { builder.setMaxYear(year) }

    fun reset() = builder.reset()

    fun dismiss() = dialog.dismiss()

    class Builder(context: Context) {

        private val binding = DialogYearPickerBinding.inflate(
            LayoutInflater.from(context),
            null,
            false
        )

        private val alertDialogBuilder = AlertDialog.Builder(context)
        private lateinit var dialog: AlertDialog

        init {
            with(binding.yearYearPicker) {
                minValue = 0
                maxValue = 9999
                value = Year.now().value
            }

            binding.buttonYearPicker.setOnClickListener {
                dialog.dismiss()
            }
        }

        var selectedYear: Year
            get() {
                return Year.of(binding.yearYearPicker.value)
            }
            set(value) {
                binding.yearYearPicker.value = value.value
            }

        fun setTitle(titleResId: Int) = apply { binding.titleYearPicker.setText(titleResId) }

        fun setMinYear(year: Year) = apply {
            binding.yearYearPicker.minValue = year.value
        }

        fun setMaxYear(year: Year) = apply {
            binding.yearYearPicker.maxValue = year.value
        }

        fun reset() = apply {
            with(binding.yearYearPicker) {
                minValue = 0
                maxValue = 9999
                value = Year.now().value
            }
        }

        fun setSelectionListener(listener: (Year) -> Unit) = apply {
            binding.buttonYearPicker.setOnClickListener {
                listener.invoke(Year.of(binding.yearYearPicker.value))
                dialog.dismiss()
            }
        }

        fun build(): YearPickerDialog {
            dialog = alertDialogBuilder
                .setView(binding.root)
                .create()

            requireNotNull(dialog.window).setBackgroundDrawableResource(R.color.transparent)

            return YearPickerDialog(dialog, this)
        }
    }
}

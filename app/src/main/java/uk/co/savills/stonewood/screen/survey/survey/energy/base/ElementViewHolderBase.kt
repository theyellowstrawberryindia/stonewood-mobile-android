package uk.co.savills.stonewood.screen.survey.survey.energy.base

import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import uk.co.savills.stonewood.R
import uk.co.savills.stonewood.model.survey.element.energy.EnergySurveyElementModel
import uk.co.savills.stonewood.model.survey.element.energy.EnergySurveyElementType
import uk.co.savills.stonewood.model.survey.element.energy.EnergySurveySubElementModel
import uk.co.savills.stonewood.screen.survey.survey.energy.ElementUpdateListener
import uk.co.savills.stonewood.util.customview.ComboBox
import uk.co.savills.stonewood.util.customview.WarningDialog

abstract class ElementViewHolderBase(
    protected val view: View,
    protected val titleTextView: TextView,
    protected val comboBox: ComboBox,
    protected val userEntryEditText: EditText
) {
    protected lateinit var element: EnergySurveyElementModel
    protected lateinit var updateListener: ElementUpdateListener

    private var userEntryTextWatcher: TextWatcher? = null

    protected val isDropDown
        get() = element.type == EnergySurveyElementType.DROP_DOWN

    protected val subElements
        get() = element.subElements.filterNot { it.isSkipped }

    private val rareSubElementWarningDialog by lazy {
        WarningDialog.Builder(view.context)
            .setNegativeButton(R.string.no) {
                comboBox.reset()
            }
            .build()
    }

    open fun bind(
        element: EnergySurveyElementModel,
        updateListener: ElementUpdateListener
    ) {
        this.element = element
        this.updateListener = updateListener

        titleTextView.text = element.titleShort

        setViewVisibility()

        if (isDropDown) {
            setComboBox()
        } else {
            setUserEntryEditText()
        }
    }

    private fun setViewVisibility() {
        val isDefaultValue = isDropDown && element.subElements[0].skipCodes.joinToString("")
            .equals("default value", true)
        view.isVisible = !isDefaultValue

        comboBox.visibility = if (isDropDown) View.VISIBLE else View.INVISIBLE
        userEntryEditText.isVisible = !isDropDown
    }

    private fun setComboBox() {
        if (element.isPreSelection) {
            view.isVisible = false
            updateListener.onSubElementSelected(subElements[0], element)
            return
        }

        with(comboBox) {
            setType(ComboBox.Type.DISABLE_WHEN_SINGLE_ITEM)

            val selectedItemPosition =
                subElements.indexOfFirst { it.id == element.entry.subElementId }
            setItems(
                subElements.map { it.title },
                subElementSelectionListener,
                selectedItemPosition
            )
        }
    }

    private fun setUserEntryEditText() {
        with(userEntryEditText) {
            if (userEntryTextWatcher != null) {
                removeTextChangedListener(userEntryTextWatcher)
            }

            setText(element.entry.subElement)

            inputType =
                if (
                    element.type == EnergySurveyElementType.QUANTITY ||
                    element.type == EnergySurveyElementType.QUANTITY0
                ) {
                    android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL
                } else {
                    android.text.InputType.TYPE_CLASS_TEXT
                }

            userEntryTextWatcher = addTextChangedListener {
                var text = it.toString()

                if (element.type == EnergySurveyElementType.QUANTITY && text.all { c -> c == '0' }) {
                    text = ""
                    error =
                        context.getString(uk.co.savills.stonewood.R.string.positive_number_error)
                }

                updateListener.onUserEntryUpdate(text, element)
            }

            setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    if (
                        element.type == EnergySurveyElementType.QUANTITY ||
                        element.type == EnergySurveyElementType.QUANTITY0
                    ) {
                        val value = try {
                            text.toString().toInt()
                        } catch (e: Exception) {
                            null
                        }

                        error = value?.let {
                            if (it > element.limitValue) context.getString(R.string.limit_error) else null
                        }

                        android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL
                    }

                    updateListener.onUserEntryComplete(element)
                }
            }
        }
    }

    private val subElementSelectionListener = object : ComboBox.OnItemSelectedListener {
        override fun onPlaceHolderSelected() {
            onSubElementChange(null)
        }

        override fun onItemSelected(position: Int, isUserSelected: Boolean) {
            val subElement = subElements[position]

            if (subElement.id != element.entry.subElementId && subElement.isRare) {
                val message = view.context.getString(
                    R.string.rare_sub_element_warning_dialog_message,
                    subElement.title
                )

                rareSubElementWarningDialog.setDescription(message)
                    .setPositiveButton(R.string.yes) {
                        onSubElementChange(subElement)
                    }
                    .show()
            } else {
                onSubElementChange(subElement)
            }
        }
    }

    open fun onSubElementChange(subElement: EnergySurveySubElementModel?) {
        if (subElement == null) {
            updateListener.onPlaceHolderSelected(element)
        } else {
            updateListener.onSubElementSelected(subElement, element)
        }
    }
}

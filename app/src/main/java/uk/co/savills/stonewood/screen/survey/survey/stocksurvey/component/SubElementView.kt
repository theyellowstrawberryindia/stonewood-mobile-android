package uk.co.savills.stonewood.screen.survey.survey.stocksurvey.component

import android.content.Context
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import uk.co.savills.stonewood.R
import uk.co.savills.stonewood.databinding.LayoutSubElementViewBinding
import uk.co.savills.stonewood.model.survey.element.StockSurveyElementModel
import uk.co.savills.stonewood.model.survey.element.StockSurveySubElementModel
import uk.co.savills.stonewood.util.customview.ComboBox
import uk.co.savills.stonewood.util.setAllEnabled
import uk.co.savills.stonewood.util.setQuestionButtonStyle
import java.lang.IndexOutOfBoundsException

class SubElementView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : FrameLayout(context, attrs, defStyleAttr) {

    interface Listener {
        fun onSubElementSelected(subElement: StockSurveySubElementModel?, isUserSelected: Boolean)
        fun onIsIndividualChange(isIndividual: Boolean?)
        fun onIsNewChange(isNew: Boolean)
        fun onUserEntryChange(userEntry: String)
    }

    private val binding = LayoutSubElementViewBinding.inflate(
        LayoutInflater.from(context),
        this,
        true
    )

    private lateinit var element: StockSurveyElementModel

    private var listener: Listener? = null

    private val subElements
        get() = element.subElements.filterNot { it.isSkipped }

    private val communalOptions = listOf("Individual property", "Communal")

    private var userEntryTextWatcher: TextWatcher? = null

    fun setListener(listener: Listener) {
        this.listener = listener
    }

    fun setElement(element: StockSurveyElementModel) {
        this.element = element

        binding.communalElementLayoutSubElementView.isVisible = element.isCommunal

        if (element.isCommunal) {
            setCommunalComboBox()
            setDataButtons()
            setDataViewVisibility()
        }

        setSubElementComboBox()
        setUserEntryEditText()
    }

    fun setError(error: String) {
        binding.userEntryEditTextSubElementView.error = error
    }

    private fun setCommunalComboBox() {
        with(binding.communalComboBoxSubElementView) {
            var selectedItemPosition = -1

            element.entry.isIndividual?.let { isIndividual ->
                selectedItemPosition = if (isIndividual) 0 else 1
            }

            setItems(
                communalOptions,
                onCommunalSelectedListener,
                selectedItemPosition
            )
        }
    }

    private val onCommunalSelectedListener = object : ComboBox.OnItemSelectedListener {
        override fun onPlaceHolderSelected() {
            listener?.onIsIndividualChange(null)
            setDataViewVisibility()
        }

        override fun onItemSelected(position: Int, isUserSelected: Boolean) {
            listener?.onIsIndividualChange(position == 0)
            setDataViewVisibility()
        }
    }

    private fun setDataButtons() = with(binding) {
        val isCloned = element.entry.isCloned == true
        createNewButtonSubElementView.setQuestionButtonStyle(!isCloned)
        usePreviousButtonSubElementView.setQuestionButtonStyle(isCloned)

        createNewButtonSubElementView.setOnClickListener {
            listener?.onIsNewChange(true)
        }

        usePreviousButtonSubElementView.setOnClickListener {
            listener?.onIsNewChange(false)
        }
    }

    fun setDataViewVisibility() {
        binding.dataButtonsLayoutSubElementView.isVisible = element.entry.isIndividual == false
        binding.dataLabelSubElementView.isVisible = element.entry.isIndividual == false
    }

    private fun setSubElementComboBox() {
        with(binding.subElementComboBoxSubElementView) {
            val selectedItemPosition =
                subElements.indexOfFirst { element.entry.subElement == it.title }
            setItems(
                subElements.map(::getSubElementText),
                onSubElementSelectedListener,
                selectedItemPosition
            )
        }
    }

    private fun getSubElementText(subElement: StockSurveySubElementModel): String {
        var title = subElement.title
        if (title.contains('<') && title.contains('>') && !title.startsWith('<')) {
            title = title.removeRange(title.indexOf('<'), title.indexOf('>') + 1)
        }

        return title
    }

    private val onSubElementSelectedListener = object : ComboBox.OnItemSelectedListener {
        override fun onPlaceHolderSelected() {
            listener?.onSubElementSelected(null, false)
            binding.userEntryEditTextSubElementView.isVisible = false
        }

        override fun onItemSelected(position: Int, isUserSelected: Boolean) {
            val subElement = try {
                subElements[position]
            } catch (exception: IndexOutOfBoundsException) {
                null
            }
            listener?.onSubElementSelected(subElement, isUserSelected)

            if (element.isCommunal && element.entry.isCloned == null) {
                setDataButtons()
            }

            updateUserEntry()
        }
    }

    private fun setUserEntryEditText() {
        with(binding.userEntryEditTextSubElementView) {
            if (userEntryTextWatcher != null) {
                removeTextChangedListener(userEntryTextWatcher)
            }

            setText(element.entry.subElementUserEntry)
            setUserEntryEditTextInputType(element.entry.subElement)
            setUserEntryEditTextVisibility()

            userEntryTextWatcher = addTextChangedListener {
                listener?.onUserEntryChange(it.toString())
            }

            setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    val userEntry = text.toString()
                    val subElement = element.entry.subElement

                    if (
                        subElement.contains("<text>", true) &&
                        userEntry.length < 4 &&
                        userEntry.isNotEmpty()
                    ) {
                        val errorMessage =
                            binding.root.context.getString(R.string.minimum_character_error)
                        error = errorMessage
                    }
                }
            }
        }
    }

    private fun setUserEntryEditTextInputType(subElement: String) {
        binding.userEntryEditTextSubElementView.inputType =
            when {
                subElement.contains("<PositiveNumeric>", ignoreCase = true) -> {
                    InputType.TYPE_CLASS_NUMBER
                }

                subElement.contains("<numeric>", ignoreCase = true) -> {
                    InputType.TYPE_CLASS_NUMBER or
                        InputType.TYPE_NUMBER_FLAG_SIGNED
                }

                subElement.contains("<decimal>", ignoreCase = true) -> {
                    InputType.TYPE_CLASS_NUMBER or
                        InputType.TYPE_NUMBER_FLAG_DECIMAL or
                        InputType.TYPE_NUMBER_FLAG_SIGNED
                }

                subElement.contains("<positiveDecimal>", ignoreCase = true) -> {
                    InputType.TYPE_CLASS_NUMBER or
                        InputType.TYPE_NUMBER_FLAG_DECIMAL
                }

                else -> InputType.TYPE_CLASS_TEXT
            }
    }

    private fun setUserEntryEditTextVisibility() {
        binding.comboBoxSpacingSubElementView.isVisible =
            subElements.size > 1 && element.entry.isUserEntryRequired
        binding.userEntryEditTextSubElementView.isVisible =
            element.entry.isUserEntryRequired
    }

    private fun updateUserEntry() {
        if (!element.entry.isUserEntryRequired) {
            element.entry.subElementUserEntry = ""
            binding.userEntryEditTextSubElementView.setText("")
        }

        setUserEntryEditTextInputType(element.entry.subElement)
        setUserEntryEditTextVisibility()
    }

    fun setSubElementEditable(isEditable: Boolean) {
        with(binding) {
            subElementComboBoxSubElementView.setAllEnabled(isEditable)
            userEntryEditTextSubElementView.isEnabled = isEditable
        }
    }
}

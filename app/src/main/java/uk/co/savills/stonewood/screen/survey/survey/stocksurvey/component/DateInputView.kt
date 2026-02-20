package uk.co.savills.stonewood.screen.survey.survey.stocksurvey.component

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import uk.co.savills.stonewood.R
import uk.co.savills.stonewood.databinding.LayoutDateInputViewBinding
import uk.co.savills.stonewood.util.customview.ComboBox
import uk.co.savills.stonewood.util.customview.YearPickerDialog
import java.time.Month
import java.time.Year
import java.time.YearMonth

class DateInputView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : FrameLayout(context, attrs, defStyleAttr) {

    enum class Type { PAST, FUTURE, ANY }

    interface Listener {
        fun onMonthSelected(month: Month?)
        fun onYearSelected(year: Year?)
    }

    private val binding = LayoutDateInputViewBinding.inflate(
        LayoutInflater.from(context),
        this,
        true
    )

    private var listener: Listener? = null

    private var type: Type = Type.ANY

    private var month: Month? = null
    private var year: Year? = null

    private val yearPickerDialog by lazy {
        YearPickerDialog.Builder(context)
            .setSelectionListener(::onYearSelected)
            .build()
    }

    private val monthSelectionListener = object : ComboBox.OnItemSelectedListener {
        override fun onPlaceHolderSelected() {
            listener?.onMonthSelected(null)
            this@DateInputView.month = null
        }

        override fun onItemSelected(position: Int, isUserSelected: Boolean) {
            val month = Month.values()[position]
            listener?.onMonthSelected(month)
            this@DateInputView.month = month
        }
    }

    init {
        binding.yearEditTextDateInputView.setOnClickListener {
            yearPickerDialog.show()
        }
    }

    fun setListener(listener: Listener) {
        this.listener = listener
    }

    fun bind(
        type: Type,
        month: Month? = null,
        year: Year? = null
    ) {
        this.type = type
        this.month = month
        this.year = year

        setMonths()

        yearPickerDialog.selectedYear = year ?: Year.now()
        binding.yearEditTextDateInputView.setText(year?.value?.toString().orEmpty())
        setYearPicker()
    }

    private fun onYearSelected(year: Year) {
        this.year = year
        binding.yearEditTextDateInputView.setText(year.value.toString())

        setMonths()

        listener?.onYearSelected(year)
    }

    private fun setMonths() {
        var months = Month.values().toList()
        if (year == Year.now()) {
            months = when (type) {
                Type.PAST -> months.filter { it.value <= YearMonth.now().month.value }
                Type.FUTURE -> months.filter { it.value >= YearMonth.now().month.value }
                Type.ANY -> months
            }
        }
        val monthStrings = months.map { it.name.toLowerCase().capitalize() }
        val selectedMonthPosition = months.indexOfFirst { it == month }

        binding.monthsDateInputView.setItems(
            monthStrings,
            monthSelectionListener,
            selectedMonthPosition,
            R.string.select_month
        )
    }

    private fun setYearPicker() = with(yearPickerDialog) {
        val currentYear = Year.now()

        selectedYear = currentYear
        setMaxYear(currentYear.plusYears(FUTURE_DATE_LIMIT))
        setMinYear(currentYear.minusYears(PAST_DATE_LIMIT))
        when (type) {
            Type.PAST -> {
                setMaxYear(currentYear)
            }
            Type.FUTURE -> {
                setMaxYear(currentYear.plusYears(FUTURE_DATE_LIMIT))
                setMinYear(currentYear)
            }
            Type.ANY -> Unit
        }
    }

    companion object {
        private const val FUTURE_DATE_LIMIT = 10L
        private const val PAST_DATE_LIMIT = 20L
    }
}

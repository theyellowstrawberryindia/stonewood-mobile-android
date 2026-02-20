package uk.co.savills.stonewood.screen.survey.survey.stocksurvey

import android.app.Activity
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import uk.co.savills.stonewood.R
import uk.co.savills.stonewood.databinding.ListItemStockSurveyElementBinding
import uk.co.savills.stonewood.model.survey.BandModel
import uk.co.savills.stonewood.model.survey.StockSurveyType
import uk.co.savills.stonewood.model.survey.element.StockSurveyElementModel
import uk.co.savills.stonewood.model.survey.element.StockSurveySubElementModel
import uk.co.savills.stonewood.model.survey.entry.Date
import uk.co.savills.stonewood.screen.survey.survey.stocksurvey.component.DateInputView
import uk.co.savills.stonewood.screen.survey.survey.stocksurvey.component.PhotosView
import uk.co.savills.stonewood.screen.survey.survey.stocksurvey.component.SubElementView
import uk.co.savills.stonewood.util.allowNestedScrolling
import uk.co.savills.stonewood.util.customview.BandView
import uk.co.savills.stonewood.util.customview.WarningDialog
import uk.co.savills.stonewood.util.hideKeyboard
import uk.co.savills.stonewood.util.photo.deletePhotoFile
import uk.co.savills.stonewood.util.setAllEnabled
import uk.co.savills.stonewood.util.setDoneButton
import uk.co.savills.stonewood.util.setQuestionButtonStyle
import uk.co.savills.stonewood.util.showKeyboard
import java.lang.Integer.max
import java.time.Month
import java.time.Year
import kotlin.math.roundToInt
import kotlin.NumberFormatException as NumberFormatException

class ElementViewHolder private constructor(
    private val binding: ListItemStockSurveyElementBinding,
    private val ageBands: List<BandModel>,
    private val renewalBands: List<BandModel>,
    private val noAccessReasons: List<String>,
    private val areRepairsAvailable: Boolean,
) : RecyclerView.ViewHolder(binding.root),
    PhotosView.Listener,
    SubElementView.Listener,
    DateInputView.Listener {

    interface UserActionListener {
        fun onSubElementSelected(position: Int)
        fun onViewExpanded(element: StockSurveyElementModel, position: Int)
    }

    private var renewalQuantityTextWatcher: TextWatcher? = null
    private var repairCostTextWatcher: TextWatcher? = null
    private var repairDescriptionTextWatcher: TextWatcher? = null
    private var notesTextWatcher: TextWatcher? = null

    private lateinit var element: StockSurveyElementModel
    private var ageOfProperty: Int = Int.MAX_VALUE
    private lateinit var updateListener: ElementUpdateListener
    private lateinit var userActionListener: UserActionListener

    private val subElements
        get() = element.subElements.filterNot { it.isSkipped }

    private val extremeRenewalBandDialog by lazy {
        WarningDialog.Builder(binding.root.context)
            .setDescription(R.string.extreme_renewal_band_warning_message)
            .build()
    }

    val id
        get() = element.id

    val isExpanded
        get() = binding.contentViewStockSurveyElement.isVisible

    private val dateInputType
        get() = when {
            element.isFutureDateEntry -> DateInputView.Type.FUTURE
            element.isPastDateEntry -> DateInputView.Type.PAST
            else -> DateInputView.Type.ANY
        }

    fun bind(
        element: StockSurveyElementModel,
        ageOfProperty: Int,
        updateListener: ElementUpdateListener,
        userActionListener: UserActionListener
    ) {
        this.element = element
        this.updateListener = updateListener
        this.ageOfProperty = ageOfProperty
        this.userActionListener = userActionListener

        setTitleView()
        binding.subElementViewStockSurveyElement.setListener(this)
        binding.subElementViewStockSurveyElement.setElement(element)
        setExtraInfoViewVisibility()
        if (element.isAsBuiltRequired) setAsBuiltView()
        setAgeBandGrid()
        setRenewalBandGrid()
        setRenewalQuantityView()

        if (areRepairsAvailable) {
            setRepairsBasedOnRenewalBand()
            setRepairsView()
        }

        setDateInputView()
        setPhotoGrid()
        setNotesView()
        setEditable()

        binding.containerStockSurveyElement.setOnClickListener {
            val currentFocus = (binding.root.context as Activity).currentFocus

            if (currentFocus is EditText) {
                currentFocus.hideKeyboard()
            } else if (!isExpanded) {
                binding.contentViewStockSurveyElement.isVisible = true
                binding.containerStockSurveyElement.isClickable = false
                userActionListener.onViewExpanded(element, absoluteAdapterPosition)
            }
        }

        binding.executePendingBindings()
    }

    private fun setDateInputView() = with(binding.dateViewStockSurveyElement) {
        isVisible = element.isDateEntry

        setListener(this@ElementViewHolder)

        if (element.isDateEntry) {
            binding.dateViewStockSurveyElement.bind(
                dateInputType,
                element.entry.date?.month,
                element.entry.date?.year
            )
        }
    }

    private fun setTitleView() {
        with(binding) {
            titleStockSurveyElement.text = element.title

            setCompletionStatusView()

            val subElement = subElements.find { it.title == element.entry.subElement }
            setLifeView(subElement)
        }
    }

    private fun setCompletionStatusView() {
        binding.titleStockSurveyElement.setCompoundDrawablesWithIntrinsicBounds(
            0,
            0,
            if (!element.entry.isComplete) R.drawable.background_element_incomplete_indicator else 0,
            0
        )
    }

    private fun setLifeView(subElement: StockSurveySubElementModel?) {
        binding.lifeTextStockSurveyElement.text =
            if (subElement != null && subElement.life > 0 && !element.isSpecialEntry) {
                binding.root.context.resources.getQuantityString(
                    R.plurals.life,
                    subElement.life,
                    subElement.life
                )
            } else {
                ""
            }
    }

    private fun setExtraInfoViewVisibility() {
        binding.extraInfoViewStockSurveyElement.isVisible = element.isExtraInfoRequired

        if (element.isExtraInfoRequired) {
            binding.renewalRepairsLayoutStockSurveyElement.isVisible = !element.isRebuildsEntry
        }

        binding.notesViewStockSurveyElement.isVisible = element.isExtraInfoRequired
    }

    private fun setAsBuiltView() {
        binding.asBuiltLayoutStockSurveyElement.isVisible = true

        setAsBuiltViewStyle(element.entry.asBuilt)

        binding.asBuiltButtonStockSurveyElement.setOnClickListener {
            if (element.entry.asBuilt == true) return@setOnClickListener

            element.entry.asBuilt = true
            setAsBuiltViewStyle(true)
            onEntryUpdate()
        }

        binding.notAsBuiltButtonStockSurveyElement.setOnClickListener {
            if (element.entry.asBuilt == false) return@setOnClickListener

            element.entry.asBuilt = false
            setAsBuiltViewStyle(false)
            onEntryUpdate()
        }
    }

    private fun setAsBuiltViewStyle(isAsBuilt: Boolean?) {
        with(binding) {
            asBuiltButtonStockSurveyElement.setQuestionButtonStyle(isAsBuilt == true)
            notAsBuiltButtonStockSurveyElement.setQuestionButtonStyle(isAsBuilt == false)
        }
    }

    private fun setAgeBandGrid() {
        val grid = binding.ageBandGridStockSurveyElement
        grid.removeAllViews()

        val ageBands = if (element.disableAgeBandFiltering) {
            ageBands
        } else {
            ageBands.filter { it.lowerBound <= ageOfProperty }
        }

        binding.ageLabelStockSurveyElement.isVisible = ageBands.isNotEmpty()
        grid.isVisible = ageBands.isNotEmpty()

        if (ageBands.isEmpty()) {
            element.entry.existingAgeBand = 0
            onEntryUpdate()
        }

        for (band in ageBands) {
            val view = BandView(grid.context)

            view.text = band.toString()
            view.setOptionStyle(band.lowerBound == element.entry.existingAgeBand)
            view.setOnClickListener {
                grid.children.forEach { view ->
                    (view as BandView).setOptionStyle(false)
                }

                element.entry.existingAgeBand = band.lowerBound
                view.setOptionStyle(true)

                if (element.surveyType == StockSurveyType.INTERNAL) {
                    setRenewalBandButtonStyle()
                    validateRenewalBand()
                }

                onEntryUpdate()
            }

            grid.addView(view)
        }
    }

    private fun setRenewalBandGrid() {
        val grid = binding.renewalBandGridStockSurveyElement
        grid.removeAllViews()

        for (band in renewalBands) {
            val view = BandView(grid.context)
            view.text = band.toString()
            band.isSelected = band.lowerBound == element.entry.lifeRenewalBand

            view.setOnClickListener {
                for (renewalBand in renewalBands) {
                    renewalBand.isSelected = false
                }

                band.isSelected = true
                element.entry.lifeRenewalBand = band.lowerBound

                if (band.upperBound != null) {
                    if (band.upperBound <= 5) element.entry.repair = false
                } else {
                    if (band.lowerBound <= 5) element.entry.repair = false
                }

                setRenewalBandButtonStyle()
                if (areRepairsAvailable) setRepairsBasedOnRenewalBand()
                onEntryUpdate()
                validateRenewalBand()
            }

            grid.addView(view)
        }

        setRenewalBandButtonStyle()
    }

    private fun validateRenewalBand() {
        if (element.isRenewalBandExtreme(renewalBands)) {
            extremeRenewalBandDialog.show()
            binding.notesEditTextStockSurveyElement.showKeyboard()
        }
    }

    private fun setRepairsBasedOnRenewalBand() {
        val band = renewalBands.find { it.lowerBound == element.entry.lifeRenewalBand }

        val isVisible = band == null || if (band.upperBound == null) {
            band.lowerBound > 5
        } else {
            band.upperBound > 5
        }

        if (!isVisible) {
            element.entry.repairSpotPrice = null
            element.entry.repairDescription = ""
        }

        setRepairsViewStyle(element.entry.repair)
        binding.repairsLayoutStockSurveyElement.isVisible = isVisible
    }

    private fun setRenewalBandButtonStyle() {
        for (view in binding.renewalBandGridStockSurveyElement.children) {
            (view as BandView).let { bandView ->
                val band = requireNotNull(renewalBands.find { it.toString() == bandView.text })
                val highlightedBands = element.getHighlightedRenewalBands(renewalBands)
                bandView.setOptionStyle(band.isSelected, highlightedBands.contains(band))
            }
        }
    }

    private fun setRenewalQuantityView() {
        with(binding.renewalQuantityEditTextStockSurveyElement) {
            if (renewalQuantityTextWatcher != null) {
                removeTextChangedListener(renewalQuantityTextWatcher)
            }

            setText(element.entry.lifeRenewalUnits?.toString() ?: "")
            isEnabled =
                !(element.isAdditionalRenewalQuantityRequired && element.useQuantityMultiplier)
            hint = if (isEnabled) context.getString(R.string.quantity_hint) else null

            renewalQuantityTextWatcher = addTextChangedListener {
                try {
                    val quantity = it.toString().toInt()
                    if (quantity <= 0) {
                        element.entry.lifeRenewalUnits = null

                        val errorMessage =
                            binding.root.context.getString(R.string.positive_number_error)
                        error = errorMessage
                    } else {
                        element.entry.lifeRenewalUnits = quantity
                        error = null
                    }
                } catch (e: Exception) {
                    element.entry.lifeRenewalUnits = null
                }

                onEntryUpdate()
            }

            setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    updateListener.onRenewalQuantityUpdate(element)
                }
            }
        }

        with(binding) {
            additionalRenewalQuantityLabelStockSurveyElement.setText(
                if (element.useQuantityMultiplier) {
                    R.string.multiplicable_additional_quantity_label
                } else {
                    R.string.additional_quantity
                }
            )
            multiplierHintStockSurveyElement.isVisible = element.useQuantityMultiplier

            minusButtonStockSurveyElement.setOnClickListener {
                setRenewalQuantityBasedOnAdditionalQuantity { renewalQuantity, addtionalQuantity ->
                    renewalQuantity - addtionalQuantity
                }
            }

            plusButtonStockSurveyElement.setOnClickListener {
                setRenewalQuantityBasedOnAdditionalQuantity { renewalQuantity, addtionalQuantity ->
                    renewalQuantity + addtionalQuantity
                }
            }

            renewalUnitStockSurveyElement.text = root.context.getString(
                R.string.renewal_quantity_unit,
                element.unitTobeUsed.toString()
            )
            renewalQuantityLayoutStockSurveyElement.isVisible = element.isRenewalQuantityRequired
            additionalRenewalQuantityLayoutStockSurveyElement.isVisible =
                element.isAdditionalRenewalQuantityRequired
        }
    }

    private fun setRenewalQuantityBasedOnAdditionalQuantity(operation: (Double, Double) -> Double) {
        with(binding) {
            val additionalQuantity = try {
                val value = additionalQuantityEditTextStockSurveyElement.text.toString().toDouble()
                if (element.useQuantityMultiplier) value * 1.3 else value
            } catch (e: NumberFormatException) {
                0.0
            }

            val renewalQuantity = try {
                renewalQuantityEditTextStockSurveyElement.text.toString().toDouble()
            } catch (e: NumberFormatException) {
                0.0
            }

            val value = operation(renewalQuantity, additionalQuantity).roundToInt()

            renewalQuantityEditTextStockSurveyElement.setText(value.toString())
            additionalQuantityEditTextStockSurveyElement.setText("")
        }
    }

    private fun setRepairsView() {
        val element = this.element
        with(binding) {
            setRepairsViewStyle(element.entry.repair)

            repairsButtonStockSurveyElement.setOnClickListener {
                if (element.entry.repair == true) return@setOnClickListener

                element.entry.repair = true
                setRepairsViewStyle(true)
                onEntryUpdate()
            }

            noRepairsButtonStockSurveyElement.setOnClickListener {
                if (element.entry.repair == false) return@setOnClickListener

                element.entry.repair = false
                setRepairsViewStyle(false)
                repairCostEditTextStockSurveyElement.setText("")
                repairDescriptionEditTextStockSurveyElement.setText("")
                onEntryUpdate()
            }

            if (repairCostTextWatcher != null) {
                repairCostEditTextStockSurveyElement.removeTextChangedListener(repairCostTextWatcher)
            }

            repairCostEditTextStockSurveyElement.setText(
                element.entry.repairSpotPrice?.toString() ?: ""
            )

            repairCostTextWatcher = repairCostEditTextStockSurveyElement.addTextChangedListener {
                element.entry.repairSpotPrice = try {
                    it.toString().toInt()
                } catch (e: Exception) {
                    null
                }
                onEntryUpdate()
            }

            repairDescriptionEditTextStockSurveyElement.allowNestedScrolling()
            repairDescriptionEditTextStockSurveyElement.setDoneButton()
            if (repairDescriptionTextWatcher != null) {
                repairDescriptionEditTextStockSurveyElement.removeTextChangedListener(
                    repairDescriptionTextWatcher
                )
            }

            repairDescriptionEditTextStockSurveyElement.setText(element.entry.repairDescription)

            repairDescriptionTextWatcher =
                repairDescriptionEditTextStockSurveyElement.addTextChangedListener {
                    val text = it.toString()
                    element.entry.repairDescription = if (text.length < 4) "" else text
                    onEntryUpdate()
                }

            repairDescriptionEditTextStockSurveyElement.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    val text = repairDescriptionEditTextStockSurveyElement.text.toString()

                    if (text.length < 4 && text.isNotEmpty()) {
                        val errorMessage =
                            binding.root.context.getString(R.string.minimum_character_error)
                        repairDescriptionEditTextStockSurveyElement.error = errorMessage
                    }
                }
            }
        }
    }

    private fun setRepairsViewStyle(hasRepairs: Boolean?) {
        with(binding) {
            repairsButtonStockSurveyElement.setQuestionButtonStyle(hasRepairs == true)
            noRepairsButtonStockSurveyElement.setQuestionButtonStyle(hasRepairs == false)

            repairsViewStockSurveyElement.isVisible = hasRepairs == true
        }
    }

    private fun setPhotoGrid() {
        setPhotosLabel()
        setPhotosViewVisibility()

        with(binding.photosViewStockSurveyElement) {
            setListener(this@ElementViewHolder)
            setPhotos(requireNotNull(element.entry).imagePaths)
            setNoAccessReasons(noAccessReasons, element.entry.noAccessReason)
        }
    }

    private fun setPhotosLabel() {
        val remainingNumberOfPhotos =
            max(0, element.entry.minimumPhotosRequired - element.entry.imagePaths.size)
        binding.photosViewStockSurveyElement.setLabel(remainingNumberOfPhotos)
    }

    private fun setPhotosViewVisibility() {
        binding.photosViewStockSurveyElement.isVisible = element.isPhotoRequired
    }

    private fun setNotesView() {
        with(binding.notesEditTextStockSurveyElement) {
            allowNestedScrolling()
            setDoneButton()
            if (notesTextWatcher != null) {
                removeTextChangedListener(notesTextWatcher)
            }

            setText(element.entry.description)

            notesTextWatcher = addTextChangedListener {
                val text = it.toString()
                element.entry.description = if (text.length < 4) "" else text
                onEntryUpdate()
            }

            setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    val text = text.toString()

                    if (text.length < 4 && text.isNotEmpty()) {
                        val errorMessage =
                            binding.root.context.getString(R.string.minimum_character_error)
                        error = errorMessage
                    }
                }
            }
        }
    }

    override fun onSubElementSelected(
        subElement: StockSurveySubElementModel?,
        isUserSelected: Boolean
    ) {
        if (subElement == null) {
            element.entry.subElement = ""
            element.entry.subElementNumber = null
            element.entry.minimumPhotosRequired = 0

            with(binding) {
                lifeTextStockSurveyElement.text = ""
                extraInfoViewStockSurveyElement.isVisible = false
                notesViewStockSurveyElement.isVisible = false
                photosViewStockSurveyElement.isVisible = false
            }
        } else {
            element.entry.subElement = subElement.title
            element.entry.subElementNumber = subElement.number
            element.entry.minimumPhotosRequired = subElement.minPhotoCount

            if (element.entry.isCloned == null) {
                element.entry.isCloned = false
            }

            setLifeView(subElement)
            if (element.isAsBuiltRequired) updateAsBuilt()
            updateAgeBands()
            updateRenewalBands()
            updateRenewalQuantity()
            if (areRepairsAvailable) updateRepair()
            updateDate(isUserSelected)
            updatePhotos()
            updateNotes()
            setExtraInfoViewVisibility()
        }

        onEntryUpdate()
        updateListener.onSubElementSelected()

        if (isUserSelected && !element.isSpecialEntry) {
            userActionListener.onSubElementSelected(layoutPosition)
        }
    }

    override fun onIsIndividualChange(isIndividual: Boolean?) {
        if (element.entry.isIndividual != isIndividual) {
            element.resetEntry()
            element.entry.isIndividual = isIndividual
            element.entry.isCloned = false
            reset()
        }
    }

    override fun onIsNewChange(isNew: Boolean) {
        if (isNew && element.entry.isCloned == true) {
            element.resetEntry()
            element.entry.isIndividual = false
            element.entry.isCloned = false
            reset()
        } else if (!isNew) {
            updateListener.onPreviousCommunalDataRequest(element, absoluteAdapterPosition)
        }
    }

    override fun onUserEntryChange(userEntry: String) {
        val subElement = element.entry.subElement

        val floatValue = userEntry.toFloatOrNull()

        if (
            subElement.contains(
                "<PositiveNumeric>",
                true
            ) && userEntry.all { c -> c == '0' }
        ) {
            element.entry.subElementUserEntry = ""

            val errorMessage = binding.root.context.getString(R.string.positive_number_error)
            binding.subElementViewStockSurveyElement.setError(errorMessage)
        } else if (
            subElement.contains("<PositiveDecimal>", true) &&
            (floatValue == null || floatValue <= 0.0)
        ) {
            element.entry.subElementUserEntry = ""

            val errorMessage = binding.root.context.getString(R.string.positive_number_error)
            binding.subElementViewStockSurveyElement.setError(errorMessage)
        } else if (
            subElement.contains("<Decimal>", true) &&
            floatValue == null
        ) {
            element.entry.subElementUserEntry = ""

            val errorMessage = binding.root.context.getString(R.string.number_error)
            binding.subElementViewStockSurveyElement.setError(errorMessage)
        } else if (subElement.contains("<text>", true) && userEntry.length < 4) {
            element.entry.subElementUserEntry = ""
        } else {
            element.entry.subElementUserEntry = userEntry
        }

        onEntryUpdate()
    }

    override fun onAddPhoto() {
        updateListener.onAddPhoto(element, absoluteAdapterPosition)
    }

    override fun onRemovePhoto(filePath: String) {
        element.entry.imagePaths.remove(filePath)
        deletePhotoFile(filePath)
        setPhotosLabel()

        onEntryUpdate()
    }

    override fun onNoAccessReasonSelected(reason: String) {
        if (reason != element.entry.noAccessReason) {
            element.entry.noAccessReason = reason
            onEntryUpdate()
        }
    }

    override fun onMonthSelected(month: Month?) {
        if (element.entry.date == null) {
            element.entry.date = Date(month, null)
        } else {
            element.entry.date?.month = month
        }

        onEntryUpdate()
    }

    override fun onYearSelected(year: Year?) {
        if (element.entry.date == null) {
            element.entry.date = Date(null, year)
        } else {
            element.entry.date?.year = year
        }

        onEntryUpdate()
    }

    private fun updateAsBuilt() {
        if (!element.isExtraInfoRequired) {
            element.entry.asBuilt = null
            setAsBuiltViewStyle(null)
        }
    }

    private fun updateAgeBands() {
        if (!element.isExtraInfoRequired) {
            element.entry.existingAgeBand = null

            ageBands.forEach { it.isSelected = false }

            binding.ageBandGridStockSurveyElement.children.forEach {
                (it as BandView).setOptionStyle(false)
            }
        }
    }

    private fun updateRenewalBands() {
        if (!element.isExtraInfoRequired) {
            element.entry.lifeRenewalBand = null

            renewalBands.forEach { it.isSelected = false }

            setRenewalBandButtonStyle()
        } else if (element.isRebuildsEntry) {
            element.entry.lifeRenewalBand = 1

            renewalBands.forEach { it.isSelected = it.lowerBound == 1 }

            setRenewalBandButtonStyle()
        }
    }

    private fun updateRenewalQuantity() {
        if (!element.isExtraInfoRequired || element.isRebuildsEntry) {
            element.entry.lifeRenewalUnits = null
        }

        binding.renewalQuantityEditTextStockSurveyElement.setText(
            element.entry.lifeRenewalUnits?.toString() ?: ""
        )

        binding.renewalQuantityLayoutStockSurveyElement.isVisible =
            element.isRenewalQuantityRequired
    }

    private fun updateRepair() {
        if (!element.isExtraInfoRequired) {
            element.entry.repair = null
            element.entry.repairSpotPrice = null
            element.entry.repairDescription = ""
        } else if (element.isRebuildsEntry) {
            element.entry.repair = false
            element.entry.repairSpotPrice = null
            element.entry.repairDescription = ""
        }

        with(binding) {
            setRepairsViewStyle(element.entry.repair)

            repairCostEditTextStockSurveyElement.setText(
                element.entry.repairSpotPrice?.toString() ?: ""
            )
            repairDescriptionEditTextStockSurveyElement.setText(element.entry.repairDescription)
        }

        setRepairsBasedOnRenewalBand()
    }

    private fun updateDate(isUserSelected: Boolean) {
        if (!element.isDateEntry || isUserSelected) {
            element.entry.date = null
        }

        binding.dateViewStockSurveyElement.isVisible = element.isDateEntry
        binding.dateViewStockSurveyElement.bind(
            dateInputType,
            element.entry.date?.month,
            element.entry.date?.year
        )
    }

    private fun updatePhotos() {
        if (!element.isPhotoRequired) {
            element.entry.imagePaths.clear()
            binding.photosViewStockSurveyElement.resetPhotos()
        }

        setPhotosLabel()
        setPhotosViewVisibility()
    }

    private fun updateNotes() {
        if (!element.isExtraInfoRequired) {
            element.entry.description = ""
            binding.notesEditTextStockSurveyElement.setText("")
        }
    }

    private fun onEntryUpdate() {
        updateListener.onEntryUpdate(element)
        setCompletionStatusView()
    }

    fun expand() {
        binding.contentViewStockSurveyElement.isVisible = true
        binding.containerStockSurveyElement.isClickable = false
    }

    fun collapse() {
        binding.contentViewStockSurveyElement.isVisible = false
        binding.containerStockSurveyElement.isClickable = true
    }

    private fun setEditable() {
        val isEditable = element.entry.isCloned != true

        with(binding) {
            subElementViewStockSurveyElement.setSubElementEditable(isEditable)

            extraInfoViewStockSurveyElement.setAllEnabled(isEditable)
            dateViewStockSurveyElement.setAllEnabled(isEditable)
            photosViewStockSurveyElement.setAllEnabled(isEditable)
        }
    }

    private fun reset() {
        binding.subElementViewStockSurveyElement.setElement(element)
        setLifeView(element.subElements.find { it.number == element.entry.subElementNumber })
        if (element.isAsBuiltRequired) updateAsBuilt()
        updateAgeBands()
        updateRenewalBands()
        updateRenewalQuantity()
        if (areRepairsAvailable) updateRepair()
        updateDate(isUserSelected = true)
        updatePhotos()
        updateNotes()
        setExtraInfoViewVisibility()
        setEditable()
        onEntryUpdate()
    }

    companion object {
        fun get(
            parent: ViewGroup,
            ageBands: List<BandModel>,
            renewalBands: List<BandModel>,
            noAccessReasons: List<String>,
            areRepairsAvailable: Boolean,
        ): ElementViewHolder {
            val inflater = LayoutInflater.from(parent.context)

            return ElementViewHolder(
                ListItemStockSurveyElementBinding.inflate(
                    inflater,
                    parent,
                    false
                ),
                ageBands,
                renewalBands,
                noAccessReasons,
                areRepairsAvailable
            )
        }
    }
}

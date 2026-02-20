package uk.co.savills.stonewood.screen.survey.survey.validation

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import uk.co.savills.stonewood.model.survey.element.validation.ValidationCategory
import uk.co.savills.stonewood.model.survey.element.validation.ValidationOperand
import uk.co.savills.stonewood.model.survey.project.SurveyType
import uk.co.savills.stonewood.repository.element.ValidationElementRepository
import uk.co.savills.stonewood.screen.survey.survey.base.SurveyViewModelBase
import uk.co.savills.stonewood.util.LocationTracker
import uk.co.savills.stonewood.util.getNonNullValue

class ValidationViewModel(
    application: Application,
    locationTracker: LocationTracker
) : SurveyViewModelBase(application, locationTracker) {

    private val _categories = MutableLiveData<List<CategoryPresentationModel>>()
    val categories: LiveData<List<CategoryPresentationModel>>
        get() = _categories

    private val _selectedCategory = MutableLiveData<CategoryPresentationModel>()
    val selectedCategory: LiveData<CategoryPresentationModel>
        get() = _selectedCategory

    private val repository: ValidationElementRepository
        get() = appContainer.validationElementRepository

    fun getValidations() {
        viewModelScope.launch(Dispatchers.IO) {
            val surveys = appState.surveys

            val isStockSurveyPresent = surveys.any { it.type == SurveyType.STOCK }
            val isEnergySurveyPresent = surveys.any { it.type == SurveyType.ENERGY }

            val validationCategories = when {
                isStockSurveyPresent && isEnergySurveyPresent -> listOf(
                    ValidationCategory.E_LOG,
                    ValidationCategory.S_LOG,
                    ValidationCategory.S_E
                )
                isStockSurveyPresent -> listOf(ValidationCategory.S_LOG)
                isEnergySurveyPresent -> listOf(ValidationCategory.E_LOG)
                else -> listOf()
            }

            val elements = repository.getElements(
                project.id,
                appState.currentProperty.UPRN,
                validationCategories
            ).filter {
                it.shouldValidate()
            }

            updateSurveyCompletionStatus(SurveyType.VALIDATION, elements.all { it.isValid })

            val categories = elements
                .groupBy { it.category }
                .map { map ->
                    CategoryPresentationModel(map.key, map.value.groupBy { it.group })
                }

            if (categories.isNotEmpty()) {
                val selectedCategory = selectedCategory.value

                val category = categories.find { it.category == selectedCategory?.category }
                    ?: categories.first()

                category.apply {
                    isSelected = true
                    _selectedCategory.postValue(this)
                }
            }

            _categories.postValue(categories)
        }
    }

    fun onCategorySelected(selected: CategoryPresentationModel) {
        _categories.value = categories.getNonNullValue().map { category ->
            category.copy()
                .apply {
                    if (this.category == selected.category) {
                        this.isSelected = true
                        _selectedCategory.value = this
                    }
                }
        }
    }

    fun onOperandClick(operand: ValidationOperand) {
        elementFind.value = operand.surveyType to operand.elementId
    }
}

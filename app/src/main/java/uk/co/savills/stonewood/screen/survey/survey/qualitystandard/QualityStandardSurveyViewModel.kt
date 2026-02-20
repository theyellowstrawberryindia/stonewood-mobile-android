package uk.co.savills.stonewood.screen.survey.survey.qualitystandard

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import uk.co.savills.stonewood.model.survey.CloseEndedQuestionAnswer
import uk.co.savills.stonewood.model.survey.element.QualityStandardSurveyElementModel
import uk.co.savills.stonewood.model.survey.project.SurveyType
import uk.co.savills.stonewood.screen.survey.survey.base.SurveyViewModelBase
import uk.co.savills.stonewood.util.LocationTracker
import uk.co.savills.stonewood.util.getNonNullValue

class QualityStandardSurveyViewModel(
    application: Application,
    locationTracker: LocationTracker
) : SurveyViewModelBase(application, locationTracker) {

    private val _elements = MutableLiveData<List<QualityStandardSurveyElementModel>>()
    val elements: LiveData<List<QualityStandardSurveyElementModel>>
        get() = _elements

    init {
        viewModelScope.launch(Dispatchers.IO) {
            val elements = appContainer.qualityStandardSurveyElementRepository.getElements()
                .filter { !it.exclude }
            _elements.postValue(elements)
        }
    }

    fun onQuestionAnswered(
        element: QualityStandardSurveyElementModel,
        answer: CloseEndedQuestionAnswer
    ) {
        updateElements(element, answer)

        viewModelScope.launch(Dispatchers.IO) {
            saveEntry(element, answer)
        }.invokeOnCompletion {
            updateSurveyCompletionStatus(
                SurveyType.QUALITY_STANDARD,
                _elements.getNonNullValue().all { it.answer != CloseEndedQuestionAnswer.UNANSWERED }
            )
        }
    }

    private fun updateElements(
        element: QualityStandardSurveyElementModel,
        answer: CloseEndedQuestionAnswer
    ) {
        _elements.value = _elements.getNonNullValue().map {
            if (it.id == element.id) {
                it.copy().apply { it.entry.answer = answer }
            } else {
                it
            }
        }
    }

    private fun saveEntry(
        element: QualityStandardSurveyElementModel,
        answer: CloseEndedQuestionAnswer
    ) {
        appContainer.qualityStandardSurveyElementEntryRepository.insertEntry(
            element.entry.apply {
                this.answer = answer
                this.details = getSurveyDetails(details?.entryInstant)
            }
        )
    }
}

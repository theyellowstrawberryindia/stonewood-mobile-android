package uk.co.savills.stonewood.screen.statistics.ondevice

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import uk.co.savills.stonewood.model.survey.property.PropertyModel
import uk.co.savills.stonewood.model.survey.property.PropertyStatus
import uk.co.savills.stonewood.model.survey.property.PropertySurveyType
import uk.co.savills.stonewood.screen.base.BaseViewModel
import uk.co.savills.stonewood.util.getNonNullValue

class OnDeviceViewModel(application: Application) : BaseViewModel(application) {

    private val _sections = MutableLiveData<List<Pair<String, List<PropertyModel>>>>()
    val sections: LiveData<List<Pair<String, List<PropertyModel>>>>
        get() = _sections

    private val _statistics = MutableLiveData<List<Pair<PropertySurveyType, Int>>>()
    val statistics: LiveData<List<Pair<PropertySurveyType, Int>>>
        get() = _statistics

    init {
        getSections()
    }

    private fun getSections() {
        viewModelScope.launch(Dispatchers.IO) {
            val properties =
                appContainer.propertyRepository.getProperties(appState.currentProject.id).filter {
                    it.status == PropertyStatus.SURVEYED
                }
            val sections = properties.groupBy { it.section }.map { it.key to it.value }

            _sections.postValue(sections)

            _statistics.postValue(
                if (sections.isNotEmpty()) getStatistics(sections.first().second) else listOf()
            )
        }
    }

    fun onSectionSelected(section: String) {
        val selectedSection = sections.getNonNullValue().find { section == it.first } ?: return
        _statistics.value = getStatistics(selectedSection.second)
    }

    private fun getStatistics(properties: List<PropertyModel>): List<Pair<PropertySurveyType, Int>> {
        val p = properties.groupBy {
            it.surveyType
        }

        return listOf(PropertySurveyType.I, PropertySurveyType.IE, PropertySurveyType.ISAP, PropertySurveyType.IESAP, PropertySurveyType.SAP, PropertySurveyType.E, PropertySurveyType.EC).map {
            val quantity = p[it]?.size ?: 0
            it to quantity
        }
    }
}

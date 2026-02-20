package uk.co.savills.stonewood.screen.statistics.projecttotal

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import uk.co.savills.stonewood.model.survey.property.PropertyStatsModel
import uk.co.savills.stonewood.screen.base.BaseViewModel
import uk.co.savills.stonewood.util.getNonNullValue

class ProjectTotalViewModel(application: Application) : BaseViewModel(application) {

    private val _sections = MutableLiveData<List<Pair<String, List<ProjectTotalStats>>>>()
    val sections: LiveData<List<Pair<String, List<ProjectTotalStats>>>>
        get() = _sections

    private val _statistics = MutableLiveData<List<ProjectTotalStats>>()
    val statistics: LiveData<List<ProjectTotalStats>>
        get() = _statistics

    init {
        getSections()
    }

    private fun getSections() {
        viewModelScope.launch(Dispatchers.IO) {
            val sections = getSections(getPropertyStats())

            _sections.postValue(sections)

            _statistics.postValue(
                if (sections.isNotEmpty()) {
                    sections.first().second
                } else {
                    listOf()
                }
            )
        }
    }

    private fun getPropertyStats(): List<PropertyStatsModel> {
        val properties =
            appContainer.propertyRepository.getProperties(appState.currentProject.id)

        val currentPropertyStats = properties.map { it.toStatsModel() }
        val recordedPropertyStats =
            appContainer.propertyStatsRepository.getStats(appState.currentProject.id)
                .toMutableList()

        currentPropertyStats.forEach { stats ->
            recordedPropertyStats.removeIf { it.uprn == stats.uprn }
            recordedPropertyStats.add(stats)
        }

        return recordedPropertyStats
    }

    private fun getSections(propertyStats: List<PropertyStatsModel>): MutableList<Pair<String, List<ProjectTotalStats>>> {
        val sectionSeparated = propertyStats.groupBy { it.section }

        val sections = mutableListOf<Pair<String, List<ProjectTotalStats>>>()

        sectionSeparated.forEach { (section, propertyStats) ->
            val strataSeparated = propertyStats.groupBy { it.strata }

            val stats = strataSeparated.map { (strata, propertyStats) ->
                val required = propertyStats.count { it.isRequired }
                val achieved = propertyStats.count { it.isComplete }

                ProjectTotalStats(
                    strata,
                    required,
                    achieved
                )
            }

            sections.add(
                section to stats
            )
        }
        return sections
    }

    fun onSectionSelected(section: String) {
        val selectedSection = sections.getNonNullValue().find { section == it.first } ?: return
        val stats = selectedSection.second
        _statistics.value = stats
    }
}

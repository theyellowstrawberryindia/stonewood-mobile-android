package uk.co.savills.stonewood.screen.survey.survey.base

import android.app.Application
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import uk.co.savills.stonewood.domain.hhsrs.SevereIssueService
import uk.co.savills.stonewood.model.survey.entry.SurveyElementEntryDetailsModel
import uk.co.savills.stonewood.model.survey.project.SurveyModel
import uk.co.savills.stonewood.model.survey.project.SurveyType
import uk.co.savills.stonewood.model.survey.property.PropertyStatus
import uk.co.savills.stonewood.model.survey.property.PropertySurveyStatus
import uk.co.savills.stonewood.screen.base.BaseViewModel
import uk.co.savills.stonewood.service.HHSRSSevereIssueService
import uk.co.savills.stonewood.service.PropertyServerStatusReporter
import uk.co.savills.stonewood.util.LocationTracker
import uk.co.savills.stonewood.util.SingleLiveEvent
import java.time.Instant

abstract class SurveyViewModelBase(
    application: Application,
    protected val locationTracker: LocationTracker,
) : BaseViewModel(application), SurveyViewModel {

    protected val project
        get() = appContainer.appState.currentProject

    protected val property = appContainer.appState.currentProperty

    override val surveyUpdated = SingleLiveEvent<Nothing?>()
    override val elementFind = SingleLiveEvent<Pair<SurveyType, Int>>()

    protected val issueService: SevereIssueService = HHSRSSevereIssueService(
        application,
        appContainer.hhsrsSevereIssueRepository
    )

    private var isPropertySurveyInProgress = false

    protected fun getSurveyDetails(entryInstant: Instant? = null): SurveyElementEntryDetailsModel {
        val instant = Instant.now()

        return SurveyElementEntryDetailsModel(
            property.UPRN,
            entryInstant ?: instant,
            instant
        )
    }

    fun updateSurveyCompletionStatus(type: SurveyType, isComplete: Boolean) {
        val currentProject = appState.currentProject
        val wasSurveyed = property.status == PropertyStatus.SURVEYED
        val wasInProgress = isPropertySurveyInProgress

        viewModelScope.launch(Dispatchers.IO) {
            property.surveyStatus.setCompletionStatus(type, isComplete)

            val surveys = appState.surveys
            val validations = appContainer.validationElementRepository.getElements(appState.currentProject.id, appState.currentProperty.UPRN)

            if (validations.any() && type == SurveyType.STOCK || type == SurveyType.ENERGY) {
                property.surveyStatus.setCompletionStatus(
                    SurveyType.VALIDATION,
                    validations.filter { it.shouldValidate() }.all { it.isValid }
                )
            }

            property.surveyStatus.evaluateRequiredSurveysCompletionStatus(surveys)
            isPropertySurveyInProgress = isPropertyInProgress(surveys)

            if (property.location.latitude == 0.0 && property.location.longitude == 0.0) {
                property.location = locationTracker.getCurrentLocation()
            }
            appContainer.propertyRepository.updateProperty(property)

            appContainer.dataBackUpService.backupData(appState.currentProject.id)
        }.invokeOnCompletion {
            if (property.status == PropertyStatus.SURVEYED) {
                issueService.reportIssues()
            }

            if (!wasSurveyed && property.status == PropertyStatus.SURVEYED) {
                appContainer.eventReportingService.reportPropertySurveyed(property.UPRN)

                PropertyServerStatusReporter.report(
                    application,
                    currentProject.id,
                    property.id,
                    PropertySurveyStatus.FINISHED
                )
            } else if (!wasInProgress && isPropertySurveyInProgress) {
                PropertyServerStatusReporter.report(
                    application,
                    currentProject.id,
                    property.id,
                    PropertySurveyStatus.IN_PROGRESS
                )
            }

            surveyUpdated.call()
        }
    }

    private fun isPropertyInProgress(surveys: List<SurveyModel>): Boolean {
        if (property.status == PropertyStatus.SURVEYED) return false

        for (survey in surveys) {
            when (survey.type) {
                SurveyType.RISK_ASSESSMENT -> {
                    return property.surveyStatus.isRiskAssessmentSurveyComplete
                }

                SurveyType.QUALITY_STANDARD -> {
                    val entries =
                        appContainer.qualityStandardSurveyElementEntryRepository.getPropertyEntries(
                            property.UPRN
                        )

                    if (entries.any()) return true
                }

                SurveyType.HHSRS -> {
                    val entries =
                        appContainer.hhsrsSurveyElementEntryRepository.getPropertyEntries(property.UPRN)

                    if (entries.any()) return true
                }

                SurveyType.ENERGY -> {
                    val entries =
                        appContainer.energySurveyElementEntryRepository.getEntries(property.UPRN)

                    if (entries.any()) return true
                }

                SurveyType.STOCK -> {
                    val entries =
                        appContainer.stockStandardSurveyElementEntryRepository.getEntries(property.UPRN)

                    if (entries.any()) return true
                }

                SurveyType.VALIDATION -> if (property.surveyStatus.isValidationComplete) return true
            }
        }

        return false
    }
}

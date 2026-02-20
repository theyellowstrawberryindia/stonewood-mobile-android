package uk.co.savills.stonewood.repository.element

import android.annotation.SuppressLint
import androidx.annotation.WorkerThread
import uk.co.savills.stonewood.AppState
import uk.co.savills.stonewood.model.survey.CloseEndedQuestionAnswer
import uk.co.savills.stonewood.model.survey.element.RiskAssessmentSurveyElementModel
import uk.co.savills.stonewood.model.survey.entry.RiskAssessmentSurveyElementEntryModel
import uk.co.savills.stonewood.repository.entry.SurveyElementEntryRepository
import uk.co.savills.stonewood.storage.db.dao.element.RiskAssessmentSurveyElementDao
import uk.co.savills.stonewood.storage.db.dao.entry.RiskAssessmentSurveyElementEntryDao
import uk.co.savills.stonewood.storage.db.entity.entry.RiskAssessmentSurveyElementEntryEntity
import uk.co.savills.stonewood.util.mapper.mapToEntity
import uk.co.savills.stonewood.util.mapper.mapToModel

@SuppressLint("InvalidMethodName")
class RiskAssessmentSurveyElementRepository(
    private val elementDao: RiskAssessmentSurveyElementDao,
    private val elementEntryRepository: SurveyElementEntryRepository<RiskAssessmentSurveyElementEntryEntity, RiskAssessmentSurveyElementEntryModel, RiskAssessmentSurveyElementEntryDao>,
    private val appState: AppState,
) {
    private val projectId
        get() = appState.currentProject.id

    private val propertyUPRN
        get() = appState.currentProperty.UPRN

    @WorkerThread
    fun getElements(): List<RiskAssessmentSurveyElementModel> {
        return elementDao.getElements(projectId).map { elementEntity ->
            val entry = elementEntryRepository.getEntry(projectId, propertyUPRN, elementEntity.id)

            mapToModel(elementEntity).apply {
                this.entry = entry ?: RiskAssessmentSurveyElementEntryModel(
                    elementEntity.id,
                    elementEntity.question,
                    CloseEndedQuestionAnswer.UNANSWERED
                )
            }
        }
    }

    @WorkerThread
    fun insertElements(elements: List<RiskAssessmentSurveyElementModel>) {
        elementDao.insertElements(
            elements.map { mapToEntity(it, projectId) }
        )
    }

    @WorkerThread
    fun clearElements() = elementDao.clearElements(projectId)

    @WorkerThread
    fun clearProjectElements(ids: List<String>) = elementDao.clearProjectElements(ids)

    @WorkerThread
    fun clearAll() = elementDao.clearAll()
}

package uk.co.savills.stonewood.repository.element

import android.annotation.SuppressLint
import androidx.annotation.WorkerThread
import uk.co.savills.stonewood.AppState
import uk.co.savills.stonewood.model.survey.CloseEndedQuestionAnswer
import uk.co.savills.stonewood.model.survey.element.QualityStandardSurveyElementModel
import uk.co.savills.stonewood.model.survey.entry.QualityStandardSurveyElementEntryModel
import uk.co.savills.stonewood.repository.entry.SurveyElementEntryRepository
import uk.co.savills.stonewood.storage.db.dao.element.QualityStandardSurveyElementDao
import uk.co.savills.stonewood.storage.db.dao.entry.QualityStandardSurveyElementEntryDao
import uk.co.savills.stonewood.storage.db.entity.entry.QualityStandardSurveyElementEntryEntity
import uk.co.savills.stonewood.util.mapper.mapToEntity
import uk.co.savills.stonewood.util.mapper.mapToModel

@SuppressLint("InvalidMethodName")
class QualityStandardSurveyElementRepository(
    private val elementDao: QualityStandardSurveyElementDao,
    private val elementEntryRepository: SurveyElementEntryRepository<QualityStandardSurveyElementEntryEntity, QualityStandardSurveyElementEntryModel, QualityStandardSurveyElementEntryDao>,
    private val appState: AppState,
) {
    private val projectId
        get() = appState.currentProject.id

    private val propertyUPRN
        get() = appState.currentProperty.UPRN

    @WorkerThread
    fun getElements(): List<QualityStandardSurveyElementModel> {
        return elementDao.getElements(projectId).map { elementEntity ->
            val entry = elementEntryRepository.getEntry(projectId, propertyUPRN, elementEntity.id)

            mapToModel(elementEntity).apply {
                this.entry = entry ?: QualityStandardSurveyElementEntryModel(
                    id,
                    question,
                    CloseEndedQuestionAnswer.UNANSWERED
                )
            }
        }
    }

    @WorkerThread
    fun insertElements(elements: List<QualityStandardSurveyElementModel>) {
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

package uk.co.savills.stonewood.repository.element

import androidx.annotation.WorkerThread
import uk.co.savills.stonewood.model.survey.element.Validatable
import uk.co.savills.stonewood.model.survey.element.validation.ValidationCategory
import uk.co.savills.stonewood.model.survey.element.validation.ValidationElementModel
import uk.co.savills.stonewood.model.survey.element.validation.ValidationOperand
import uk.co.savills.stonewood.model.survey.project.SurveyType
import uk.co.savills.stonewood.storage.db.dao.element.ValidationElementDao
import uk.co.savills.stonewood.util.mapper.mapToEntity
import uk.co.savills.stonewood.util.mapper.mapToModel

class ValidationElementRepository(
    val dao: ValidationElementDao,
    private val stockSurveyElementRepository: StockSurveyElementRepository,
    private val energySurveyElementRepository: EnergySurveyElementRepository
) {
    @WorkerThread
    fun getElements(
        projectId: String,
        propertyUPRN: String
    ): List<ValidationElementModel> {
        return dao.get(projectId).map { entity ->
            mapToModel(entity).also {
                it.leftOperand.element = getElement(projectId, propertyUPRN, it.leftOperand)
                it.rightOperand.element = getElement(projectId, propertyUPRN, it.rightOperand)
            }
        }.filter {
            it.leftOperand.element != null && it.rightOperand.element != null
        }
    }

    @WorkerThread
    fun getElements(
        projectId: String,
        propertyUPRN: String,
        categories: List<ValidationCategory>
    ): List<ValidationElementModel> {
        return dao.get(projectId, categories.map { it.ordinal }).map { entity ->
            mapToModel(entity).also {
                it.leftOperand.element = getElement(projectId, propertyUPRN, it.leftOperand)
                it.rightOperand.element = getElement(projectId, propertyUPRN, it.rightOperand)
            }
        }.filter {
            it.leftOperand.element != null && it.rightOperand.element != null
        }
    }

    private fun getElement(
        projectId: String,
        propertyUPRN: String,
        operand: ValidationOperand
    ): Validatable? {
        return if (operand.surveyType == SurveyType.STOCK) {
            stockSurveyElementRepository.getElement(projectId, operand.elementTitle, propertyUPRN)
        } else {
            energySurveyElementRepository.getElement(projectId, operand.elementTitle, propertyUPRN)
        }
    }

    @WorkerThread
    fun insertElements(
        projectId: String,
        elements: List<ValidationElementModel>,
        propertyUPRN: String? = null
    ) {
        val validElements = elements.filter {
            val leftElement = if (it.leftOperand.surveyType == SurveyType.STOCK) {
                stockSurveyElementRepository.getElement(
                    projectId,
                    it.leftOperand.elementTitle,
                    propertyUPRN
                )
            } else {
                energySurveyElementRepository.getElement(
                    projectId,
                    it.leftOperand.elementTitle,
                    propertyUPRN
                )
            }

            val rightElement = if (it.rightOperand.surveyType == SurveyType.STOCK) {
                stockSurveyElementRepository.getElement(
                    projectId,
                    it.rightOperand.elementTitle,
                    propertyUPRN
                )
            } else {
                energySurveyElementRepository.getElement(
                    projectId,
                    it.rightOperand.elementTitle,
                    propertyUPRN
                )
            }

            leftElement != null && rightElement != null
        }

        dao.insert(validElements.map { mapToEntity(it, projectId) })
    }

    @WorkerThread
    fun clearElements(projectId: String) = dao.clear(projectId)

    @WorkerThread
    fun clearProjectElements(ids: List<String>) = dao.clearProjectElements(ids)

    @WorkerThread
    fun clearAll() = dao.clearAll()
}

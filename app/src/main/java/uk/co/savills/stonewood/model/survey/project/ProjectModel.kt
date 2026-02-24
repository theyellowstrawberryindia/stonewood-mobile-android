package uk.co.savills.stonewood.model.survey.project

import uk.co.savills.stonewood.model.survey.property.PropertySurveyType

data class ProjectModel(
    val id: String,
    val name: String,
    val description: String,
    val surveys: List<SurveyModel>,
    val isExternalOnlyType: Boolean,
    val areRepairsAvailable: Boolean,
    val numberOfSharedExternalPhotos: Int,
    val isClosed: Boolean
) {
    fun getSurveys(propertySurveyType: PropertySurveyType): List<SurveyModel> {
        return when (propertySurveyType) {
            PropertySurveyType.I -> surveys.filterNot { it.type == SurveyType.ENERGY }
            PropertySurveyType.E -> surveys.filter { it.type == SurveyType.STOCK }
            PropertySurveyType.IE -> surveys.filterNot { it.type == SurveyType.ENERGY }
            PropertySurveyType.EC -> surveys.filter { it.type == SurveyType.STOCK }
            PropertySurveyType.IESAP -> surveys
            PropertySurveyType.ISAP -> surveys
            PropertySurveyType.SAP -> surveys.filter { it.type == SurveyType.ENERGY }
        }
    }
}

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

//{
//        "name": "swapTestProject",
//        "description": null,
//        "riskAssessmentSurvey": true,
//        "riskAssessmentSurveyName": null,
//        "qualityStandardSurvey": true,
//        "qualityStandardSurveyName": null,
//        "energySurveyName": null,
//        "stockConditionSurvey": true,
//        "stockConditionSurveyName": null,
//        "hhsrsSurvey": true,
//        "hhsrsSurveyName": null,
//        "externalOnlyAvailable": false,
//        "stockSurveyExcelSynced": true,
//        "id": 3,
//        "createdAt": "2025-07-14T10:29:04.082804Z",
//        "updatedAt": "2025-07-14T10:29:04.082804Z"
//    }
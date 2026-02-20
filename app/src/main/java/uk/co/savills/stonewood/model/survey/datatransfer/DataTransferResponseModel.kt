package uk.co.savills.stonewood.model.survey.datatransfer

import uk.co.savills.stonewood.model.survey.BandModel
import uk.co.savills.stonewood.model.survey.HHSRSLocationModel
import uk.co.savills.stonewood.model.survey.PhotoNoAccessReasonModel
import uk.co.savills.stonewood.model.survey.element.HHSRSSurveyElementModel
import uk.co.savills.stonewood.model.survey.element.QualityStandardSurveyElementModel
import uk.co.savills.stonewood.model.survey.element.RiskAssessmentSurveyElementModel
import uk.co.savills.stonewood.model.survey.element.StockSurveyElementModel
import uk.co.savills.stonewood.model.survey.element.energy.EnergySurveyElementModel
import uk.co.savills.stonewood.model.survey.element.validation.ValidationElementModel
import uk.co.savills.stonewood.model.survey.entry.CommunalDataModel
import uk.co.savills.stonewood.model.survey.entry.ExtBlockPhotoModel
import uk.co.savills.stonewood.model.survey.project.ProjectModel
import uk.co.savills.stonewood.model.survey.property.PropertyModel
import uk.co.savills.stonewood.model.survey.property.PropertyStatsModel

data class DataTransferResponseModel(
    val project: ProjectModel,
    val properties: List<PropertyModel>,
    val noAccessReasons: List<String>,
    val hhsrsLocations: List<HHSRSLocationModel>,
    val ageBands: List<BandModel>,
    val renewalBands: List<BandModel>,
    val qualityStandardSurveyElements: List<QualityStandardSurveyElementModel>,
    val hhsrsSurveyElements: List<HHSRSSurveyElementModel>,
    val riskAssessmentSurveyElements: List<RiskAssessmentSurveyElementModel>,
    val energySurveyElements: List<EnergySurveyElementModel>,
    val stockSurveyElements: List<StockSurveyElementModel>,
    val validationElements: List<ValidationElementModel>,
    val photoNoAccessReasons: List<PhotoNoAccessReasonModel>,
    val communalData: List<CommunalDataModel>,
    val extBlockPhotos: List<ExtBlockPhotoModel>,
    val projectStatistics: List<PropertyStatsModel>
)

package uk.co.savills.stonewood.model.survey.datatransfer

import android.annotation.SuppressLint
import uk.co.savills.stonewood.model.survey.entry.CommunalDataModel
import uk.co.savills.stonewood.model.survey.entry.EnergySurveyElementEntryModel
import uk.co.savills.stonewood.model.survey.entry.ExtBlockPhotoModel
import uk.co.savills.stonewood.model.survey.entry.HHSRSSurveyElementEntryModel
import uk.co.savills.stonewood.model.survey.entry.QualityStandardSurveyElementEntryModel
import uk.co.savills.stonewood.model.survey.entry.RiskAssessmentSurveyElementEntryModel
import uk.co.savills.stonewood.model.survey.entry.StockSurveyElementEntryModel
import uk.co.savills.stonewood.model.survey.noaccess.NoAccessEntryModel
import uk.co.savills.stonewood.model.survey.property.PropertyModel
import java.time.Instant

@SuppressLint("InvalidMethodName")
data class DataTransferRequestModel(
    val propertyEntries: List<PropertyModel>,
    val energySurveyEntries: List<EnergySurveyElementEntryModel>,
    val HHSRSSurveyEntries: List<HHSRSSurveyElementEntryModel>,
    val qualityStandardSurveyEntries: List<QualityStandardSurveyElementEntryModel>,
    val riskAssessmentSurveyEntries: List<RiskAssessmentSurveyElementEntryModel>,
    val stockSurveyEntries: List<StockSurveyElementEntryModel>,
    val noAccessEntries: List<NoAccessEntryModel>,
    val communalData: List<CommunalDataModel>,
    val extBlockPhotos: List<ExtBlockPhotoModel>,
    val alteration: AlterationModel
) {
    lateinit var syncStartTime: Instant
}

package uk.co.savills.stonewood.network.dto.datatransfer

import android.annotation.SuppressLint
import com.squareup.moshi.JsonClass
import uk.co.savills.stonewood.network.dto.datatransfer.entries.CommunalDataDto
import uk.co.savills.stonewood.network.dto.datatransfer.entries.EnergySurveyElementEntryDto
import uk.co.savills.stonewood.network.dto.datatransfer.entries.ExtBlockPhotoDto
import uk.co.savills.stonewood.network.dto.datatransfer.entries.HHSRSSurveyElementEntryDto
import uk.co.savills.stonewood.network.dto.datatransfer.entries.NoAccessEntryDto
import uk.co.savills.stonewood.network.dto.datatransfer.entries.QualityStandardSurveyElementEntryDto
import uk.co.savills.stonewood.network.dto.datatransfer.entries.RiskAssessmentSurveyElementEntryDto
import uk.co.savills.stonewood.network.dto.datatransfer.entries.StockSurveyElementEntryDto

@SuppressLint("InvalidMethodName")
@JsonClass(generateAdapter = true)
data class DataTransferRequestDto(
    val surveyorId: Int,
    val surveyDataEnergyList: List<EnergySurveyElementEntryDto>,
    val surveyDataHHSRSList: List<HHSRSSurveyElementEntryDto>,
    val surveyDataQualityStandardList: List<QualityStandardSurveyElementEntryDto>,
    val surveyDataRiskAssessmentList: List<RiskAssessmentSurveyElementEntryDto>,
    val surveyDataStockList: List<StockSurveyElementEntryDto>,
    val propertyAddresses: List<PropertyDto>,
    val noAccesses: List<NoAccessEntryDto>,
    val communalDataLookupList: List<CommunalDataDto>,
    val externalEnergyPhotoLookupList: List<ExtBlockPhotoDto>,
    val syncStartTime: String,
    val alteration: AlterationDto,
)

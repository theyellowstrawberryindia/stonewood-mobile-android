package uk.co.savills.stonewood.network.dto.datatransfer

import android.annotation.SuppressLint
import com.squareup.moshi.JsonClass
import uk.co.savills.stonewood.network.dto.ProjectDto
import uk.co.savills.stonewood.network.dto.datatransfer.elements.EnergySurveyElementDto
import uk.co.savills.stonewood.network.dto.datatransfer.elements.HHSRSSurveyElementDto
import uk.co.savills.stonewood.network.dto.datatransfer.elements.QualityStandardSurveyElementDto
import uk.co.savills.stonewood.network.dto.datatransfer.elements.RiskAssessmentSurveyElementDto
import uk.co.savills.stonewood.network.dto.datatransfer.elements.StockSurveyElementDto
import uk.co.savills.stonewood.network.dto.datatransfer.elements.ValidationElementDto
import uk.co.savills.stonewood.network.dto.datatransfer.entries.CommunalDataDto
import uk.co.savills.stonewood.network.dto.datatransfer.entries.ExtBlockPhotoDto

@SuppressLint("InvalidMethodName")
@JsonClass(generateAdapter = true)
data class DataTransferResponseDto(
    val surveyProjectConfiguration: ProjectDto,
    val propertyAddresses: List<PropertyDto>,
    val noAccessReasons: List<NoAccessReasonDto>,
    val hhsrsLocations: List<HHSRSLocationDto>,
    val ageBands: List<AgeBandDto>,
    val renewalBands: List<RenewalBandDto>,
    val surveyDesignQualityStandardList: List<QualityStandardSurveyElementDto>,
    val surveyDesignHHSRSList: List<HHSRSSurveyElementDto>,
    val surveyDesignRiskAssessmentList: List<RiskAssessmentSurveyElementDto>,
    val energyQuestionList: List<EnergySurveyElementDto>,
    val stockQuestionList: List<StockSurveyElementDto>,
    val validationList: List<ValidationElementDto>?,
    val noPhotoReasons: List<PhotoNoAccessReasonDto>,
    val communalDataLookupList: List<CommunalDataDto>,
    val externalEnergyPhotoLookupList: List<ExtBlockPhotoDto>,
    val projectStatisticList: List<PropertyStatsDto>
)

package uk.co.savills.stonewood.util.mapper

import uk.co.savills.stonewood.model.AppVersionModel
import uk.co.savills.stonewood.model.AuthTokenModel
import uk.co.savills.stonewood.model.LocationModel
import uk.co.savills.stonewood.model.UserModel
import uk.co.savills.stonewood.model.survey.BandModel
import uk.co.savills.stonewood.model.survey.EmailAttachmentModel
import uk.co.savills.stonewood.model.survey.HHSRSLocationModel
import uk.co.savills.stonewood.model.survey.HHSRSSevereIssueModel
import uk.co.savills.stonewood.model.survey.ImageRequestModel
import uk.co.savills.stonewood.model.survey.PhotoNoAccessReasonModel
import uk.co.savills.stonewood.model.survey.PropertyLocationType
import uk.co.savills.stonewood.model.survey.StockSurveyType
import uk.co.savills.stonewood.model.survey.datatransfer.AlterationModel
import uk.co.savills.stonewood.model.survey.datatransfer.DataTransferRequestModel
import uk.co.savills.stonewood.model.survey.datatransfer.DataTransferResponseModel
import uk.co.savills.stonewood.model.survey.element.HHSRSSurveyElementModel
import uk.co.savills.stonewood.model.survey.element.QualityStandardSurveyElementModel
import uk.co.savills.stonewood.model.survey.element.RiskAssessmentElementType
import uk.co.savills.stonewood.model.survey.element.RiskAssessmentSurveyElementModel
import uk.co.savills.stonewood.model.survey.element.StockSurveyElementModel
import uk.co.savills.stonewood.model.survey.element.StockSurveySubElementModel
import uk.co.savills.stonewood.model.survey.element.energy.EnergySurveyElementModel
import uk.co.savills.stonewood.model.survey.element.energy.EnergySurveyElementType
import uk.co.savills.stonewood.model.survey.element.energy.EnergySurveySubElementModel
import uk.co.savills.stonewood.model.survey.element.validation.ValidationCategory
import uk.co.savills.stonewood.model.survey.element.validation.ValidationElementModel
import uk.co.savills.stonewood.model.survey.element.validation.ValidationOperand
import uk.co.savills.stonewood.model.survey.element.validation.ValidationOperator
import uk.co.savills.stonewood.model.survey.entry.CommunalDataModel
import uk.co.savills.stonewood.model.survey.entry.EnergySurveyElementEntryModel
import uk.co.savills.stonewood.model.survey.entry.ExtBlockPhotoModel
import uk.co.savills.stonewood.model.survey.entry.HHSRSSurveyElementEntryModel
import uk.co.savills.stonewood.model.survey.entry.QualityStandardSurveyElementEntryModel
import uk.co.savills.stonewood.model.survey.entry.RiskAssessmentSurveyElementEntryModel
import uk.co.savills.stonewood.model.survey.entry.StockSurveyElementEntryModel
import uk.co.savills.stonewood.model.survey.noaccess.NoAccessEntryModel
import uk.co.savills.stonewood.model.survey.project.ProjectModel
import uk.co.savills.stonewood.model.survey.project.SurveyModel
import uk.co.savills.stonewood.model.survey.project.SurveyType
import uk.co.savills.stonewood.model.survey.property.AddressModel
import uk.co.savills.stonewood.model.survey.property.ContactModel
import uk.co.savills.stonewood.model.survey.property.PropertyModel
import uk.co.savills.stonewood.model.survey.property.PropertyStatsModel
import uk.co.savills.stonewood.model.survey.property.PropertySurveyType
import uk.co.savills.stonewood.network.dto.AppVersionDto
import uk.co.savills.stonewood.network.dto.EmailAttachmentDto
import uk.co.savills.stonewood.network.dto.HHSRSSevereIssueDto
import uk.co.savills.stonewood.network.dto.ImageRequestDto
import uk.co.savills.stonewood.network.dto.ProjectDto
import uk.co.savills.stonewood.network.dto.UserDto
import uk.co.savills.stonewood.network.dto.datatransfer.AgeBandDto
import uk.co.savills.stonewood.network.dto.datatransfer.AlterationDto
import uk.co.savills.stonewood.network.dto.datatransfer.DataTransferRequestDto
import uk.co.savills.stonewood.network.dto.datatransfer.DataTransferResponseDto
import uk.co.savills.stonewood.network.dto.datatransfer.HHSRSLocationDto
import uk.co.savills.stonewood.network.dto.datatransfer.PhotoNoAccessReasonDto
import uk.co.savills.stonewood.network.dto.datatransfer.PropertyDto
import uk.co.savills.stonewood.network.dto.datatransfer.PropertyStatsDto
import uk.co.savills.stonewood.network.dto.datatransfer.RenewalBandDto
import uk.co.savills.stonewood.network.dto.datatransfer.elements.EnergySurveyElementDto
import uk.co.savills.stonewood.network.dto.datatransfer.elements.EnergySurveySubElementDto
import uk.co.savills.stonewood.network.dto.datatransfer.elements.HHSRSSurveyElementDto
import uk.co.savills.stonewood.network.dto.datatransfer.elements.QualityStandardSurveyElementDto
import uk.co.savills.stonewood.network.dto.datatransfer.elements.RiskAssessmentSurveyElementDto
import uk.co.savills.stonewood.network.dto.datatransfer.elements.StockSurveyElementDto
import uk.co.savills.stonewood.network.dto.datatransfer.elements.StockSurveySubElementDto
import uk.co.savills.stonewood.network.dto.datatransfer.elements.ValidationElementDto
import uk.co.savills.stonewood.network.dto.datatransfer.entries.CommunalDataDto
import uk.co.savills.stonewood.network.dto.datatransfer.entries.EnergySurveyElementEntryDto
import uk.co.savills.stonewood.network.dto.datatransfer.entries.ExtBlockPhotoDto
import uk.co.savills.stonewood.network.dto.datatransfer.entries.HHSRSSurveyElementEntryDto
import uk.co.savills.stonewood.network.dto.datatransfer.entries.NoAccessEntryDto
import uk.co.savills.stonewood.network.dto.datatransfer.entries.QualityStandardSurveyElementEntryDto
import uk.co.savills.stonewood.network.dto.datatransfer.entries.RiskAssessmentSurveyElementEntryDto
import uk.co.savills.stonewood.network.dto.datatransfer.entries.StockSurveyElementEntryDto
import uk.co.savills.stonewood.util.photo.getBase64StringFromFile
import java.io.File
import java.time.Instant
import java.util.Locale
import kotlin.text.split as splitOrValue

fun mapToModel(appVersion: AppVersionDto): AppVersionModel {
    return with(appVersion) {
        AppVersionModel(
            minimumCompatibleAppVersion,
            latestAppVersion
        )
    }
}

fun mapToModel(user: UserDto): UserModel {
    return with(user) {
        UserModel(
            id,
            firstName,
            lastName,
            userName,
            workEmail,
            !defaultPasswordChanged,
            AuthTokenModel(token, refreshToken, expiryDateTime)
        )
    }
}

fun mapToModel(project: ProjectDto): ProjectModel {
    return with(project) {
        val surveys = mutableListOf<SurveyModel>()

        if (riskAssessmentSurvey) {
            surveys.add(SurveyModel(SurveyType.RISK_ASSESSMENT, riskAssessmentSurveyName))
        }

        if (qualityStandardSurvey) {
            surveys.add(SurveyModel(SurveyType.QUALITY_STANDARD, qualityStandardSurveyName))
        }

        if (stockConditionSurvey) {
            surveys.add(SurveyModel(SurveyType.STOCK, stockConditionSurveyName))
        }

        if (hhsrsSurvey) {
            surveys.add(SurveyModel(SurveyType.HHSRS, hhsrsSurveyName))
        }

        surveys.add(SurveyModel(SurveyType.ENERGY, energySurveyName))

        ProjectModel(
            id,
            name,
            description.orEmpty(),
            surveys,
            externalOnlyAvailable,
            isRepairsAvailable,
            numberOfExternalBlockPhotos,
            isClosed
        )
    }
}

fun mapToModel(dataTransferResponse: DataTransferResponseDto): DataTransferResponseModel {
    return with(dataTransferResponse) {
        DataTransferResponseModel(
            mapToModel(surveyProjectConfiguration),
            propertyAddresses.map { mapToModel(it) },
            noAccessReasons.map { it.reason },
            hhsrsLocations.map(::mapToModel),
            ageBands.map(::mapToModel),
            renewalBands.map(::mapToModel),
            surveyDesignQualityStandardList.map(::mapToModel),
            surveyDesignHHSRSList.map(::mapToModel),
            surveyDesignRiskAssessmentList.map(::mapToModel),
            energyQuestionList.map(::mapToModel),
            stockQuestionList.map(::mapToModel),
            validationList?.map(::mapToModel).orEmpty(),
            noPhotoReasons.map(::mapToModel),
            communalDataLookupList.map(::mapToModel),
            externalEnergyPhotoLookupList.map(::mapToModel),
            projectStatisticList.map(::mapToModel)
        )
    }
}

fun mapToModel(property: PropertyDto): PropertyModel {
    return with(property) {
        PropertyModel(
            id,
            if (order.isNullOrBlank()) 0 else order.toInt(),
            uprn,
            ta,
            strata.orEmpty(),
            AddressModel(
                number.orEmpty(),
                address1,
                address2.orEmpty(),
                address3.orEmpty(),
                address4.orEmpty(),
                postCode.orEmpty()
            ),
            PropertySurveyType.valueOf(originalSurveyType),
            PropertySurveyType.valueOf(surveyType),
            section,
            ContactModel(
                contactNumber1.orEmpty(),
                contactNumber2.orEmpty(),
                contactNotes.orEmpty()
            ),
            frontDoorPhoto.orEmpty(),
            LocationModel(latitude, longitude),
            extBlockPhotos = extBlockElevationPhotos?.split().orEmpty().toMutableList(),
            extPhotosClonedFrom = extBlockElevationPhotosClonedFromUPRN.orEmpty(),
            hasExternalPhoto = hasExternalPhoto,
            isDeleted = isDeleted,
            createdAt = createdAt?.toInstant(),
            updatedAt = updatedAt?.toInstant()
        )
    }
}

fun mapToDto(entry: PropertyModel): PropertyDto {
    return with(entry) {
        PropertyDto(
            id,
            order.toString(),
            UPRN,
            TA,
            strata,
            address.number,
            address.line1,
            address.line2,
            address.line3,
            address.line4,
            address.postalCode,
            surveyTypeOriginal.toString(),
            surveyType.toString(),
            section,
            contact.number,
            contact.numberSecondary,
            contact.notes,
            File(frontDoorPhoto).name,
            extBlockPhotos.map { filePath ->
                File(filePath).name
            }.join(),
            extPhotosClonedFrom,
            hasExternalPhoto,
            location.latitude,
            location.longitude,
            isDeleted,
            createdAt?.toString(),
            updatedAt?.toString()
        )
    }
}

fun mapToModel(location: HHSRSLocationDto): HHSRSLocationModel {
    return with(location) {
        HHSRSLocationModel(
            id,
            locationName,
            PropertyLocationType.from(locationType)
        )
    }
}

fun mapToModel(band: AgeBandDto): BandModel {
    return with(band) {
        BandModel(
            startYear,
            if (startYear == endYear && endYear != 0) null else endYear
        )
    }
}

fun mapToModel(band: RenewalBandDto): BandModel {
    return with(band) {
        BandModel(
            startYear,
            if (startYear == endYear && endYear != 0 && endYear != 1) null else endYear
        )
    }
}

fun mapToModel(elements: QualityStandardSurveyElementDto): QualityStandardSurveyElementModel {
    return with(elements) {
        QualityStandardSurveyElementModel(
            elementId,
            id,
            elementName,
            elementExclude
        )
    }
}

fun mapToModel(elements: HHSRSSurveyElementDto): HHSRSSurveyElementModel {
    return with(elements) {
        HHSRSSurveyElementModel(
            elementId,
            id,
            elementName,
            elementExclude
        )
    }
}

fun mapToModel(elements: RiskAssessmentSurveyElementDto): RiskAssessmentSurveyElementModel {
    return with(elements) {
        RiskAssessmentSurveyElementModel(
            elementId,
            id,
            elementName,
            RiskAssessmentElementType.from(expectedAnswer),
            elementExclude
        )
    }
}

fun mapToModel(elements: EnergySurveyElementDto): EnergySurveyElementModel {
    return with(elements) {
        EnergySurveyElementModel(
            questionId,
            questionOrder,
            grouping,
            subHeading,
            specialScreenExtraHeader.orEmpty(),
            displayQuestion,
            question,
            warnValueLow ?: Int.MIN_VALUE,
            warnValueHigh ?: Int.MAX_VALUE,
            limitValueHigh ?: Int.MAX_VALUE,
            EnergySurveyElementType.from(responseType),
            energyQuestionVarients.map(::mapToModel).toMutableList()
        )
    }
}

fun mapToModel(elements: EnergySurveySubElementDto): EnergySurveySubElementModel {
    return with(elements) {
        EnergySurveySubElementModel(
            responseId.orEmpty(),
            responseOrder,
            response.orEmpty(),
            responseDescription.orEmpty(),
            skipCodes.split(";"),
            isRareItem
        )
    }
}

fun mapToModel(elements: StockSurveyElementDto): StockSurveyElementModel {
    return with(elements) {
        StockSurveyElementModel(
            id,
            elementSequence,
            element,
            elementGroup,
            StockSurveyType.from(surveyType),
            StockSurveyElementModel.UnitType.from(dwellingUom),
            StockSurveyElementModel.UnitType.from(blockUom),
            communalElement ?: false,
            warnValueLow ?: Int.MIN_VALUE,
            warnValueHigh ?: Int.MAX_VALUE,
            useQuantityAdder ?: false,
            useQuantityMultiplier ?: false,
            disableAgeBandFiltering ?: false,
            asBuilt ?: false,
            stockQuestionVarients.map(::mapToModel)
        )
    }
}

fun mapToModel(elements: StockSurveySubElementDto): StockSurveySubElementModel {
    return with(elements) {
        StockSurveySubElementModel(
            id,
            subElementNumber,
            subElement.trim(),
            skipElements.split(),
            life,
            minimumNumberPhotosRequired,
            StockSurveySubElementModel.Cost(
                costHouse,
                costBungalow,
                costFlat,
                costBlock
            )
        )
    }
}

fun mapToModel(element: ValidationElementDto): ValidationElementModel {
    return with(element) {
        val getSurveyType: (String) -> SurveyType = {
            if (it.trim().equals("Stock condition", ignoreCase = true)) {
                SurveyType.STOCK
            } else {
                SurveyType.ENERGY
            }
        }

        val leftSurveyType = getSurveyType(leftSurveyType)
        val rightSurveyType = getSurveyType(rightSurveyType)

        val surveyTypes = listOf(leftSurveyType, rightSurveyType)

        val category = when {
            surveyTypes.contains(SurveyType.STOCK) && surveyTypes.contains(SurveyType.ENERGY) -> ValidationCategory.S_E
            surveyTypes.contains(SurveyType.ENERGY) -> ValidationCategory.E_LOG
            surveyTypes.contains(SurveyType.STOCK) -> ValidationCategory.S_LOG
            else -> throw IllegalArgumentException("Invalid validation: $element")
        }

        ValidationElementModel(
            id,
            ValidationOperator.from(operand.trim()),
            groupName,
            category,
            ValidationOperand(
                leftElement,
                leftSubElement.orEmpty().split(),
                leftSurveyType,
            ),
            ValidationOperand(
                rightElement,
                rightSubElement.orEmpty().split(),
                rightSurveyType
            ),
            errorMessage
        )
    }
}

fun mapToModel(reason: PhotoNoAccessReasonDto): PhotoNoAccessReasonModel {
    return with(reason) {
        PhotoNoAccessReasonModel(id, this.reason)
    }
}

fun mapToModel(data: CommunalDataDto): CommunalDataModel {
    return with(data) {
        CommunalDataModel(
            id,
            element,
            propertyAddressUPRN,
            mapToModel(requireNotNull(propertyAddress)).address,
            surveyorUserName,
            communalPartNumber,
            subElementNumber,
            subElement,
            subElementDescription,
            elementNotes,
            repair,
            repairDescription,
            repairSpotPrice,
            lifeRenewalBand,
            lifeRenewalUnits,
            asBuilt,
            images.split().toMutableList(),
            imageNoPhotoReason,
            existingAgeBand,
            createdAt.toInstant(),
            requireNotNull(syncId)
        )
    }
}

fun mapToDto(data: CommunalDataModel): CommunalDataDto {
    return with(data) {
        CommunalDataDto(
            id,
            element,
            propertyUPRN,
            null,
            surveyor,
            communalPartNumber,
            subElement,
            subElementNumber,
            subElementUserEntry,
            description,
            repair,
            repairDescription,
            repairSpotPrice,
            lifeRenewalBand,
            lifeRenewalUnits,
            asBuilt,
            existingAgeBand,
            imagePaths.map { filePath ->
                File(filePath).name
            }.join(),
            noAccessReason,
            createdAt.toString(),
            createdAt.toString(),
            syncId
        )
    }
}

fun mapToModel(extPhoto: ExtBlockPhotoDto): ExtBlockPhotoModel {
    return with(extPhoto) {
        ExtBlockPhotoModel(
            id,
            propertyAddressUPRN,
            mapToModel(requireNotNull(propertyAddress)).address,
            surveyorUserName,
            images.split().toMutableList(),
            syncId,
            createdAt.toInstant(),
        )
    }
}

fun mapToDto(extBlockPhoto: ExtBlockPhotoModel): ExtBlockPhotoDto {
    return with(extBlockPhoto) {
        ExtBlockPhotoDto(
            id,
            propertyUPRN,
            null,
            imagePaths.map { filePath ->
                File(filePath).name
            }.join(),
            surveyor,
            syncId,
            createdAt.toString(),
            createdAt.toString(),
        )
    }
}

fun mapToDto(
    dataTransferRequest: DataTransferRequestModel,
    user: UserModel,
): DataTransferRequestDto {
    return with(dataTransferRequest) {
        DataTransferRequestDto(
            user.id,
            energySurveyEntries.map { mapToDto(it, user.userName) },
            HHSRSSurveyEntries.map { mapToDto(it, user.userName) },
            qualityStandardSurveyEntries.map { mapToDto(it, user.userName) },
            riskAssessmentSurveyEntries.map { mapToDto(it, user.userName) },
            stockSurveyEntries.map { mapToDto(it, user.userName) },
            propertyEntries.map { mapToDto(it) },
            noAccessEntries.map { mapToDto(it, user.userName) },
            communalData.map(::mapToDto),
            extBlockPhotos.map(::mapToDto),
            syncStartTime.toString(),
            mapToDto(dataTransferRequest.alteration),
        )
    }
}

fun mapToDto(
    entry: EnergySurveyElementEntryModel,
    surveyorName: String,
): EnergySurveyElementEntryDto {
    val details = requireNotNull(entry.details)
    return with(entry) {
        EnergySurveyElementEntryDto(
            elementId,
            element,
            subElementId,
            subElement,
            details.propertyUPRN,
            surveyorName,
            details.entryInstant.toString(),
            details.updateInstant.toString()
        )
    }
}

fun mapToDto(
    entry: HHSRSSurveyElementEntryModel,
    surveyorName: String,
): HHSRSSurveyElementEntryDto {
    val details = requireNotNull(entry.details)
    return with(entry) {
        HHSRSSurveyElementEntryDto(
            id,
            name,
            requireNotNull(rating).title,
            ratingDescription,
            ratingCost,
            imagePaths.map { filePath ->
                File(filePath).name
            }.join(),
            internalLocations.join(),
            externalLocations.join(),
            changedToTypicalFrom?.title,
            details.propertyUPRN,
            surveyorName,
            details.entryInstant.toString(),
            details.updateInstant.toString()
        )
    }
}

fun mapToDto(
    entry: QualityStandardSurveyElementEntryModel,
    surveyorName: String,
): QualityStandardSurveyElementEntryDto {
    val details = requireNotNull(entry.details)
    return with(entry) {
        QualityStandardSurveyElementEntryDto(
            elementId,
            question,
            answer.name.toLowerCase(Locale.ROOT),
            details.propertyUPRN,
            surveyorName,
            details.entryInstant.toString(),
            details.updateInstant.toString()
        )
    }
}

fun mapToDto(
    entry: RiskAssessmentSurveyElementEntryModel,
    surveyorName: String,
): RiskAssessmentSurveyElementEntryDto {
    return with(entry) {
        val details = requireNotNull(entry.details)
        RiskAssessmentSurveyElementEntryDto(
            elementId,
            question,
            answer.name.toLowerCase(Locale.ROOT),
            details.propertyUPRN,
            surveyorName,
            details.entryInstant.toString(),
            details.updateInstant.toString()
        )
    }
}

fun mapToDto(
    entry: StockSurveyElementEntryModel,
    surveyorName: String,
): StockSurveyElementEntryDto {
    val details = requireNotNull(entry.details)
    val userEntry =
        if (entry.date?.isValid == true) entry.date.toString() else entry.subElementUserEntry

    return with(entry) {
        StockSurveyElementEntryDto(
            communalPartNumber,
            surveyType.title,
            sequenceNumber,
            title,
            subElement,
            requireNotNull(subElementNumber),
            userEntry,
            description,
            repair,
            repairDescription,
            repairSpotPrice,
            lifeRenewalBand,
            lifeRenewalUnits,
            mapToDto(asBuilt),
            existingAgeBand,
            imagePaths.map { filePath ->
                File(filePath).name
            }.join(),
            noAccessReason,
            isCloned ?: false,
            details.propertyUPRN,
            surveyorName,
            details.entryInstant.toString(),
            details.updateInstant.toString()
        )
    }
}

fun mapToModel(asBuilt: String): Boolean? {
    return when (asBuilt) {
        "Yes" -> true
        "No" -> false
        else -> null
    }
}

fun mapToDto(asBuilt: Boolean?): String {
    return when (asBuilt) {
        true -> "Yes"
        false -> "No"
        else -> "NotApplicable"
    }
}

fun mapToDto(entry: NoAccessEntryModel, surveyorName: String): NoAccessEntryDto {
    return with(entry) {
        NoAccessEntryDto(
            UPRN,
            reason,
            remarks,
            surveyorName,
            imagePaths.map { filePath ->
                File(filePath).name
            }.join(),
            location.latitude,
            location.longitude,
            createdAt.toString(),
            createdAt.toString()
        )
    }
}

fun mapToDto(alteration: AlterationModel): AlterationDto {
    return with(alteration) {
        AlterationDto(
            hasAlterations,
            alterations
        )
    }
}

fun mapToDto(issue: HHSRSSevereIssueModel): HHSRSSevereIssueDto {
    return with(issue) {
        HHSRSSevereIssueDto(
            surveyorId,
            propertyId,
            elementName,
            attachments.map { path ->
                val file = File(path)
                EmailAttachmentDto(
                    file.name,
                    "image/${file.extension}",
                    getBase64StringFromFile(path)
                )
            }.filter {
                it.content.isNotEmpty()
            },
            remarks,
            internalLocations,
            externalLocations
        )
    }
}

fun mapToDto(attachment: EmailAttachmentModel): EmailAttachmentDto {
    return with(attachment) {
        EmailAttachmentDto(name, type, content)
    }
}

fun mapToDto(request: ImageRequestModel): ImageRequestDto {
    return with(request) {
        ImageRequestDto(surveyor, syncId, fileName)
    }
}

fun mapToModel(propertyStats: PropertyStatsDto): PropertyStatsModel {
    return with(propertyStats) {
        PropertyStatsModel(
            section,
            uprn,
            strata,
            ta.equals("t", ignoreCase = true) || ta.equals("pt", ignoreCase = true),
            isCompleted
        )
    }
}

private fun String.toInstant(): Instant {
    return if (contains('Z')) Instant.parse(this) else Instant.parse("${this}Z")
}

private fun String?.split(delimiter: String = DEFAULT_DELIMITER): List<String> {
    return if (isNullOrBlank()) {
        listOf()
    } else {
        val text = if (endsWith(delimiter)) removeSuffix(delimiter) else this
        text.splitOrValue(delimiter).map { it.trim() }
    }
}

private fun List<String>.join(delimiter: String = DEFAULT_DELIMITER): String {
    return joinToString(delimiter)
}

private const val DEFAULT_DELIMITER = ";"

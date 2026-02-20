package uk.co.savills.stonewood.util.mapper

import uk.co.savills.stonewood.model.LocationModel
import uk.co.savills.stonewood.model.survey.BandModel
import uk.co.savills.stonewood.model.survey.CloseEndedQuestionAnswer
import uk.co.savills.stonewood.model.survey.HHSRSElementRating
import uk.co.savills.stonewood.model.survey.HHSRSLocationModel
import uk.co.savills.stonewood.model.survey.HHSRSSevereIssueModel
import uk.co.savills.stonewood.model.survey.PhotoNoAccessReasonModel
import uk.co.savills.stonewood.model.survey.PropertyLocationType
import uk.co.savills.stonewood.model.survey.StockSurveyType
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
import uk.co.savills.stonewood.model.survey.entry.Date
import uk.co.savills.stonewood.model.survey.entry.EnergySurveyElementEntryModel
import uk.co.savills.stonewood.model.survey.entry.ExtBlockPhotoModel
import uk.co.savills.stonewood.model.survey.entry.HHSRSSurveyElementEntryModel
import uk.co.savills.stonewood.model.survey.entry.QualityStandardSurveyElementEntryModel
import uk.co.savills.stonewood.model.survey.entry.RiskAssessmentSurveyElementEntryModel
import uk.co.savills.stonewood.model.survey.entry.StockSurveyElementEntryModel
import uk.co.savills.stonewood.model.survey.entry.SurveyElementEntryDetailsModel
import uk.co.savills.stonewood.model.survey.noaccess.NoAccessEntryModel
import uk.co.savills.stonewood.model.survey.project.ProjectModel
import uk.co.savills.stonewood.model.survey.project.SurveyModel
import uk.co.savills.stonewood.model.survey.project.SurveyType
import uk.co.savills.stonewood.model.survey.property.AddressModel
import uk.co.savills.stonewood.model.survey.property.ContactModel
import uk.co.savills.stonewood.model.survey.property.PropertyModel
import uk.co.savills.stonewood.model.survey.property.PropertyStatsModel
import uk.co.savills.stonewood.model.survey.property.PropertySurveyType
import uk.co.savills.stonewood.model.survey.property.SurveyStatus
import uk.co.savills.stonewood.storage.db.entity.AgeBandEntity
import uk.co.savills.stonewood.storage.db.entity.HHSRSLocationEntity
import uk.co.savills.stonewood.storage.db.entity.HHSRSSevereIssueEntity
import uk.co.savills.stonewood.storage.db.entity.PhotoNoAccessReasonEntity
import uk.co.savills.stonewood.storage.db.entity.RenewalBandEntity
import uk.co.savills.stonewood.storage.db.entity.element.EnergySurveyElementEntity
import uk.co.savills.stonewood.storage.db.entity.element.EnergySurveySubElementEntity
import uk.co.savills.stonewood.storage.db.entity.element.HHSRSSurveyElementEntity
import uk.co.savills.stonewood.storage.db.entity.element.QualityStandardSurveyElementEntity
import uk.co.savills.stonewood.storage.db.entity.element.RiskAssessmentSurveyElementEntity
import uk.co.savills.stonewood.storage.db.entity.element.StockSurveyElementEntity
import uk.co.savills.stonewood.storage.db.entity.element.StockSurveySubElementEntity
import uk.co.savills.stonewood.storage.db.entity.element.ValidationElementEntity
import uk.co.savills.stonewood.storage.db.entity.entry.CommunalDataEntity
import uk.co.savills.stonewood.storage.db.entity.entry.EnergySurveyElementEntryEntity
import uk.co.savills.stonewood.storage.db.entity.entry.ExtBlockPhotoEntity
import uk.co.savills.stonewood.storage.db.entity.entry.HHSRSSurveyElementEntryEntity
import uk.co.savills.stonewood.storage.db.entity.entry.NoAccessEntryEntity
import uk.co.savills.stonewood.storage.db.entity.entry.QualityStandardSurveyElementEntryEntity
import uk.co.savills.stonewood.storage.db.entity.entry.RiskAssessmentSurveyElementEntryEntity
import uk.co.savills.stonewood.storage.db.entity.entry.StockSurveyElementEntryEntity
import uk.co.savills.stonewood.storage.db.entity.entry.SurveyElementEntryDetails
import uk.co.savills.stonewood.storage.db.entity.project.ProjectEntity
import uk.co.savills.stonewood.storage.db.entity.project.ProjectWithSurveys
import uk.co.savills.stonewood.storage.db.entity.project.SurveyEntity
import uk.co.savills.stonewood.storage.db.entity.property.PropertyEntity
import uk.co.savills.stonewood.storage.db.entity.property.PropertyStatsEntity
import uk.co.savills.stonewood.storage.db.entity.property.PropertyWithNoAccessHistory
import java.time.Instant
import kotlin.text.split as splitOrValue

fun mapToModel(projectWithSurveys: ProjectWithSurveys): ProjectModel {
    return with(projectWithSurveys.project) {
        ProjectModel(
            id,
            name,
            description,
            projectWithSurveys.surveys.map(::mapToModel),
            isExternalOnlyType,
            areRepairsAvailable,
            numberOfEnergyExtPhotos,
            isClosed
        )
    }
}

fun mapToEntity(project: ProjectModel): ProjectEntity {
    return with(project) {
        ProjectEntity(
            id,
            name,
            description,
            isExternalOnlyType,
            areRepairsAvailable,
            numberOfSharedExternalPhotos,
            isClosed
        )
    }
}

fun mapToModel(entry: EnergySurveyElementEntryEntity): EnergySurveyElementEntryModel {
    return with(entry) {
        EnergySurveyElementEntryModel(
            elementId,
            element,
            subElementId,
            subElement,
            mapToModel(details)
        )
    }
}

fun mapToEntity(
    entry: EnergySurveyElementEntryModel,
    projectId: String,
): EnergySurveyElementEntryEntity {
    return with(entry) {
        EnergySurveyElementEntryEntity(
            elementId,
            element,
            subElementId,
            subElement,
            mapToEntity(requireNotNull(details), projectId)
        )
    }
}

fun mapToModel(entry: HHSRSSurveyElementEntryEntity): HHSRSSurveyElementEntryModel {
    return with(entry) {
        HHSRSSurveyElementEntryModel(
            elementId,
            name,
            HHSRSElementRating.from(rating),
            ratingCost,
            ratingDescription,
            imagePaths.split().toMutableList(),
            internalLocations.split().toMutableList(),
            externalLocations.split().toMutableList(),
            changedToTypicalFrom?.let { HHSRSElementRating.from(it) },
            isComplete,
            mapToModel(details),
        )
    }
}

fun mapToEntity(
    entry: HHSRSSurveyElementEntryModel,
    projectId: String,
): HHSRSSurveyElementEntryEntity {
    return with(entry) {
        val isTypical = rating == HHSRSElementRating.TYPICAL
        val wasChangedToTypical = changedToTypicalFrom != null
        HHSRSSurveyElementEntryEntity(
            id,
            name,
            requireNotNull(rating).title,
            ratingDescription.takeUnless { isTypical && !wasChangedToTypical }.orEmpty(),
            ratingCost.takeIf { rating == HHSRSElementRating.SEVERE || wasChangedToTypical }
                .orEmpty(),
            imagePaths.join().takeUnless { isTypical && !wasChangedToTypical }.orEmpty(),
            internalLocations.join().takeUnless { isTypical && !wasChangedToTypical }.orEmpty(),
            externalLocations.join().takeUnless { isTypical && !wasChangedToTypical }.orEmpty(),
            changedToTypicalFrom?.title,
            isComplete,
            mapToEntity(requireNotNull(details), projectId)
        )
    }
}

fun mapToModel(entry: QualityStandardSurveyElementEntryEntity): QualityStandardSurveyElementEntryModel {
    return with(entry) {
        QualityStandardSurveyElementEntryModel(
            elementId,
            question,
            CloseEndedQuestionAnswer.from(answer),
            mapToModel(details)
        )
    }
}

fun mapToEntity(
    entry: QualityStandardSurveyElementEntryModel,
    projectId: String,
): QualityStandardSurveyElementEntryEntity {
    return with(entry) {
        QualityStandardSurveyElementEntryEntity(
            elementId,
            question,
            answer.name,
            mapToEntity(requireNotNull(details), projectId)
        )
    }
}

fun mapToModel(entry: RiskAssessmentSurveyElementEntryEntity): RiskAssessmentSurveyElementEntryModel {
    return with(entry) {
        RiskAssessmentSurveyElementEntryModel(
            elementId,
            question,
            CloseEndedQuestionAnswer.from(answer),
            mapToModel(details)
        )
    }
}

fun mapToEntity(
    entry: RiskAssessmentSurveyElementEntryModel,
    projectId: String,
): RiskAssessmentSurveyElementEntryEntity {
    return with(entry) {
        RiskAssessmentSurveyElementEntryEntity(
            elementId,
            question,
            answer.name,
            mapToEntity(requireNotNull(details), projectId)
        )
    }
}

fun mapToModel(entry: StockSurveyElementEntryEntity): StockSurveyElementEntryModel {
    return with(entry) {
        StockSurveyElementEntryModel(
            elementId,
            communalPartNumber,
            StockSurveyType.from(surveyType),
            sequenceNumber,
            title,
            subElementNumber,
            subElement,
            subElementUserEntry,
            Date.fromString(subElementUserEntry),
            description,
            repair,
            repairDescription,
            repairSpotPrice,
            lifeRenewalBand,
            lifeRenewalUnits,
            asBuilt,
            imagePaths.split().toMutableList(),
            noAccessReason,
            existingAgeBand,
            isIndividual,
            isCloned,
            isComplete,
            mapToModel(details),
        )
    }
}

fun mapToEntity(
    entry: StockSurveyElementEntryModel,
    projectId: String,
): StockSurveyElementEntryEntity {
    val userEntry =
        if (entry.date?.isValid == true) entry.date.toString() else entry.subElementUserEntry

    return with(entry) {
        StockSurveyElementEntryEntity(
            elementId,
            communalPartNumber,
            surveyType.title,
            sequenceNumber,
            title,
            requireNotNull(subElementNumber),
            subElement,
            userEntry,
            description,
            repair,
            repairDescription,
            repairSpotPrice,
            lifeRenewalBand,
            lifeRenewalUnits,
            asBuilt,
            existingAgeBand,
            imagePaths.join(),
            noAccessReason,
            isIndividual,
            isCloned,
            isComplete,
            mapToEntity(requireNotNull(details), projectId),
        )
    }
}

fun mapToModel(details: SurveyElementEntryDetails): SurveyElementEntryDetailsModel {
    return with(details) {
        SurveyElementEntryDetailsModel(
            propertyUPRN,
            Instant.parse(entryTimestamp),
            Instant.parse(updateTimestamp)
        )
    }
}

fun mapToEntity(
    details: SurveyElementEntryDetailsModel,
    projectId: String,
): SurveyElementEntryDetails {
    return with(details) {
        SurveyElementEntryDetails(
            projectId,
            propertyUPRN,
            entryInstant.toString(),
            updateInstant.toString()
        )
    }
}

fun mapToModel(propertyWithNoAccessHistory: PropertyWithNoAccessHistory): PropertyModel {
    val property = propertyWithNoAccessHistory.property
    val noAccessHistory = propertyWithNoAccessHistory.noAccessHistory

    return with(property) {
        PropertyModel(
            id,
            order,
            UPRN,
            TA,
            strata,
            AddressModel(
                number,
                address1,
                address2,
                address3,
                address4,
                postalCode
            ),
            PropertySurveyType.valueOf(surveyType1),
            PropertySurveyType.valueOf(surveyType2),
            section,
            ContactModel(
                contactNumber1,
                contactNumber2,
                contactNotes
            ),
            frontDoorPhoto,
            LocationModel(latitude, longitude),
            SurveyStatus(
                isRiskAssessmentSurveyComplete,
                isQualityStandardSurveyComplete,
                isStockSurveyComplete,
                isEnergySurveyComplete,
                isHHSRSSurveyComplete,
                isValidationComplete,
                areRequiredSurveysComplete
            ),
            noAccessHistory.map(::mapToModel),
            extPhotos.split().toMutableList(),
            extPhotosClonedFrom,
            hasExternalPhoto,
            isDeleted,
            createdAt?.let { Instant.parse(it) },
            updatedAt?.let { Instant.parse(it) }
        )
    }
}

fun mapToModel(property: PropertyEntity): PropertyModel {
    return with(property) {
        PropertyModel(
            id,
            order,
            UPRN,
            TA,
            strata,
            AddressModel(
                number,
                address1,
                address2,
                address3,
                address4,
                postalCode
            ),
            PropertySurveyType.valueOf(surveyType1),
            PropertySurveyType.valueOf(surveyType2),
            section,
            ContactModel(
                contactNumber1,
                contactNumber2,
                contactNotes
            ),
            frontDoorPhoto,
            LocationModel(latitude, longitude),
            SurveyStatus(
                isRiskAssessmentSurveyComplete,
                isQualityStandardSurveyComplete,
                isStockSurveyComplete,
                isEnergySurveyComplete,
                isHHSRSSurveyComplete,
                isValidationComplete,
                areRequiredSurveysComplete
            ),
            extBlockPhotos = extPhotos.split().toMutableList(),
            extPhotosClonedFrom = extPhotosClonedFrom,
            hasExternalPhoto = hasExternalPhoto,
            isDeleted = isDeleted,
            createdAt = createdAt?.let { Instant.parse(it) },
            updatedAt = updatedAt?.let { Instant.parse(it) }
        )
    }
}

fun mapToEntity(property: PropertyModel, projectId: String): PropertyEntity {
    return with(property) {
        PropertyEntity(
            "$projectId$id",
            order,
            id,
            projectId,
            UPRN,
            TA,
            strata,
            address.number,
            address.line1,
            address.line2,
            address.line3,
            address.line4,
            address.postalCode,
            surveyTypeOriginal.name,
            surveyType.name,
            section,
            contact.number,
            contact.numberSecondary,
            contact.notes,
            frontDoorPhoto,
            extBlockPhotos.join(),
            extPhotosClonedFrom,
            hasExternalPhoto,
            location.latitude,
            location.longitude,
            surveyStatus.isRiskAssessmentSurveyComplete,
            surveyStatus.isQualityStandardSurveyComplete,
            surveyStatus.isStockSurveyComplete,
            surveyStatus.isEnergySurveyComplete,
            surveyStatus.isHHSRSSurveyComplete,
            surveyStatus.isValidationComplete,
            surveyStatus.areRequiredSurveysComplete,
            isDeleted,
            createdAt?.toString(),
            updatedAt?.toString()
        )
    }
}

fun mapToModel(survey: SurveyEntity): SurveyModel {
    return with(survey) {
        SurveyModel(
            SurveyType.from(type),
            title
        )
    }
}

fun mapToEntity(survey: SurveyModel, projectId: String): SurveyEntity {
    return with(survey) {
        SurveyEntity(
            projectId,
            type.ordinal,
            title
        )
    }
}

fun mapToModel(entry: NoAccessEntryEntity): NoAccessEntryModel {
    return with(entry) {
        NoAccessEntryModel(
            UPRN,
            reason,
            remarks,
            imagePaths.split(),
            LocationModel(latitude, longitude),
            Instant.parse(createdAt)
        )
    }
}

fun mapToEntity(
    entry: NoAccessEntryModel,
    projectId: String,
    propertyId: Int
): NoAccessEntryEntity {
    return with(entry) {
        NoAccessEntryEntity(
            "$projectId$propertyId",
            projectId,
            UPRN,
            reason,
            remarks,
            imagePaths.join(),
            location.latitude,
            location.longitude,
            createdAt.toString()
        )
    }
}

fun mapToModel(entry: QualityStandardSurveyElementEntity): QualityStandardSurveyElementModel {
    return with(entry) {
        QualityStandardSurveyElementModel(id, sequenceNumber, question, exclude)
    }
}

fun mapToEntity(
    entry: QualityStandardSurveyElementModel,
    projectId: String
): QualityStandardSurveyElementEntity {
    return with(entry) {
        QualityStandardSurveyElementEntity(id, sequenceNumber, question, exclude, projectId)
    }
}

fun mapToModel(entry: RiskAssessmentSurveyElementEntity): RiskAssessmentSurveyElementModel {
    return with(entry) {
        RiskAssessmentSurveyElementModel(
            id,
            sequenceNumber,
            question,
            RiskAssessmentElementType.from(type),
            exclude
        )
    }
}

fun mapToEntity(
    entry: RiskAssessmentSurveyElementModel,
    projectId: String
): RiskAssessmentSurveyElementEntity {
    return with(entry) {
        RiskAssessmentSurveyElementEntity(
            id,
            sequenceNumber,
            question,
            type.ordinal,
            exclude,
            projectId
        )
    }
}

fun mapToModel(entry: HHSRSSurveyElementEntity): HHSRSSurveyElementModel {
    return with(entry) {
        HHSRSSurveyElementModel(id, sequenceNumber, title, exclude)
    }
}

fun mapToEntity(entry: HHSRSSurveyElementModel, projectId: String): HHSRSSurveyElementEntity {
    return with(entry) {
        HHSRSSurveyElementEntity(id, sequenceNumber, title, exclude, projectId)
    }
}

fun mapToModel(
    elementWithSubElements: EnergySurveyElementEntity,
): EnergySurveyElementModel {
    return with(elementWithSubElements) {
        EnergySurveyElementModel(
            id,
            serialNumber,
            group,
            section,
            subSection,
            titleShort,
            titleLong,
            warnValueLow,
            warnValue,
            limitValue,
            EnergySurveyElementType.from(type)
        )
    }
}

fun mapToEntity(entry: EnergySurveyElementModel, projectId: String): EnergySurveyElementEntity {
    return with(entry) {
        EnergySurveyElementEntity(
            id,
            projectId,
            serialNumber,
            group,
            section,
            subSection,
            titleShort,
            titleLong,
            warnValueHigh,
            warnValueLow,
            limitValue,
            type.title
        )
    }
}

fun mapToModel(element: EnergySurveySubElementEntity): EnergySurveySubElementModel {
    return with(element) {
        EnergySurveySubElementModel(
            id,
            serialNumber,
            title,
            description,
            skipCodes.split(";"),
            isRare
        )
    }
}

fun mapToEntity(
    entry: EnergySurveySubElementModel,
    elementId: Int,
    projectId: String
): EnergySurveySubElementEntity {
    return with(entry) {
        EnergySurveySubElementEntity(
            id,
            elementId,
            projectId,
            serialNumber,
            title,
            description,
            skipCodes.join(";"),
            isRare
        )
    }
}

fun mapToModel(
    element: StockSurveyElementEntity,
    subElements: List<StockSurveySubElementEntity>
): StockSurveyElementModel {
    return with(element) {
        StockSurveyElementModel(
            id,
            sequenceNumber,
            title,
            group,
            StockSurveyType.from(surveyType),
            StockSurveyElementModel.UnitType.from(unit),
            StockSurveyElementModel.UnitType.from(unitBlock),
            isCommunal,
            warnValueLow,
            warnValue,
            useQuantityAdder,
            useQuantityMultiplier,
            disableAgeBandFiltering,
            isAsBuiltRequired,
            subElements.map(::mapToModel)
        )
    }
}

fun mapToEntity(entry: StockSurveyElementModel, projectId: String): StockSurveyElementEntity {
    return with(entry) {
        StockSurveyElementEntity(
            id,
            projectId,
            sequenceNumber,
            title,
            group,
            surveyType.title,
            unit.toString(),
            unitBlock.toString(),
            isCommunal,
            warnValueHigh,
            warnValueLow,
            useQuantityAdder,
            useQuantityMultiplier,
            disableAgeBandFiltering,
            isAsBuiltRequired
        )
    }
}

fun mapToModel(element: StockSurveySubElementEntity): StockSurveySubElementModel {
    return with(element) {
        StockSurveySubElementModel(
            id,
            number,
            title,
            skippedElements.split(";"),
            life,
            minPhotoCount,
            StockSurveySubElementModel.Cost(costHouse, costBungalow, costFlat, costBlock)
        )
    }
}

fun mapToEntity(
    entry: StockSurveySubElementModel,
    elementId: Int,
    projectId: String
): StockSurveySubElementEntity {
    return with(entry) {
        StockSurveySubElementEntity(
            id,
            projectId,
            number,
            elementId,
            title,
            skippedElements.join(";"),
            life,
            minPhotoCount,
            cost.house,
            cost.bungalow,
            cost.flat,
            cost.block
        )
    }
}

fun mapToModel(element: ValidationElementEntity): ValidationElementModel {
    return with(element) {
        ValidationElementModel(
            id,
            ValidationOperator.from(operator),
            group,
            ValidationCategory.values()[category],
            ValidationOperand(
                leftElement,
                leftSubElement.split(),
                SurveyType.from(leftSurveyType)
            ),
            ValidationOperand(
                rightElement,
                rightSubElement.split(),
                SurveyType.from(rightSurveyType)
            ),
            errorMessage
        )
    }
}

fun mapToEntity(element: ValidationElementModel, projectId: String): ValidationElementEntity {
    return with(element) {
        ValidationElementEntity(
            id,
            projectId,
            operator.symbol,
            group,
            category.ordinal,
            leftOperand.surveyType.ordinal,
            leftOperand.elementTitle,
            leftOperand.subElements.join(),
            rightOperand.surveyType.ordinal,
            rightOperand.elementTitle,
            rightOperand.subElements.join(),
            errorMessage
        )
    }
}

fun mapToModel(reason: PhotoNoAccessReasonEntity): PhotoNoAccessReasonModel {
    return with(reason) {
        PhotoNoAccessReasonModel(id, this.reason)
    }
}

fun mapToEntity(
    projectId: String,
    reason: PhotoNoAccessReasonModel
): PhotoNoAccessReasonEntity {
    return with(reason) {
        PhotoNoAccessReasonEntity(id, projectId, this.reason)
    }
}

fun mapToModel(data: CommunalDataEntity): CommunalDataModel {
    return with(data) {
        CommunalDataModel(
            id,
            element,
            propertyUPRN,
            AddressModel(
                number,
                address1,
                address2,
                address3,
                address4,
                postalCode
            ),
            surveyor,
            communalPartNumber,
            subElementNumber,
            subElement,
            subElementUserEntry,
            description,
            repair,
            repairDescription,
            repairSpotPrice,
            lifeRenewalBand,
            lifeRenewalUnits,
            asBuilt,
            imagePaths.split().toMutableList(),
            noAccessReason,
            existingAgeBand,
            Instant.parse(entryTimestamp),
            syncId
        )
    }
}

fun mapToEntity(data: CommunalDataModel, projectId: String): CommunalDataEntity {
    return with(data) {
        CommunalDataEntity(
            id,
            element,
            propertyUPRN,
            projectId,
            address.number,
            address.line1,
            address.line2,
            address.line3,
            address.line4,
            address.postalCode,
            surveyor,
            communalPartNumber,
            subElementNumber,
            subElement,
            subElementUserEntry,
            description,
            repair,
            repairDescription,
            repairSpotPrice,
            lifeRenewalBand,
            lifeRenewalUnits,
            asBuilt,
            existingAgeBand,
            imagePaths.join(),
            noAccessReason,
            createdAt.toString(),
            syncId
        )
    }
}

fun mapToModel(extPhoto: ExtBlockPhotoEntity): ExtBlockPhotoModel {
    return with(extPhoto) {
        ExtBlockPhotoModel(
            id,
            propertyUPRN,
            AddressModel(
                number,
                address1,
                address2,
                address3,
                address4,
                postalCode
            ),
            surveyor,
            imagePaths.split().toMutableList(),
            syncId,
            Instant.parse(entryTimestamp)
        )
    }
}

fun mapToEntity(extPhoto: ExtBlockPhotoModel, projectId: String): ExtBlockPhotoEntity {
    return with(extPhoto) {
        ExtBlockPhotoEntity(
            id,
            propertyUPRN,
            projectId,
            address.number,
            address.line1,
            address.line2,
            address.line3,
            address.line4,
            address.postalCode,
            surveyor,
            imagePaths.join(),
            createdAt.toString(),
            syncId
        )
    }
}

fun mapToModel(entry: HHSRSLocationEntity): HHSRSLocationModel {
    return with(entry) {
        HHSRSLocationModel(id, name, PropertyLocationType.from(type))
    }
}

fun mapToEntity(entry: HHSRSLocationModel, projectId: String): HHSRSLocationEntity {
    return with(entry) {
        HHSRSLocationEntity(name, type.ordinal, projectId)
    }
}

fun mapToModel(band: AgeBandEntity): BandModel {
    return with(band) {
        BandModel(lowerBound, upperBound)
    }
}

fun mapToEntity(projectId: String, band: BandModel): AgeBandEntity {
    return with(band) {
        AgeBandEntity(projectId, lowerBound, upperBound)
    }
}

fun mapToModel(band: RenewalBandEntity): BandModel {
    return with(band) {
        BandModel(lowerBound, upperBound)
    }
}

fun mapToEntity(band: BandModel, projectId: String): RenewalBandEntity {
    return with(band) {
        RenewalBandEntity(projectId, lowerBound, upperBound)
    }
}

fun mapToModel(stats: PropertyStatsEntity): PropertyStatsModel {
    return with(stats) {
        PropertyStatsModel(section, uprn, strata, isRequired, isComplete)
    }
}

fun mapToEntity(stats: PropertyStatsModel, projectId: String): PropertyStatsEntity {
    return with(stats) {
        PropertyStatsEntity(projectId, section, uprn, strata, isRequired, isComplete)
    }
}

fun mapToEntity(issue: HHSRSSevereIssueModel): HHSRSSevereIssueEntity {
    return with(issue) {
        HHSRSSevereIssueEntity(
            projectId,
            propertyId,
            elementId,
            surveyorId,
            elementName,
            issue.remarks,
            issue.attachments.join(),
            issue.internalLocations.join(),
            issue.externalLocations.join(),
            isReported
        )
    }
}

fun mapToModel(issue: HHSRSSevereIssueEntity): HHSRSSevereIssueModel {
    return with(issue) {
        HHSRSSevereIssueModel(
            surveyorId,
            projectId,
            propertyId,
            elementId,
            element,
            issue.remarks,
            issue.images.split(),
            issue.internalLocations.split(),
            issue.externalLocations.split()
        ).apply {
            this.isReported = issue.isReported
        }
    }
}

private fun String?.split(delimiter: String = DEFAULT_DELIMITER): List<String> {
    return if (isNullOrBlank()) {
        listOf()
    } else {
        val text = if (endsWith(delimiter)) removeSuffix(delimiter) else this
        text.splitOrValue(delimiter)
    }
}

private fun List<String>.join(delimiter: String = DEFAULT_DELIMITER): String {
    return joinToString(delimiter)
}

private const val DEFAULT_DELIMITER = ","

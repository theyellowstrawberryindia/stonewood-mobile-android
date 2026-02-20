package uk.co.savills.stonewood.model.survey.project

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
enum class SurveyType : Parcelable {
    RISK_ASSESSMENT,
    QUALITY_STANDARD,
    STOCK,
    ENERGY,
    HHSRS,
    VALIDATION;

    companion object {
        fun from(ordinal: Int): SurveyType {
            return values()[ordinal]
        }
    }
}

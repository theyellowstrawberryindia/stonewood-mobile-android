package uk.co.savills.stonewood.model.survey.element

enum class RiskAssessmentElementType {
    EXPECTED_YES,
    EXPECTED_NO,
    REQUIRED;

    companion object {
        fun from(ordinal: Int): RiskAssessmentElementType {
            return values()[ordinal]
        }
    }
}

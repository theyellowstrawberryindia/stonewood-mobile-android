package uk.co.savills.stonewood.model.survey.property

enum class PropertyStatus {
    SURVEYED,
    VOID,
    REFUSED_OR_PRIVATE,
    REPEATED_NO_ACCESS_OR_FAILED,
    NO_ACCESS_OR_FAILED,
    CONTACT_AVAILABLE,
    CONTACT_UNAVAILABLE;

    companion object {
        fun from(ordinal: Int): PropertyStatus {
            return values()[ordinal]
        }
    }
}

package uk.co.savills.stonewood.model.survey

enum class PropertyLocationType {
    INTERNAL,
    EXTERNAL;

    companion object {
        fun from(ordinal: Int): PropertyLocationType {
            return values()[ordinal]
        }
    }
}

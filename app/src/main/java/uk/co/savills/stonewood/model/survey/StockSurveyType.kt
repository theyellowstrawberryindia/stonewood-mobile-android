package uk.co.savills.stonewood.model.survey

enum class StockSurveyType(val title: String) {
    INTERNAL("Internal"),
    EXTERNAL("External"),
    COMMUNAL("Communal"),
    BLOCK_ONLY_EXTERNAL("BlockOnlyExternal"),
    HOUSE_ONLY_EXTERNAL("HouseExternalOnly");

    companion object {
        fun from(type: String): StockSurveyType {
            return requireNotNull(
                values().find { it.title.equals(type, ignoreCase = true) }
            )
        }
    }
}

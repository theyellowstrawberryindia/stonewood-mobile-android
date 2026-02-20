package uk.co.savills.stonewood.model.survey.element.energy

enum class EnergySurveyElementType(val title: String) {
    TEXT("text"),
    QUANTITY("quantity"),
    QUANTITY0("quantity0"),
    DROP_DOWN("drop down list");

    companion object {
        fun from(type: String): EnergySurveyElementType {
            return requireNotNull(values().find { it.title.equals(type, ignoreCase = true) })
        }
    }
}

package uk.co.savills.stonewood.model.survey.element.validation

enum class ValidationOperator(val symbol: String) {
    THEN("then"),
    THEN_EQUALS("then ="),
    THEN_GREATER_THAN("then >"),
    THEN_GREATER_THAN_OR_EQUAL_TO("then >="),
    THEN_LESSER_THAN("then <"),
    THEN_LESSER_THAN_OR_EQUAL_TO("then <="),
    GREATER_THAN(">"),
    GREATER_THAN_OR_EQUAL_TO(">="),
    LESSER_THAN("<"),
    LESSER_THAN_OR_EQUAL_TO("<="),
    EQUALS("=");

    companion object {
        fun from(string: String): ValidationOperator {
            return values().find { it.symbol == string }
                ?: throw IllegalArgumentException("Invalidation validation operator: $string")
        }
    }
}

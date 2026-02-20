package uk.co.savills.stonewood.model.survey.element.validation

data class ValidationElementModel(
    val id: Int,
    val operator: ValidationOperator,
    val group: String,
    val category: ValidationCategory,
    val leftOperand: ValidationOperand,
    val rightOperand: ValidationOperand,
    val errorMessage: String,
) {
    fun shouldValidate(): Boolean {
        val shouldValidate = leftOperand.element?.isComplete == true && rightOperand.element?.isComplete == true

        return when (operator) {
            ValidationOperator.THEN,
            ValidationOperator.THEN_EQUALS,
            ValidationOperator.THEN_GREATER_THAN,
            ValidationOperator.THEN_GREATER_THAN_OR_EQUAL_TO,
            ValidationOperator.THEN_LESSER_THAN,
            ValidationOperator.THEN_LESSER_THAN_OR_EQUAL_TO -> shouldValidate && leftOperand.subElements.first() == leftOperand.subElement
            else -> shouldValidate
        }
    }

    val isValid: Boolean
        get() {
            val rightElementSubElement = rightOperand.answer
            val leftElementSubElement = leftOperand.answer

            return try {
                when (operator) {
                    ValidationOperator.THEN -> rightOperand.subElements.any { it == rightElementSubElement }
                    ValidationOperator.THEN_EQUALS -> rightOperand.subElements.any { rightElementSubElement.toDouble() == it.toDouble() }
                    ValidationOperator.THEN_GREATER_THAN -> rightOperand.subElements.any { rightElementSubElement.toDouble() > it.toDouble() }
                    ValidationOperator.THEN_GREATER_THAN_OR_EQUAL_TO -> rightOperand.subElements.any { rightElementSubElement.toDouble() >= it.toDouble() }
                    ValidationOperator.THEN_LESSER_THAN -> rightOperand.subElements.any { rightElementSubElement.toDouble() < it.toDouble() }
                    ValidationOperator.THEN_LESSER_THAN_OR_EQUAL_TO -> rightOperand.subElements.any { rightElementSubElement.toDouble() <= it.toDouble() }

                    ValidationOperator.GREATER_THAN -> leftElementSubElement.toDouble() > rightElementSubElement.toDouble()
                    ValidationOperator.GREATER_THAN_OR_EQUAL_TO -> leftElementSubElement.toDouble() >= rightElementSubElement.toDouble()
                    ValidationOperator.LESSER_THAN -> leftElementSubElement.toDouble() < rightElementSubElement.toDouble()
                    ValidationOperator.LESSER_THAN_OR_EQUAL_TO -> leftElementSubElement.toDouble() <= rightElementSubElement.toDouble()
                    ValidationOperator.EQUALS -> leftElementSubElement == rightElementSubElement
                }
            } catch (e: Exception) {
                true
            }
        }
}

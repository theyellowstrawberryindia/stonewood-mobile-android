package uk.co.savills.stonewood.model.survey

enum class CloseEndedQuestionAnswer {
    YES,
    NO,
    UNANSWERED;

    companion object {
        fun from(result: String): CloseEndedQuestionAnswer {
            return requireNotNull(values().find { it.name == result })
        }
    }
}

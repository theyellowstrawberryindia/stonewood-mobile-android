package uk.co.savills.stonewood.model.survey.entry

import java.time.Month
import java.time.Year

data class Date(
    var month: Month?,
    var year: Year?
) {
    val isValid
        get() = month != null && year != null

    override fun toString() = "01/${month?.value}/${year?.value}"

    companion object {
        fun fromString(text: String): Date? {
            val date = text.split("/")

            if (date.size != 3) return null

            return Date(Month.of(date[1].toInt()), Year.of(date[2].toInt()))
        }
    }
}

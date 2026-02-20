package uk.co.savills.stonewood.model.survey

import android.annotation.SuppressLint

@SuppressLint("InvalidClassName")
enum class HHSRSElementRating(val title: String) {
    TYPICAL("Typical"),
    SLIGHT("Slight"),
    MODERATE("Moderate"),
    SEVERE("Severe");

    companion object {
        fun from(title: String): HHSRSElementRating {
            return requireNotNull(values().find { it.title == title })
        }
    }
}

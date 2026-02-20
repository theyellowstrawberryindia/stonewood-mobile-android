package uk.co.savills.stonewood.util

import java.util.Locale

fun String.isNumeric(): Boolean {
    return try {
        toDouble()
        true
    } catch (e: NumberFormatException) {
        false
    }
}

fun String.spitOrEmpty(delimiter: String): List<String> {
    return if (isBlank()) {
        listOf()
    } else {
        val text = if (endsWith(delimiter)) removeSuffix(delimiter) else this
        text.split(delimiter).map { it.trim() }
    }
}

fun String.toProperCase(): String {
    return split(" ")
        .joinToString(" ") {
            it.capitalize(Locale.ROOT)
        }
}

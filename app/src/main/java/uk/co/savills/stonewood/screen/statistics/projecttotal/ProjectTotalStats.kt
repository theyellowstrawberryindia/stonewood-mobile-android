package uk.co.savills.stonewood.screen.statistics.projecttotal

import java.text.DecimalFormat

data class ProjectTotalStats(
    val strata: String,
    val required: Int,
    val achieved: Int,
) {
    companion object {
        fun getCompletionPercent(required: Int, achieved: Int): String? {
            val percent = if (required - achieved < 1) {
                100f
            } else {
                minOf((achieved.toFloat() / required) * 100, 100f)
            }

            return DecimalFormat("#.##").format(percent)
        }
    }
}

package uk.co.savills.stonewood.model.survey

data class BandModel(
    val lowerBound: Int,
    val upperBound: Int?
) {
    var isSelected = false

    override fun toString(): String {
        return when {
            upperBound == null -> "$lowerBound+"
            lowerBound == upperBound -> "$upperBound"
            else -> "$lowerBound-$upperBound"
        }
    }
}

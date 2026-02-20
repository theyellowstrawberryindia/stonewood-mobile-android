package uk.co.savills.stonewood.model.survey.property

data class AddressModel(
    val number: String,
    val line1: String,
    val line2: String,
    val line3: String,
    val line4: String,
    val postalCode: String,
) {
    override fun toString(): String {
        var address = if (number.isNotBlank()) "$number, $line1" else line1

        if (postalCode.isNotBlank()) {
            address += ", $postalCode"
        }

        return address
    }
}

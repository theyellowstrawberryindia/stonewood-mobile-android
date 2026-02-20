package uk.co.savills.stonewood.model.survey.property

data class ContactModel(
    val number: String,
    val numberSecondary: String,
    var notes: String
) {
    val isAvailable
        get() = number.isNotBlank() || numberSecondary.isNotBlank()
}

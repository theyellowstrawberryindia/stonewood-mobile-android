package uk.co.savills.stonewood.model

data class UserModel(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val userName: String,
    val email: String,
    val defaultPassword: Boolean,
    val authToken: AuthTokenModel
) {
    val fullName: String
        get() {
            var name = firstName

            if (lastName.isNotEmpty()) name += " $lastName"

            return name
        }
}

package uk.co.savills.stonewood.model

data class AuthTokenModel(
    val accessToken: String,
    val refreshToken: String,
    val expiryTimeStamp: String,
)

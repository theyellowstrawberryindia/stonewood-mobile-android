package uk.co.savills.stonewood.network.exception

class UnauthorizedException : Exception() {
    override val message: String?
        get() = "Unauthorized access"
}

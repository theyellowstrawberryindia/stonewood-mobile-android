package uk.co.savills.stonewood.network.exception

import java.net.ConnectException

class NetworkUnavailableException : ConnectException() {
    override val message: String
        get() = "No internet connection"
}

package uk.co.savills.stonewood.util.connectionerror

interface ServerConnectionErrorHandler {
    fun onApiError(message: String)
    fun onNoConnectionError()
    fun onUnexpectedError(message: String)
}

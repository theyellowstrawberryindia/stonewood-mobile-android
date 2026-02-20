package uk.co.savills.stonewood.util.connectionerror

import uk.co.savills.stonewood.util.SingleLiveEvent

interface ServerConnectionErrorDispatcher {
    val serverConnectionError: SingleLiveEvent<String>
    val noConnectionError: SingleLiveEvent<Unit?>
    val unexpectedError: SingleLiveEvent<String>
}

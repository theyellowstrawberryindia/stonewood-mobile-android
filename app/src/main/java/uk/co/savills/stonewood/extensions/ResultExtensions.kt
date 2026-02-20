package uk.co.savills.stonewood.extensions

import uk.co.savills.stonewood.util.Result
import java.lang.Exception
import java.util.concurrent.CancellationException

fun <T : Any> Result<out T>?.throwOnError(message: String? = null) {
    if (this is Result.Success) {
        return
    }

    if (this == null) {
        throw CancellationException()
    }

    if (this is Result.Error) {
        if (message != null) {
            throw Exception(message, this.exception)
        } else {
            throw Exception(this.exception)
        }
    }

    throw Exception("Unexpected Result")
}

fun <T : Any> Result<out T>?.resultOrThrow(message: String? = null): T {
    this.throwOnError(message)

    if (this is Result.Success) {
        return this.data
    }

    throw Exception("Unexpected Result")
}

package uk.co.savills.stonewood.network.util

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class DeferredCallAdapterFactory : CallAdapter.Factory() {
    override fun get(
        returnType: Type,
        annotations: Array<Annotation>,
        retrofit: Retrofit,
    ): CallAdapter<*, *>? {
        if (getRawType(returnType) != Deferred::class.java) {
            throw IllegalStateException("Api call return type must be Deferred")
        }

        val type = getParameterUpperBound(0, returnType as ParameterizedType) as ParameterizedType
        val deferredType = getRawType(type)

        if (deferredType != ApiResponse::class.java) {
            throw IllegalArgumentException("Deferred type must be ApiResponse")
        }

        val apiResponseType = getParameterUpperBound(0, type)
        return DeferredCallAdapter<Any>(apiResponseType)
    }

    private class DeferredCallAdapter<T : Any>(
        private val responseType: Type,
    ) : CallAdapter<T, Deferred<ApiResponse<T>>> {
        override fun responseType() = responseType

        override fun adapt(call: Call<T>): Deferred<ApiResponse<T>> {
            val deferred = CompletableDeferred<ApiResponse<T>>().apply {
                invokeOnCompletion {
                    if (isCancelled) call.cancel()
                }
            }

            call.enqueue(object : Callback<T> {
                override fun onFailure(call: Call<T>, t: Throwable) {
                    val exception = if (t is Exception) t else Exception(t)
                    deferred.completeExceptionally(exception)
                }

                override fun onResponse(call: Call<T>, response: Response<T>) {
                    try {
                        deferred.complete(ApiResponse.create(response))
                    } catch (e: Exception) {
                        deferred.completeExceptionally(e)
                    }
                }
            })

            return deferred
        }
    }
}

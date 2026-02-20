package uk.co.savills.stonewood.network.util

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Response
import uk.co.savills.stonewood.network.dto.ErrorResponseDto
import uk.co.savills.stonewood.network.exception.UnauthorizedException

sealed class ApiResponse<out T : Any> {
    data class Success<out T : Any>(val data: T) : ApiResponse<T>()
    data class Error(val message: String) : ApiResponse<Nothing>()

    companion object {
        fun <T : Any> create(response: Response<T>): ApiResponse<T> {
            val callUrl = response.raw().request.url

            return when {
                response.isSuccessful -> {
                    val serverResponse = response.body()
                    if (serverResponse == null || response.code() == 204) {
                        throw Exception("Api call(\"$callUrl\") succeeded with empty body")
                    } else {
                        Success(serverResponse)
                    }
                }

                response.code() == 401 -> throw UnauthorizedException()

                else -> {
                    val errorResponseJson = response.errorBody()?.string()
                    if (errorResponseJson.isNullOrEmpty()) {
                        throw Exception("Api call(\"$callUrl\") failed with code: ${response.code()} and message: ${response.message()}")
                    }

                    val converter = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
                    val jsonAdapter = converter.adapter(ErrorResponseDto::class.java)
                    val errorResponse = jsonAdapter.fromJson(errorResponseJson)
                        ?: throw Exception("Error parsing error body: $errorResponseJson")

                    Error(errorResponse.message)
                }
            }
        }
    }
}

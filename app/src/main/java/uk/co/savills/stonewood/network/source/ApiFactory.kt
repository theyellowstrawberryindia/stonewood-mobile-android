package uk.co.savills.stonewood.network.source

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import uk.co.savills.stonewood.BASE_ADDRESS
import uk.co.savills.stonewood.BuildConfig
import uk.co.savills.stonewood.network.exception.NetworkUnavailableException
import uk.co.savills.stonewood.network.util.DeferredCallAdapterFactory
import java.util.concurrent.TimeUnit

class ApiFactory(
    getAccessToken: () -> String?,
    isNetworkAvailable: () -> Boolean,
) {
    private val authInterceptor = Interceptor { chain ->
        val request = chain.request()
        val shouldAddAuthHeader = request.headers["isAuthorizable"] != "false"

        val requestBuilder = request
            .newBuilder()
            .method(request.method, request.body)
            .removeHeader("isAuthorizable")

        if (shouldAddAuthHeader) {
            requestBuilder.addHeader("Authorization", "Bearer ${getAccessToken()}")
        }

        chain.proceed(requestBuilder.build())
    }

    private val acceptInterceptor = Interceptor { chain ->
        val request = chain.request()
        val newRequest = request
            .newBuilder()
            .addHeader("Accept", "application/json")
            .method(request.method, request.body)
            .build()

        chain.proceed(newRequest)
    }

    private val networkConnectionInterceptor = Interceptor { chain ->
        if (!isNetworkAvailable()) throw NetworkUnavailableException()

        chain.proceed(chain.request())
    }

    private val httpLoggingInterceptor = HttpLoggingInterceptor().apply {
        this.level =
            if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
    }

    fun create(): Api {
        val httpClient = OkHttpClient.Builder()
            .addInterceptor(networkConnectionInterceptor)
            .addInterceptor(authInterceptor)
            .addInterceptor(acceptInterceptor)
            .addInterceptor(httpLoggingInterceptor)
            .connectTimeout(CONNECTION_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .writeTimeout(READ_WRITE_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(READ_WRITE_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .build()

        val retrofit = Retrofit.Builder()
            .client(httpClient)
            .baseUrl(BASE_ADDRESS)
            .addConverterFactory(MoshiConverterFactory.create())
            .addCallAdapterFactory(DeferredCallAdapterFactory())
            .build()

        return retrofit.create(Api::class.java)
    }

    companion object {
        private const val READ_WRITE_TIMEOUT_SECONDS = 300L
        private const val CONNECTION_TIMEOUT_SECONDS = 300L
    }
}

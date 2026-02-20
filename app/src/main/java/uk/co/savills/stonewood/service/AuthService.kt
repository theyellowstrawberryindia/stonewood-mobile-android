package uk.co.savills.stonewood.service

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import uk.co.savills.stonewood.AppState
import uk.co.savills.stonewood.model.AuthTokenModel
import uk.co.savills.stonewood.model.UserModel
import uk.co.savills.stonewood.network.dto.RefreshAuthTokenRequestDto
import uk.co.savills.stonewood.network.source.Api
import uk.co.savills.stonewood.network.util.ApiResponse
import uk.co.savills.stonewood.util.SingleLiveEvent
import uk.co.savills.stonewood.util.mapper.mapToModel
import java.time.Instant

class AuthService(
    private val api: Api,
    private val appState: AppState
) {
    private val mutex = Mutex()

    val isLoggedIn
        get() = appState.profile != null

    val sessionInvalidatedEvent = SingleLiveEvent<Unit>()

    fun validateSession(user: UserModel) {
        appState.profile = user
    }

    fun invalidateSession() {
        appState.clear()

        sessionInvalidatedEvent.postValue(Unit)
    }

    suspend fun validateAuthToken() {
        mutex.withLock {
            val currentAuthToken = appState.profile?.authToken ?: return
            val expiryInstant = Instant.parse(currentAuthToken.expiryTimeStamp)
            val isTokenExpired = Instant.now().isAfter(expiryInstant)

            if (isTokenExpired) renewAccessToken(currentAuthToken)
        }
    }

    private suspend fun renewAccessToken(currentAuthToken: AuthTokenModel) {
        val request = RefreshAuthTokenRequestDto(
            currentAuthToken.refreshToken,
            currentAuthToken.accessToken
        )

        val response = api.refreshAuthToken(request).await()

        if (response is ApiResponse.Success) {
            appState.profile = mapToModel(response.data)
        }
    }
}

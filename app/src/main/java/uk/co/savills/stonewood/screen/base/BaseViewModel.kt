package uk.co.savills.stonewood.screen.base

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavDirections
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import uk.co.savills.stonewood.MainApplication
import uk.co.savills.stonewood.network.exception.NetworkUnavailableException
import uk.co.savills.stonewood.network.exception.ServerException
import uk.co.savills.stonewood.util.AppAnalytics
import uk.co.savills.stonewood.util.Result
import uk.co.savills.stonewood.util.SingleLiveEvent
import uk.co.savills.stonewood.util.connectionerror.ServerConnectionErrorDispatcher
import uk.co.savills.stonewood.util.navigation.NavigationCommand
import uk.co.savills.stonewood.util.navigation.Navigator

abstract class BaseViewModel(application: Application) :
    AndroidViewModel(application), Navigator, ServerConnectionErrorDispatcher {

    protected val application
        get() = getApplication<MainApplication>()

    protected val appContainer
        get() = application.appContainer

    protected val appState
        get() = appContainer.appState

    protected val apiService
        get() = appContainer.apiService

    override val navigationEvent = SingleLiveEvent<NavigationCommand>()

    protected val _isBusy = MutableLiveData<Boolean>()
    val isBusy: LiveData<Boolean>
        get() = _isBusy

    override val serverConnectionError = SingleLiveEvent<String>()
    override val noConnectionError = SingleLiveEvent<Unit?>()
    override val unexpectedError = SingleLiveEvent<String>()

    protected fun <T : Any> makeApiCall(
        call: suspend () -> Result<T>?,
        successCallback: ((T) -> Unit)? = null,
    ) {
        _isBusy.postValue(true)

        viewModelScope.launch(Dispatchers.IO) {
            when (val result = call()) {
                is Result.Success -> successCallback?.invoke(result.data)
                is Result.Error -> handleApiCallError(result.exception)
                null -> TODO()
            }
        }.invokeOnCompletion {
            _isBusy.postValue(false)
        }
    }

    protected fun handleApiCallError(error: Exception) {
        AppAnalytics.trackError(error)

        when (error) {
            is ServerException -> serverConnectionError.postValue(error.message)

            is NetworkUnavailableException -> noConnectionError.call()

            else -> unexpectedError.postValue(error.message)
        }
    }

    open fun handleUnexpectedError(error: Exception) {
        AppAnalytics.trackError(error)
        println(error.message)
        unexpectedError.postValue(error.message)
    }

    protected fun navigateTo(direction: NavDirections) {
        navigationEvent.postValue(
            NavigationCommand.Navigate(direction)
        )
    }

    fun navigateBack() {
        navigationEvent.postValue(
            NavigationCommand.PopBackStack
        )
    }
}

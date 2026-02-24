package uk.co.savills.stonewood.screen.login

import android.app.Application
import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import uk.co.savills.stonewood.FORGOT_PASSWORD_ADDRESS
import uk.co.savills.stonewood.screen.base.BaseViewModel
import uk.co.savills.stonewood.service.NotificationService
import uk.co.savills.stonewood.util.getNonNullValue
import uk.co.savills.stonewood.util.navigation.openExternalLink

class LoginViewModel(application: Application) : BaseViewModel(application) {

    val email = MutableLiveData("")
    val password = MutableLiveData("")

    private val _canLogin = MutableLiveData<Boolean>()
    val canLogin: LiveData<Boolean>
        get() = _canLogin

    init {
        email.observeForever { evaluateCredentials() }
        password.observeForever { evaluateCredentials() }

        isBusy.observeForever { isBusy ->
            if (!isBusy) {
                evaluateCredentials()
            } else {
                _canLogin.value = false
            }
        }
    }

    private fun evaluateCredentials() {
        _canLogin.value = email.getNonNullValue().isNotBlank() &&
            email.getNonNullValue().matches(Patterns.EMAIL_ADDRESS.toRegex()) &&
            password.getNonNullValue().isNotBlank()
    }

    fun login() {
        makeApiCall({
            apiService.login(email.getNonNullValue(), password.getNonNullValue())
        }) { user ->
            appContainer.authService.validateSession(user)

            NotificationService.register(application, user.id.toString())

            if (user.defaultPassword) {
                navigateToChangePassword()
            } else {
                navigateToHome()
            }
        }
    }

    private fun navigateToHome() {
        navigateTo(LoginFragmentDirections.moveToProjectListScreen())
    }

    private fun navigateToChangePassword() {
        navigateTo(LoginFragmentDirections.moveToChangePasswordScreen(isStartScreen = false))
    }

    fun onForgotPasswordClick() {
        application.openExternalLink(FORGOT_PASSWORD_ADDRESS)
    }
}

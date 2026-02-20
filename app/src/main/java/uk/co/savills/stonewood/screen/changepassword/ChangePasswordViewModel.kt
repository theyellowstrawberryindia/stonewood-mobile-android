package uk.co.savills.stonewood.screen.changepassword

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import uk.co.savills.stonewood.screen.base.BaseViewModel
import uk.co.savills.stonewood.util.SingleLiveEvent
import uk.co.savills.stonewood.util.getNonNullValue
import java.util.regex.Pattern

class ChangePasswordViewModel(application: Application) : BaseViewModel(application) {

    val password = MutableLiveData("")
    val newPassword = MutableLiveData("")
    val reenteredPassword = MutableLiveData("")

    private val _canChangePassword = MutableLiveData<Boolean>()
    val canChangePassword: LiveData<Boolean>
        get() = _canChangePassword

    val passwordMismatch = SingleLiveEvent<Nothing?>()

    init {
        password.observeForever { evaluatePasswords() }
        newPassword.observeForever { evaluatePasswords() }
        reenteredPassword.observeForever { evaluatePasswords() }
    }

    private fun evaluatePasswords() {
        _canChangePassword.value = password.getNonNullValue().isNotBlank() &&
            isValidPassword(newPassword.getNonNullValue()) &&
            reenteredPassword.getNonNullValue().isNotBlank()
    }

    fun isValidPassword(password: String) = Pattern.matches(PASSWORD_REGEX, password)

    fun changePassword() {
        if (newPassword.getNonNullValue() != reenteredPassword.getNonNullValue()) {
            passwordMismatch.call()
            return
        }

        makeApiCall({
            apiService.changePassword(
                password.getNonNullValue(),
                newPassword.getNonNullValue()
            )
        }) {
            appState.profile = requireNotNull(appState.profile).copy(defaultPassword = false)
            navigateToProjectListScreen()
        }
    }

    fun navigateToLogin() {
        navigateTo(ChangePasswordFragmentDirections.moveToLoginScreen())
    }

    private fun navigateToProjectListScreen() {
        navigateTo(ChangePasswordFragmentDirections.moveToProjectListScreen())
    }

    companion object {
        private const val SPECIAL_CHARACTERS_REGEX =
            "?=.*[\\u0020-\\u002F\\u003A-\\u0040\\u005B-\\u0060\\u007B-\\u007E]"
        private const val PASSWORD_REGEX =
            "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])($SPECIAL_CHARACTERS_REGEX).{8,}\$"
    }
}

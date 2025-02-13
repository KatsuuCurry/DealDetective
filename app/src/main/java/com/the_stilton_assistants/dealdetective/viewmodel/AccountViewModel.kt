package com.the_stilton_assistants.dealdetective.viewmodel

import android.net.Uri
import androidx.annotation.MainThread
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.the_stilton_assistants.dealdetective.model.ImagesSize
import com.the_stilton_assistants.dealdetective.model.NotificationFilter
import com.the_stilton_assistants.dealdetective.model.Settings
import com.the_stilton_assistants.dealdetective.repository.settings.ISettingsRepository
import com.the_stilton_assistants.dealdetective.repository.user.IUserAuthRepository
import com.the_stilton_assistants.dealdetective.util.INotificationBubbleHandler.NotificationType
import com.the_stilton_assistants.dealdetective.viewmodel.AccountSuccessMessages.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel to manage the user account.
 */
class AccountViewModel(
    private val userAuthRepository: IUserAuthRepository,
    private val settingsRepository: ISettingsRepository,
) : BaseViewModel() {
    /**
     * Holds the user state.
     */
    private var _accountUiMutableState: MutableStateFlow<AccountUiState> =
        MutableStateFlow(AccountUiState.Loading)
    val accountUiState: StateFlow<AccountUiState> = _accountUiMutableState.asStateFlow()

    /**
     * Holds the settings state.
     */
    private var _settingsUiMutableState: MutableStateFlow<SettingsUiState> =
        MutableStateFlow(SettingsUiState.Loading)
    val settingsUiState: StateFlow<SettingsUiState> = _settingsUiMutableState.asStateFlow()

    var isInitialized = false

    /**
     * Initializes the ViewModel.
     */
    @MainThread
    fun initialize() {
        if (isInitialized) return
        isInitialized = true

        viewModelScope.launch {
            userAuthRepository.getCurrentUser()
                .onSuccess { user ->
                    _accountUiMutableState.update {
                        if (user == null) {
                            AccountUiState.NoUser
                        } else {
                            AccountUiState.User(user = user)
                        }
                    }
                }
                .onError { error ->
                    _accountUiMutableState.value = AccountUiState.Error(error.message)
                }
        }
        viewModelScope.launch {
            settingsRepository.getSettingsFlow()
                .onSuccess { flow ->
                    flow.collect { settings ->
                        _settingsUiMutableState.update {
                            SettingsUiState.Display(settings)
                        }
                    }
                }
                .onError { error ->
                    _settingsUiMutableState.value = SettingsUiState.Error(error.message)
                }
        }
    }

    /**
     * Signs in the user.
     */
    fun signIn(email: String, password: String) {
        startOperation()
        viewModelScope.launch{
            userAuthRepository.signIn(email, password)
                .onSuccess { user ->
                    operationUiMutableState.value = OperationUiState.Success(
                        SignInSuccess.message
                    )
                    _accountUiMutableState.update { AccountUiState.User(user) }
                }
                .onError {
                    operationUiMutableState.value = OperationUiState.Error(it.message)
                }
        }
    }

    /**
     * Signs up the user.
     */
    fun signUp(email: String, password: String) {
        startOperation()
        viewModelScope.launch {
            userAuthRepository.signUp(email, password)
                .onSuccess { user ->
                    operationUiMutableState.value = OperationUiState.Success(
                        SignUpSuccess.message
                    )
                    _accountUiMutableState.update { AccountUiState.User(user) }
                }
                .onError {
                    operationUiMutableState.value = OperationUiState.Error(it.message)
                }
        }
    }

    /**
     * Signs out the user.
     */
    fun signOut() {
        startOperation()
        viewModelScope.launch {
            userAuthRepository.signOut()
                .onSuccess {
                    operationUiMutableState.value = OperationUiState.Success(
                        SignOutSuccess.message,
                        NotificationType.IMPORTANT
                    )
                    _accountUiMutableState.update { AccountUiState.NoUser }
                }
                .onError {
                    operationUiMutableState.value = OperationUiState.Error(it.message)
                }

        }
    }

    /**
     * Sends a password reset email.
     */
    fun sendPasswordResetEmail(email: String) {
        startOperation()
        viewModelScope.launch {
            userAuthRepository.sendPasswordResetEmail(email)
                .onSuccess {
                    operationUiMutableState.value = OperationUiState.Success(
                        PasswordResetEmailSent.message,
                        NotificationType.FORCE
                    )
                }
                .onError {
                    operationUiMutableState.value = OperationUiState.Error(it.message)
                }
        }
    }

    /**
     * Sends an email verification.
     */
    fun sendEmailVerification() {
        startOperation()
        viewModelScope.launch {
            userAuthRepository.sendEmailVerification()
                .onSuccess {
                    operationUiMutableState.value = OperationUiState.Success(
                        EmailVerificationSent.message,
                        NotificationType.FORCE
                    )
                }
                .onError {
                    operationUiMutableState.value = OperationUiState.Error(it.message)
                }
        }
    }

    /**
     * Updates the user account.
     */
    fun updateAccount(displayName: String, image: Uri?) {
        startOperation()
        viewModelScope.launch {
            val builder = UserProfileChangeRequest.Builder()
                .setDisplayName(displayName)
                .setPhotoUri(image)

            userAuthRepository.updateAccount(builder.build())
                .onSuccess {
                    operationUiMutableState.value = OperationUiState.Success(
                        AccountUpdated.message
                    )
                }
                .onError {
                    operationUiMutableState.value = OperationUiState.Error(it.message)
                }
        }
    }

    /**
     * Updates the settings.
     */
    fun updateSettings(
        imagesSize: ImagesSize? = null,
        boldText: Boolean? = null,
        notificationFilter: NotificationFilter? = null,
    ) {
        startOperation()
        viewModelScope.launch {
            val newSettings =
                (_settingsUiMutableState.value as SettingsUiState.Display).settings.toBuilder()

            imagesSize?.let {
                newSettings.setImagesSize(imagesSize)
            }
            boldText?.let {
                newSettings.setBoldText(boldText)
            }

            notificationFilter?.let {
                newSettings.setNotificationFilter(notificationFilter)
            }

            settingsRepository.updateSettings(newSettings.build())
                .onSuccess {
                    operationUiMutableState.value = OperationUiState.Success(
                        SettingsUpdated.message
                    )
                }
                .onError {
                    operationUiMutableState.value = OperationUiState.Error(it.message)
                }
        }
    }

    companion object {
        val Factory = viewModelFactory {
            initializer {
                val userAuthRepository = dealDetectiveAppContainer().userAuthRepository
                val settingsRepository = dealDetectiveAppContainer().settingsRepository
                AccountViewModel(
                    userAuthRepository = userAuthRepository,
                    settingsRepository = settingsRepository,
                )
            }
        }
    }
}

/**
 * Ui State for the Account.
 */
sealed interface AccountUiState {
    object Loading : AccountUiState
    object NoUser : AccountUiState
    data class User(
        val user: FirebaseUser,
    ) : AccountUiState
    data class Error(val message: String): AccountUiState
}

/**
 * Ui State for the Settings.
 */
sealed interface SettingsUiState {
    object Loading : SettingsUiState
    data class Display(val settings: Settings) : SettingsUiState
    data class Error(val message: String) : SettingsUiState
}

/**
 * Success messages for the AccountViewModel.
 */
sealed interface AccountSuccessMessages : SuccessMessage {
    object SignInSuccess : AccountSuccessMessages {
        override val message: String = "Accesso effettuato"
    }

    object SignUpSuccess : AccountSuccessMessages {
        override val message: String = "Registrazione effettuata"
    }

    object SignOutSuccess : AccountSuccessMessages {
        override val message: String = "Disconnessione effettuata"
    }

    object PasswordResetEmailSent : AccountSuccessMessages {
        override val message: String = "Email di reset password inviata"
    }

    object EmailVerificationSent : AccountSuccessMessages {
        override val message: String = "Email di verifica inviata"
    }

    object AccountUpdated : AccountSuccessMessages {
        override val message: String = "Account aggiornato"
    }

    object SettingsUpdated : AccountSuccessMessages {
        override val message: String = "Impostazioni aggiornate"
    }
}

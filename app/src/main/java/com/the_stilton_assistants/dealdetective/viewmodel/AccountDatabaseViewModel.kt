package com.the_stilton_assistants.dealdetective.viewmodel

import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.the_stilton_assistants.dealdetective.model.ImagesSize
import com.the_stilton_assistants.dealdetective.model.Settings
import com.the_stilton_assistants.dealdetective.repository.products.IProductsRepository
import com.the_stilton_assistants.dealdetective.repository.settings.ISettingsRepository
import com.the_stilton_assistants.dealdetective.repository.user.IUserAuthRepository
import com.the_stilton_assistants.dealdetective.repository.user.IUserDatabaseRepository
import com.the_stilton_assistants.dealdetective.util.NotificationType
import com.the_stilton_assistants.dealdetective.util.Result
import com.the_stilton_assistants.dealdetective.viewmodel.AccountDBErrorMessages.*
import com.the_stilton_assistants.dealdetective.viewmodel.AccountDBSuccessMessages.*
import kotlinx.coroutines.launch
import org.json.JSONObject

/**
 * ViewModel to retrieve all items in the Room database.
 */
class AccountDatabaseViewModel(
    private val userAuthRepository: IUserAuthRepository,
    private val userDatabaseRepository: IUserDatabaseRepository,
    private val productsRepository: IProductsRepository,
    private val settingsRepository: ISettingsRepository,
) : BaseViewModel() {

    fun uploadUserData() {
        startOperation()
        viewModelScope.launch {
            val userResult = userAuthRepository.getCurrentUser()

            if (userResult is Result.Error) {
                operationUiMutableState.value = OperationUiState.Error(userResult.error.message)
                return@launch
            }
            val settingsResult = settingsRepository.getSettings()

            if (settingsResult is Result.Error) {
                operationUiMutableState.value = OperationUiState.Error(settingsResult.error.message)
                return@launch
            }

            val user = (userResult as Result.Success).data

            if (user == null) {
                operationUiMutableState.value = OperationUiState.Error(
                    UserIsNull.message
                )
                return@launch
            }

            val settings = (settingsResult as Result.Success).data
            val jsonObj = JSONObject()

            jsonObj.put("imagesSize", settings.imagesSize.number)
            jsonObj.put("boldText", settings.boldText)

            userDatabaseRepository.uploadUserData(user, jsonObj)
                .onSuccess {
                    operationUiMutableState.value = OperationUiState.Success(
                        UploadUserDataSuccess.message,
                        NotificationType.FORCE
                    )
                }
                .onError {
                    operationUiMutableState.value = OperationUiState.Error(it.message)
                }
        }
    }

    fun retrieveUserData() {
        startOperation()
        viewModelScope.launch {
            val userResult = userAuthRepository.getCurrentUser()

            if (userResult is Result.Error) {
                operationUiMutableState.value = OperationUiState.Error(userResult.error.message)
                return@launch
            }
            val user = (userResult as Result.Success).data

            if (user == null) {
                operationUiMutableState.value = OperationUiState.Error(
                    UserIsNull.message
                )
                return@launch
            }

            userDatabaseRepository.retrieveUserData(user)
                .onSuccess { jsonObj ->
                    if (jsonObj.length() == 0) {
                        operationUiMutableState.value = OperationUiState.Error(
                            UserDataNotUploaded.message
                        )
                        return@onSuccess
                    }

                    if (!jsonObj.has("imagesSize") && jsonObj.get("imagesSize") !is Int) {
                        operationUiMutableState.value = OperationUiState.Error(
                            UserDataCorrupted.message
                        )
                        return@onSuccess
                    }

                    if (!jsonObj.has("boldText") && jsonObj.get("boldText") !is Boolean) {
                        operationUiMutableState.value = OperationUiState.Error(
                            UserDataCorrupted.message
                        )
                        return@onSuccess
                    }

                    val imagesSize = ImagesSize.forNumber(jsonObj.getInt("imagesSize"))

                    if (imagesSize == null) {
                        operationUiMutableState.value = OperationUiState.Error(
                            UserDataCorrupted.message
                        )
                        return@onSuccess
                    }

                    val newSettings = Settings.newBuilder()
                        .setImagesSize(imagesSize)
                        .setBoldText(jsonObj.getBoolean("boldText"))
                        .build()

                    settingsRepository.updateSettings(newSettings)
                        .onSuccess {
                            operationUiMutableState.value = OperationUiState.Success(
                                RetrieveUserDataSuccess.message,
                                NotificationType.FORCE
                            )
                        }
                        .onError {
                            operationUiMutableState.value = OperationUiState.Error(it.message)
                        }
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
                val userDatabaseRepository = dealDetectiveAppContainer().userDatabaseRepository
                val productsRepository = dealDetectiveAppContainer().productsRepository
                val settingsRepository = dealDetectiveAppContainer().settingsRepository
                AccountDatabaseViewModel(
                    userAuthRepository = userAuthRepository,
                    userDatabaseRepository = userDatabaseRepository,
                    productsRepository = productsRepository,
                    settingsRepository = settingsRepository,
                )
            }
        }
    }
}

sealed interface AccountDBSuccessMessages: SuccessMessage {
    object UploadUserDataSuccess: AccountDBSuccessMessages {
        override val message = "Caricamento dati utente avvenuto con successo"
    }
    object RetrieveUserDataSuccess: AccountDBSuccessMessages {
        override val message = "Recupero dati utente avvenuto con successo"
    }
}

sealed interface AccountDBErrorMessages: ErrorMessage {
    object UserIsNull: AccountDBErrorMessages {
        override val message = "L'utente è nullo"
    }
    object UserDataNotUploaded: AccountDBErrorMessages {
        override val message = "I dati dell'utente non sono stati caricati"
    }
    object UserDataCorrupted: AccountDBErrorMessages {
        override val message = "I dati dell'utente sono corrotti"
    }
}

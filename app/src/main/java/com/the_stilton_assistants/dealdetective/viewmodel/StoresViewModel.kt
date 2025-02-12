package com.the_stilton_assistants.dealdetective.viewmodel

import androidx.annotation.MainThread
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.the_stilton_assistants.dealdetective.model.Store
import com.the_stilton_assistants.dealdetective.model.StoreId
import com.the_stilton_assistants.dealdetective.model.StoreSettings
import com.the_stilton_assistants.dealdetective.repository.products.IProductsRepository
import com.the_stilton_assistants.dealdetective.repository.settings.IStoresSettingsRepository
import com.the_stilton_assistants.dealdetective.service.StoresServiceHandler
import com.the_stilton_assistants.dealdetective.util.Result
import com.the_stilton_assistants.dealdetective.viewmodel.StoresSuccessMessages.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class StoresViewModel(
    private val storesSettingsRepository: IStoresSettingsRepository,
    private val productsRepository: IProductsRepository,
    private val storesServiceHandler: StoresServiceHandler,
    private val appScope: CoroutineScope,
): BaseViewModel() {

    private var _storesUiMutableState: MutableStateFlow<StoresUiState> =
        MutableStateFlow(StoresUiState.Loading)
    val storesUiState: StateFlow<StoresUiState> = _storesUiMutableState.asStateFlow()

    var isInitialized = false

    @MainThread
    fun initialize() {
        if (isInitialized) return
        isInitialized = true

        viewModelScope.launch {
            val resultFlow = storesSettingsRepository.getStoresSettingsFlow()
            if (resultFlow is Result.Error) {
                _storesUiMutableState.update {
                    StoresUiState.Error(resultFlow.error.message)
                }
                return@launch
            }

            val flow = (resultFlow as Result.Success).data
            flow.collect { storeSettings ->
                _storesUiMutableState.update {
                    if (_storesUiMutableState.value is StoresUiState.Loading) {
                        StoresUiState.Display(
                            storeSettings = storeSettings.storesSettingsMap
                        )
                    } else {
                        (it as StoresUiState.Display).copy(
                            storeSettings = storeSettings.storesSettingsMap
                        )
                    }
                }
            }
        }
    }

    fun enableStore(storeId: Int) {
        startOperation()
        viewModelScope.launch {
            val store = Store.newBuilder().setCodPromo(-1).build()
            storesServiceHandler.retrieveSingleProducts(
                storeId = storeId,
                store = store,
                appScope = appScope,
            ).onSuccess {
                operationUiMutableState.value =
                    OperationUiState.Success(StoreEnabled.message)
            }.onError {
                operationUiMutableState.value = OperationUiState.Error(it.message)
            }.onServiceError { error ->
                operationUiMutableState.value = OperationUiState.Error(error.message)
            }
        }
    }

    fun disableStore(storeId: Int) {
        startOperation()
        viewModelScope.launch {
            withContext(NonCancellable) {
                storesSettingsRepository.disableStore(storeId)
                    .onSuccess {
                        productsRepository.deleteProductsByStoreType(storeId)
                            .onSuccess {
                                operationUiMutableState.value =
                                    OperationUiState.Success(StoreDisabled.message)
                            }.onError {
                                operationUiMutableState.value = OperationUiState.Error(it.message)
                            }
                    }.onError {
                        operationUiMutableState.value = OperationUiState.Error(it.message)
                    }
            }
        }
    }

    fun insertStore(storeId: StoreId, url: String) {
        startOperation()
        viewModelScope.launch {
            storesServiceHandler.saveNewStore(
                storeId = storeId,
                url = url,
                appScope = appScope,
            ).onSuccess {
                operationUiMutableState.value = OperationUiState.Success(StoreAdded.message)
            }.onError {
                operationUiMutableState.value = OperationUiState.Error(it.message)
            }.onServiceError {
                operationUiMutableState.value = OperationUiState.Error(it.message)
            }
        }
    }

    fun removeStore(storeId: Int) {
        startOperation()
        viewModelScope.launch {
            withContext(NonCancellable) {
                storesSettingsRepository.removeStore(storeId)
                    .onSuccess {
                        productsRepository.deleteProductsByStoreType(storeId)
                            .onSuccess {
                                operationUiMutableState.value =
                                    OperationUiState.Success(StoreRemoved.message)
                            }.onError {
                                operationUiMutableState.value = OperationUiState.Error(it.message)
                            }
                    }.onError {
                        operationUiMutableState.value = OperationUiState.Error(it.message)
                    }
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val storesSettingsRepository = dealDetectiveAppContainer().storesSettingsRepository
                val productsRepository = dealDetectiveAppContainer().productsRepository
                val storesServiceHandler = dealDetectiveAppContainer().storesServiceHandler
                val appScope = dealDetectiveAppCoroutineScope()
                StoresViewModel(
                    storesSettingsRepository = storesSettingsRepository,
                    productsRepository = productsRepository,
                    storesServiceHandler = storesServiceHandler,
                    appScope = appScope,
                )
            }
        }
    }
}

/**
 * Ui State for Supermarkets screen.
 */
sealed interface StoresUiState {
    object Loading : StoresUiState
    data class Display(val storeSettings: Map<Int, StoreSettings>) : StoresUiState
    data class Error(val message: String) : StoresUiState
}

sealed interface StoresSuccessMessages : SuccessMessage {
    object StoreEnabled : StoresSuccessMessages {
        override val message = "Negozio Abilitato"
    }
    object StoreDisabled : StoresSuccessMessages {
        override val message = "Negozio Disabilitato"
    }
    object StoreAdded : StoresSuccessMessages {
        override val message = "Negozio Aggiunto"
    }
    object StoreRemoved : StoresSuccessMessages {
        override val message = "Negozio Rimosso"
    }
}

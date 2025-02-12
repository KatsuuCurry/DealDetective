package com.the_stilton_assistants.dealdetective.ui.stores

import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.koalas.trackmybudget.ui.utils.getColumnModifier
import com.koalas.trackmybudget.ui.utils.getScrollBehaviorAndModifier
import com.the_stilton_assistants.dealdetective.R
import com.the_stilton_assistants.dealdetective.model.StoreId
import com.the_stilton_assistants.dealdetective.ui.common.ErrorText
import com.the_stilton_assistants.dealdetective.ui.common.LoadingComponent
import com.the_stilton_assistants.dealdetective.ui.common.NavigationBottomBar
import com.the_stilton_assistants.dealdetective.ui.common.TopBar
import com.the_stilton_assistants.dealdetective.ui.navigation.EsselungaRoute
import com.the_stilton_assistants.dealdetective.ui.navigation.ScreenRoute
import com.the_stilton_assistants.dealdetective.ui.navigation.StoresRoute
import com.the_stilton_assistants.dealdetective.ui.utils.handleOperationState
import com.the_stilton_assistants.dealdetective.viewmodel.OperationUiState
import com.the_stilton_assistants.dealdetective.viewmodel.StoresUiState
import com.the_stilton_assistants.dealdetective.viewmodel.StoresViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoresScreen(
    modifier: Modifier,
    navLambda: (ScreenRoute) -> Unit,
    viewModel: StoresViewModel = viewModel(
        viewModelStoreOwner = LocalActivity.current!! as ComponentActivity,
        factory = StoresViewModel.Factory
    ),
) {
    LaunchedEffect(Unit) {
        viewModel.initialize()
    }
    val enabled = handleOperationState(viewModel = viewModel)
    val (scrollBehavior, scaffoldModifier) = getScrollBehaviorAndModifier(modifier)
    Scaffold(
        modifier = scaffoldModifier,
        bottomBar = {
            NavigationBottomBar(
                modifier = modifier,
                navLambda = navLambda,
                currentDestination = StoresRoute,
            )
        },
        topBar = {
            TopBar(
                modifier = modifier,
                title = "Negozi",
                scrollBehavior = scrollBehavior,
            )
        },
    ) { innerPadding ->
        val columnModifier = getColumnModifier(modifier, innerPadding)
        val storesUiState by viewModel.storesUiState.collectAsStateWithLifecycle()
        if (storesUiState is StoresUiState.Loading) {
            LoadingComponent(
                modifier = modifier.padding(innerPadding),
            )
            return@Scaffold
        }

        if (storesUiState is StoresUiState.Error) {
            ErrorText(
                modifier = modifier.padding(innerPadding),
                text = (storesUiState as StoresUiState.Error).message,
            )
            return@Scaffold
        }

        val stores = (storesUiState as StoresUiState.Display)
        Column(
            modifier = columnModifier
                .fillMaxSize(),
        ) {
            SelectableStore(
                modifier = modifier,
                storeSettings = stores.storeSettings[StoreId.ESSELUNGA.value]!!,
                navLambda = navLambda,
                viewModel = viewModel,
                navRoute = EsselungaRoute,
                storeId = StoreId.ESSELUNGA.value,
                storeName = "Esselunga",
                storeLogo = R.drawable.esselunga_logo,
                enabled = enabled,
            )
            ToggleStore(
                modifier = modifier,
                storeSettings = stores.storeSettings[StoreId.CARREFOUR.value]!!,
                viewModel = viewModel,
                storeId = StoreId.CARREFOUR.value,
                storeName = "Carrefour",
                storeLogo = R.drawable.carrefour_logo,
                enabled = enabled,
            )
            ToggleStore(
                modifier = modifier,
                storeSettings = stores.storeSettings[StoreId.TIGROS.value]!!,
                viewModel = viewModel,
                storeId = StoreId.TIGROS.value,
                storeName = "Tigros",
                storeLogo = R.drawable.tigros_logo,
                enabled = enabled,
            )

            val operationUiState by viewModel.operationUiState.collectAsStateWithLifecycle()
            if (operationUiState is OperationUiState.Loading) {
                LoadingComponent(
                    modifier = modifier,
                )
            }
        }
    }
}

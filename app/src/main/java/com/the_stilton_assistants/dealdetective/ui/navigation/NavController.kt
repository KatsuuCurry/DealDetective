package com.the_stilton_assistants.dealdetective.ui.navigation

import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.the_stilton_assistants.dealdetective.MainActivity
import com.the_stilton_assistants.dealdetective.ui.DealsScreen
import com.the_stilton_assistants.dealdetective.ui.ProductScreen
import com.the_stilton_assistants.dealdetective.ui.ProductsScreen
import com.the_stilton_assistants.dealdetective.ui.SettingsScreen
import com.the_stilton_assistants.dealdetective.ui.ShoppingListScreen
import com.the_stilton_assistants.dealdetective.ui.account.AccountScreen
import com.the_stilton_assistants.dealdetective.ui.account.EditAccountScreen
import com.the_stilton_assistants.dealdetective.ui.account.LoginScreen
import com.the_stilton_assistants.dealdetective.ui.account.RegisterScreen
import com.the_stilton_assistants.dealdetective.ui.stores.EsselungaScreen
import com.the_stilton_assistants.dealdetective.ui.stores.StoresScreen
import com.the_stilton_assistants.dealdetective.viewmodel.AccountUiState
import com.the_stilton_assistants.dealdetective.viewmodel.AccountViewModel
import com.the_stilton_assistants.dealdetective.viewmodel.SettingsUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavController(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    accountViewModel: AccountViewModel = viewModel(
        viewModelStoreOwner = LocalActivity.current!! as ComponentActivity,
        factory = AccountViewModel.Factory,
    ),
) {
    LaunchedEffect(Unit) {
        accountViewModel.initialize()
    }

    val navLambda = { route: ScreenRoute ->
        navController.navigate(route) {
            launchSingleTop = true
            restoreState = true
        }
    }

    val navLambdaRemoveLast = { route: ScreenRoute ->
        navController.navigate(route) {
            popUpTo(SettingsRoute) {
                inclusive = true
            }
            launchSingleTop = true
        }
    }

    val navBackLambda = {
        navController.popBackStack()
    }

    NavHost(
        navController = navController,
        startDestination = DealsRoute,
        modifier = modifier.fillMaxSize(),
    ) {
        composable<ProductsRoute> {
            ProductsScreen(
                modifier = modifier,
                navLambda = navLambda,
                settingsUiState = accountViewModel.settingsUiState,
            )
        }

        composable<DealsRoute> {
            DealsScreen(
                modifier = modifier,
                navLambda = navLambda,
                settingsUiState = accountViewModel.settingsUiState,
            )
        }

        composable<ShoppingListRoute> {
            ShoppingListScreen(
                modifier = modifier,
                navLambda = navLambda,
                navBackLambda = navBackLambda,
                settingsUiState = accountViewModel.settingsUiState,
            )
        }

        composable<ProductRoute> { backStackEntry ->
            val productRoute: ProductRoute = backStackEntry.toRoute()
            val storeId = productRoute.storeId
            val productName = productRoute.productName
            ProductScreen(
                modifier = modifier,
                storeId = storeId,
                productName = productName,
                navBackLambda = navBackLambda,
            )
        }

        composable<StoresRoute> {
            StoresScreen(
                modifier = modifier,
                navLambda = navLambda,
            )
        }

        composable<SettingsRoute> {
            SettingsScreen(
                modifier = modifier,
                navLambda = navLambda,
                viewModel = accountViewModel,
            )
        }

        composable<EsselungaRoute> {
            EsselungaScreen(
                modifier = modifier,
                navBackLambda = navBackLambda,
            )
        }

        composable<AccountRoute> {
            val user = accountViewModel.accountUiState

            if (user.value is AccountUiState.NoUser) {
                LoginScreen(
                    modifier = modifier,
                    navLambda = navLambdaRemoveLast,
                    viewModel = accountViewModel,
                )
            } else {
                AccountScreen(
                    modifier = modifier,
                    navLambda = navLambdaRemoveLast,
                    accountViewModel = accountViewModel,
                )
            }
        }

        composable<LoginRoute> {
            LoginScreen(
                modifier = modifier,
                navLambda = navLambdaRemoveLast,
                viewModel = accountViewModel,
            )
        }

        composable<RegisterRoute> {
            RegisterScreen(
                modifier = modifier,
                navLambda = navLambdaRemoveLast,
                viewModel = accountViewModel,
            )
        }

        composable<EditAccountRoute> {
            EditAccountScreen(
                modifier = modifier,
                navLambda = navLambdaRemoveLast,
                viewModel = accountViewModel,
            )
        }
    }


    val settingsUiState by accountViewModel.settingsUiState.collectAsStateWithLifecycle()

    if (settingsUiState !is SettingsUiState.Display) {
        return
    }

    val settings = (settingsUiState as SettingsUiState.Display).settings
    val activityContainer = (LocalActivity.current!! as MainActivity).activityContainer
    LaunchedEffect(settings.notificationFilter) {
        activityContainer.notificationBubbleHandler.notificationFilter = settings.notificationFilter
    }
}

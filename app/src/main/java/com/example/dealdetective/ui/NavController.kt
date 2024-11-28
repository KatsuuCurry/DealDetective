package com.example.dealdetective.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.dealdetective.ui.viewmodel.AppViewModel
import com.example.dealdetective.ui.viewmodel.AppViewModelProvider

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavController(
    modifier: Modifier = Modifier,
    appViewModel: AppViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val navController = rememberNavController()

    val navLambda = { route: ScreenRoute ->
        navController.navigate(route) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    val navBackLambda = {
        navController.popBackStack()
    }

    NavHost(
        navController = navController,
        startDestination = DealsScreen,
        modifier = modifier.fillMaxSize(),
    ) {
        composable<CategoriesScreen> {
            CategoriesScreen(
                modifier = modifier,
                navLambda = navLambda,
            )
        }

        composable<DealsScreen> {
            DealsScreen(
                modifier = modifier,
                navLambda = navLambda,
                appViewModel = appViewModel,
            )
        }

        composable<SupermarketsScreen> {
            Text(
                modifier = modifier,
                text = "Supermarkets Screen",
            )
        }

        composable<SettingsScreen> {
            SettingsScreen(
                modifier = modifier,
                navLambda = navLambda,

            )
        }

        composable<EsselungaWebViewScreen> {
            EsselungaScreen(
                modifier = modifier,
                navBackLambda = navBackLambda,
            )
        }
    }
}
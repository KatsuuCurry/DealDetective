package com.the_stilton_assistants.dealdetective.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material.icons.outlined.Settings
import com.the_stilton_assistants.dealdetective.R
import kotlinx.serialization.Serializable

interface ScreenRoute

@Serializable
object ProductsRoute: ScreenRoute
@Serializable
object DealsRoute: ScreenRoute
@Serializable
object ShoppingListRoute: ScreenRoute
@Serializable
object StoresRoute: ScreenRoute
@Serializable
object SettingsRoute: ScreenRoute

@Serializable
data class ProductRoute(val storeId: Int, val productName: String) : ScreenRoute

@Serializable
object EsselungaRoute: ScreenRoute

@Serializable
object AccountRoute: ScreenRoute

@Serializable
object LoginRoute: ScreenRoute

@Serializable
object RegisterRoute: ScreenRoute

@Serializable
object EditAccountRoute: ScreenRoute

data class Route<T : Any>(val name: String, val route: T, val iconSelected: Any, val iconUnselected: Any)


val MainRoutesList = listOf(
    Route("Prodotti", ProductsRoute, R.drawable.shopping_bag, R.drawable.shopping_bag_outline),
    Route("Migliori Sconti", DealsRoute, R.drawable.discount_filled, R.drawable.discount_outline),
    Route("Negozi", StoresRoute, Icons.Filled.Place, Icons.Outlined.Place),
    Route("Impostazioni", SettingsRoute, Icons.Filled.Settings, Icons.Outlined.Settings)
)

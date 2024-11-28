package com.example.dealdetective.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.rounded.DateRange
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.serialization.Serializable

interface ScreenRoute

@Serializable
object CategoriesScreen: ScreenRoute
@Serializable
object DealsScreen: ScreenRoute
@Serializable
object SupermarketsScreen: ScreenRoute
@Serializable
object SettingsScreen: ScreenRoute

@Serializable
object EsselungaWebViewScreen: ScreenRoute

data class Route<T : Any>(val name: String, val route: T, val icon: ImageVector)

val MainRoutesList = listOf(
    Route("Categorie", CategoriesScreen, Icons.Rounded.DateRange),
    Route("Migliori Sconti", DealsScreen, Icons.Default.Star),
    Route("Supermercati", SupermarketsScreen, Icons.Rounded.DateRange),
    Route("Settings Screen", SettingsScreen, Icons.Rounded.Settings)
)

enum class ProductsOrder {
    ASC, DESC
}
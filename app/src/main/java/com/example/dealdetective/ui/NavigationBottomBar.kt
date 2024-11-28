package com.example.dealdetective.ui

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun NavigationBottomBar(
    modifier: Modifier = Modifier,
    navLambda: (ScreenRoute) -> Unit,
    currentDestination: ScreenRoute,
) {
    NavigationBar (
        modifier = modifier,
    ) {
        MainRoutesList.forEachIndexed { index, routeObj ->
            NavigationBarItem(
                icon = { Icon(routeObj.icon, contentDescription = routeObj.name) },
                label = { Text(routeObj.name) },
                selected = currentDestination == routeObj.route,
                onClick = {
                    navLambda(routeObj.route)
                }
            )
        }
    }
}
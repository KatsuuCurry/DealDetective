package com.the_stilton_assistants.dealdetective.ui.common

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import com.the_stilton_assistants.dealdetective.ui.navigation.MainRoutesList
import com.the_stilton_assistants.dealdetective.ui.navigation.ScreenRoute

@Composable
fun NavigationBottomBar(
    modifier: Modifier = Modifier,
    navLambda: (ScreenRoute) -> Unit,
    currentDestination: ScreenRoute,
) {
    NavigationBar (
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
    ) {
        MainRoutesList.forEachIndexed { index, routeObj ->
            NavigationBarItem(
                icon = {
                    val image = if (currentDestination == routeObj.route) routeObj.iconSelected else routeObj.iconUnselected
                    if (image is Int) {
                        Icon(
                            modifier = modifier,
                            painter = painterResource(id = image),
                            contentDescription = routeObj.name,
                        )
                    } else if (image is ImageVector) {
                        Icon(
                            modifier = modifier,
                            imageVector = image,
                            contentDescription = routeObj.name,
                        )
                    }
                },
                label = { Text(
                    modifier = modifier,
                    text = routeObj.name,
                    textAlign = TextAlign.Center,
                ) },
                selected = currentDestination == routeObj.route,
                enabled = currentDestination != routeObj.route,
                onClick = {
                    navLambda(routeObj.route)
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = MaterialTheme.colorScheme.primary,
                    disabledIconColor = MaterialTheme.colorScheme.onPrimary,
                    disabledTextColor = MaterialTheme.colorScheme.onPrimary,
                    selectedIconColor = MaterialTheme.colorScheme.onSecondary,
                    selectedTextColor = MaterialTheme.colorScheme.onSecondary,
                ),
            )
        }
    }
}

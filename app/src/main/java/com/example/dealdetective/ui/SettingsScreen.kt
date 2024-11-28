package com.example.dealdetective.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    navLambda: (ScreenRoute) -> Unit,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
        },
        bottomBar = {
            NavigationBottomBar(
                modifier = modifier,
                navLambda = navLambda,
                currentDestination = SettingsScreen,
            )
        },
    ) { innerPadding ->
        Column(
            modifier = modifier.padding(innerPadding),
        ) {
            IconButton(
                modifier = modifier,
                onClick = { navLambda(EsselungaWebViewScreen) },
            ) {
                Icon(
                    modifier = modifier,
                    imageVector = Icons.Default.ShoppingCart,
                    contentDescription = "Esselunga",
                )
            }
        }
    }
}
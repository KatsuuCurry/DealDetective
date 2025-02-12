package com.the_stilton_assistants.dealdetective.ui.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.the_stilton_assistants.dealdetective.R
import com.the_stilton_assistants.dealdetective.ui.navigation.ScreenRoute
import com.the_stilton_assistants.dealdetective.ui.utils.appContainer
import com.the_stilton_assistants.dealdetective.util.WifiStatus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    modifier: Modifier = Modifier,
    title: String,
    navBackLambda: (() -> Boolean)? = null,
    navLambda: ((ScreenRoute) -> Unit)? = null,
    navLambdaRoute: ScreenRoute? = null,
    wifiStatusState: WifiStatus.Status? = null,
    scrollBehavior: TopAppBarScrollBehavior? = null,
) {
    CenterAlignedTopAppBar(
        modifier = modifier,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            actionIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        ),
        title = {
            Text(
                modifier = modifier,
                text = title,
            )
        },
        actions = {
            if (wifiStatusState == null) {
                val wifiStatusState by appContainer().wifiStatusState.collectAsStateWithLifecycle()
                if (wifiStatusState != WifiStatus.Status.Available) {
                    Icon(
                        modifier = modifier.padding(horizontal = 8.dp),
                        painter = painterResource(id = R.drawable.wifi_off),
                        contentDescription = "Wifi Off",
                        tint = MaterialTheme.colorScheme.error,
                    )
                }
            } else {
                if (wifiStatusState != WifiStatus.Status.Available) {
                    Icon(
                        modifier = modifier.padding(horizontal = 8.dp),
                        painter = painterResource(id = R.drawable.wifi_off),
                        contentDescription = "Wifi Off",
                        tint = MaterialTheme.colorScheme.error,
                    )
                }
            }
            Image(
                painter = painterResource(id = R.drawable.app_logo),
                contentDescription = "App Logo",
                modifier = modifier
                    .size(48.dp),
            )
        },
        navigationIcon = {
            navBackLambda?.let {
                IconButton(
                    modifier = modifier,
                    onClick = { it() },
                ) {
                    Icon(
                        modifier = modifier,
                        imageVector = Icons.AutoMirrored.Default.ArrowBack,
                        contentDescription = "Back",
                    )
                }
            }
            navLambda?.let {
                require(navLambdaRoute != null)
                IconButton(
                    modifier = modifier,
                    onClick = { it(navLambdaRoute) },
                ) {
                    Icon(
                        modifier = modifier,
                        imageVector = Icons.AutoMirrored.Default.ArrowBack,
                        contentDescription = "Back",
                    )
                }
            }
        },
        scrollBehavior = scrollBehavior,
    )
}

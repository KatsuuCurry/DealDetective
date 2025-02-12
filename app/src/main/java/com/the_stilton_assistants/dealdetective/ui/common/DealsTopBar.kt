package com.the_stilton_assistants.dealdetective.ui.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
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
import com.the_stilton_assistants.dealdetective.service.StoresRetrievingState
import com.the_stilton_assistants.dealdetective.ui.utils.appContainer
import com.the_stilton_assistants.dealdetective.ui.utils.appScope
import com.the_stilton_assistants.dealdetective.util.WifiStatus
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DealsTopBar(
    modifier: Modifier = Modifier,
    title: String,
    actions: @Composable (Modifier) -> Unit = {},
    scrollBehavior: TopAppBarScrollBehavior? = null,
) {
    val storesServiceHandler = appContainer().storesServiceHandler
    val storesRetrievingState by storesServiceHandler.storesRetrievingState.collectAsStateWithLifecycle()
    val wifiStatusState by appContainer().wifiStatusState.collectAsStateWithLifecycle()
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
            if (wifiStatusState != WifiStatus.Status.Available) {
                Icon(
                    modifier = modifier,
                    imageVector = Icons.Default.Warning,
                    contentDescription = "Search",
                )
            }
            Image(
                painter = painterResource(id = R.drawable.app_logo),
                contentDescription = "App Logo",
                modifier = modifier
                    .size(48.dp),
            )
        },
        navigationIcon = {
            Row(
                modifier = modifier,
            ) {
                if (storesRetrievingState is StoresRetrievingState.Running) {
                    CircularProgressIndicator(
                        modifier = modifier,
                        color = MaterialTheme.colorScheme.primary,
                    )
                } else {
                    val appScope = appScope()
                    IconButton(
                        onClick = {
                            appScope.launch {
                                storesServiceHandler.retrieveFromAllStores()
                            }
                        },
                        enabled = wifiStatusState == WifiStatus.Status.Available,
                    ) {
                        Icon(
                            modifier = modifier,
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Search",
                        )
                    }
                }
                actions(modifier)
            }
        },
        scrollBehavior = scrollBehavior,
    )
}

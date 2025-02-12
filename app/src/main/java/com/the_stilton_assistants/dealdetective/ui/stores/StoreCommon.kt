package com.the_stilton_assistants.dealdetective.ui.stores

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.the_stilton_assistants.dealdetective.model.StoreSettings
import com.the_stilton_assistants.dealdetective.model.storeOrNull
import com.the_stilton_assistants.dealdetective.ui.navigation.EsselungaRoute
import com.the_stilton_assistants.dealdetective.ui.navigation.ScreenRoute
import com.the_stilton_assistants.dealdetective.ui.utils.appContainer
import com.the_stilton_assistants.dealdetective.ui.utils.isWifiAvailable
import com.the_stilton_assistants.dealdetective.viewmodel.StoresViewModel

@Composable
fun SelectableStore(
    modifier: Modifier,
    storeSettings: StoreSettings,
    navLambda: (ScreenRoute) -> Unit,
    viewModel: StoresViewModel,
    navRoute: ScreenRoute,
    storeId: Int,
    storeName: String,
    storeLogo: Int,
    enabled: Boolean,
) {
    Row(
        modifier = modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painter = painterResource(id = storeLogo),
            contentDescription = "Store Logo",
            modifier = modifier
                .weight(0.2f)
                .padding(4.dp)
                .size(64.dp),
        )
        val textWeight = if (storeSettings.storeOrNull == null) {
            0.8f
        } else {
            0.6f
        }
        Text(
            modifier = modifier
                .weight(textWeight)
                .padding(4.dp),
            text = storeName,
            style = MaterialTheme.typography.titleLarge,
        )
        if (storeSettings.storeOrNull != null) {
            IconButton(
                modifier = modifier
                    .weight(0.2f)
                    .padding(4.dp),
                onClick = {
                    navLambda(EsselungaRoute)
                },
                enabled = enabled,
            ) {
                Icon(
                    modifier = modifier,
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit",
                )
            }
        }
        val wifiStatusState by appContainer().wifiStatusState.collectAsStateWithLifecycle()
        Checkbox(
            modifier = modifier
                .weight(0.2f)
                .padding(4.dp),
            checked = storeSettings.storeOrNull != null,
            onCheckedChange = {
                if (storeSettings.storeOrNull == null) {
                    navLambda(navRoute)
                } else {
                    viewModel.removeStore(storeId)
                }
            },
            enabled = enabled && (isWifiAvailable(wifiStatusState) || storeSettings.storeOrNull != null),
        )
    }
}

@Composable
fun ToggleStore(
    modifier: Modifier,
    storeSettings: StoreSettings,
    viewModel: StoresViewModel,
    storeId: Int,
    storeName: String,
    storeLogo: Int,
    enabled: Boolean,
) {
    Row(
        modifier = modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painter = painterResource(id = storeLogo),
            contentDescription = "Store Logo",
            modifier = modifier
                .weight(0.2f)
                .padding(4.dp)
                .size(64.dp),
        )
        Text(
            modifier = modifier
                .weight(0.8f)
                .padding(4.dp),
            text = storeName,
            style = MaterialTheme.typography.titleLarge,
        )
        val wifiStatusState by appContainer().wifiStatusState.collectAsStateWithLifecycle()
        Checkbox(
            modifier = modifier
                .weight(0.2f)
                .padding(4.dp),
            checked = storeSettings.storeOrNull != null,
            onCheckedChange = {
                if (storeSettings.storeOrNull == null) {
                    viewModel.enableStore(storeId)
                } else {
                    viewModel.disableStore(storeId)
                }
            },
            enabled = enabled && (isWifiAvailable(wifiStatusState) || storeSettings.storeOrNull != null),
        )
    }
}

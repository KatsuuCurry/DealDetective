package com.example.dealdetective.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.dealdetective.DealDetectiveApplication
import com.example.dealdetective.ui.viewmodel.ProductViewModel
import com.example.dealdetective.util.WifiStatus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    modifier: Modifier = Modifier,
    title: String,
    viewModel: ProductViewModel,
) {
    CenterAlignedTopAppBar(
        modifier = modifier,
        title = {
            Text(
                modifier = modifier,
                text = title,
            )
        },
        actions = {
            IconButton(
                modifier = modifier,
                onClick = { viewModel.searchQuery = "kg" },
            ) {
                Icon(
                    modifier = modifier,
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                )
            }
            val wifiStatusFlow by (LocalContext.current.applicationContext as DealDetectiveApplication).container.wifiStatusState.collectAsStateWithLifecycle()
            if (wifiStatusFlow != WifiStatus.Status.Available) {
                Icon(
                    modifier = modifier,
                    imageVector = Icons.Default.Warning,
                    contentDescription = "Search",
                )
            }
        },

    )
}
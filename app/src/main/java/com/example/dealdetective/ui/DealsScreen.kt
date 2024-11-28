package com.example.dealdetective.ui

import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.dealdetective.ui.viewmodel.AppViewModel
import com.example.dealdetective.ui.viewmodel.AppViewModelProvider
import com.example.dealdetective.ui.viewmodel.DealsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DealsScreen(
    modifier: Modifier = Modifier,
    navLambda: (ScreenRoute) -> Unit,
    appViewModel: AppViewModel,
    viewModel: DealsViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    Scaffold(
        modifier = modifier,
        bottomBar = {
            NavigationBottomBar(
                modifier = modifier,
                navLambda = navLambda,
                currentDestination = DealsScreen,
            )
        },
        topBar = {
            TopBar(
                modifier = modifier,
                title = "Deals",
                viewModel = viewModel,
            )
        },
    ) { innerPadding ->
        val uiState by viewModel.dealsUiState.collectAsStateWithLifecycle()

        if (uiState.productList.isEmpty()) {
            Text(
                modifier = modifier.padding(innerPadding),
                text = "No Products",
            )
        } else {
            LazyVerticalGrid(
                modifier = modifier.padding(innerPadding),
                columns = GridCells.Adaptive(200.dp),
            ) {
                item("search", span = { GridItemSpan(maxCurrentLineSpan) }) {
                    TextField(
                        modifier = modifier.fillMaxWidth(),
                        value = viewModel.searchQuery,
                        onValueChange = { viewModel.searchQuery = it },
                        label = { Text("Search") },
                        leadingIcon = {
                            Icon(
                                modifier = modifier,
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search",
                            )
                        },
                        placeholder = { Text("Search") },
                    )
                }
                items(uiState.productList) { product ->
                    Column {
                        Text(
                            modifier = modifier,
                            text = product.productName
                        )
                        Text(
                            modifier = modifier,
                            text = product.storeId.toString()
                        )
                    }
                }
            }
        }
    }
}
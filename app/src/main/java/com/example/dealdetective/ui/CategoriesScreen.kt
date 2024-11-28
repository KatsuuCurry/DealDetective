package com.example.dealdetective.ui

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.dealdetective.ui.viewmodel.AppViewModelProvider
import com.example.dealdetective.ui.viewmodel.CategoriesViewModel

@Composable
fun CategoriesScreen(
    modifier: Modifier = Modifier,
    navLambda: (ScreenRoute) -> Unit,
    viewModel: CategoriesViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    Scaffold(
        modifier = modifier,
        bottomBar = {
            NavigationBottomBar(
                modifier = modifier,
                navLambda = navLambda,
                currentDestination = CategoriesScreen,
            )
        },
        topBar = {
            TopBar(
                modifier = modifier,
                title = "Categories",
                viewModel = viewModel,
            )
        },
    ) { innerPadding ->
        Column(
            modifier = modifier.padding(innerPadding),
        ) {
            val scrollState = rememberScrollState()
            Row(
                modifier = modifier.horizontalScroll(scrollState)
                    .fillMaxWidth(),
            ) {
                Button(
                    modifier = modifier,
                    onClick = { }
                ) {
                    Icon(
                        modifier = modifier,
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add",
                    )
                }
                Button(
                    modifier = modifier,
                    onClick = { }
                ) {
                    Text(
                        modifier = modifier,
                        text = "Add",
                    )
                }
                viewModel.categoriesList.forEach { category ->
                    Button(
                        modifier = modifier,
                        onClick = { }
                    ) {
                        Text(
                            modifier = modifier,
                            text = category,
                        )
                    }
                }
            }
            val uiState by viewModel.categoriesUiState.collectAsStateWithLifecycle()
            LazyVerticalGrid(
                modifier = modifier.fillMaxSize(),
                columns = GridCells.Adaptive(150.dp),
                contentPadding = PaddingValues(8.dp),
            ) {
                items(uiState.filteredProductList) { product ->
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
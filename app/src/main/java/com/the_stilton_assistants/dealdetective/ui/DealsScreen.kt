package com.the_stilton_assistants.dealdetective.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.koalas.trackmybudget.ui.utils.getScrollBehaviorAndModifier
import com.the_stilton_assistants.dealdetective.ui.common.DealsTopBar
import com.the_stilton_assistants.dealdetective.ui.common.ErrorText
import com.the_stilton_assistants.dealdetective.ui.common.FiltersRow
import com.the_stilton_assistants.dealdetective.ui.common.FloatingActionComponent
import com.the_stilton_assistants.dealdetective.ui.common.LoadingComponent
import com.the_stilton_assistants.dealdetective.ui.common.NavigationBottomBar
import com.the_stilton_assistants.dealdetective.ui.common.ProductCard
import com.the_stilton_assistants.dealdetective.ui.navigation.DealsRoute
import com.the_stilton_assistants.dealdetective.ui.navigation.ScreenRoute
import com.the_stilton_assistants.dealdetective.ui.navigation.ShoppingListRoute
import com.the_stilton_assistants.dealdetective.viewmodel.DealsUiState
import com.the_stilton_assistants.dealdetective.viewmodel.DealsViewModel
import com.the_stilton_assistants.dealdetective.viewmodel.SettingsUiState
import kotlinx.coroutines.flow.StateFlow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DealsScreen(
    modifier: Modifier = Modifier,
    navLambda: (ScreenRoute) -> Unit,
    settingsUiState: StateFlow<SettingsUiState>,
    viewModel: DealsViewModel = viewModel(
        factory = DealsViewModel.Factory
    ),
) {
    LaunchedEffect(Unit) {
        viewModel.initialize()
    }
    val lazyStaggeredGridState = rememberLazyStaggeredGridState()
    val (scrollBehavior, scaffoldModifier) = getScrollBehaviorAndModifier(modifier)
    Scaffold(
        modifier = scaffoldModifier,
        bottomBar = {
            NavigationBottomBar(
                modifier = modifier,
                navLambda = navLambda,
                currentDestination = DealsRoute,
            )
        },
        topBar = {
            DealsTopBar(
                modifier = modifier,
                title = "Migliori Sconti",
                scrollBehavior = scrollBehavior,
            )
        },
        floatingActionButton = {
            Row(
                modifier = modifier,
            ) {
                FloatingActionButton(
                    modifier = modifier,
                    onClick = { navLambda(ShoppingListRoute) },
                ) {
                    Icon(
                        modifier = modifier,
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = "Shopping List",
                    )
                }
                FloatingActionComponent(
                    modifier = modifier,
                    lazyStaggeredGridState = lazyStaggeredGridState,
                )
            }
        }
    ) { innerPadding ->
        val dealsUiState by viewModel.dealsUiState.collectAsStateWithLifecycle()
        if (dealsUiState is DealsUiState.Loading) {
            LoadingComponent(
                modifier = modifier.padding(innerPadding).fillMaxSize(),
            )
            return@Scaffold
        }
        if (dealsUiState is DealsUiState.Error) {
            ErrorText(
                modifier = modifier.padding(innerPadding).fillMaxSize(),
                text = (dealsUiState as DealsUiState.Error).message,
            )
            return@Scaffold
        }

        val state = dealsUiState as DealsUiState.Display
        val productsList = state.productList
        if (productsList.isEmpty()) {
            Text(
                modifier = modifier
                    .padding(innerPadding)
                    .padding(vertical = 16.dp)
                    .fillMaxSize(),
                text = "Nessun prodotto trovato",
                textAlign = TextAlign.Center,
            )
            return@Scaffold
        }
        LazyVerticalStaggeredGrid(
            state = lazyStaggeredGridState,
            modifier = modifier.padding(innerPadding),
            columns = StaggeredGridCells.Adaptive(150.dp),
        ) {
            item("Filters", span = StaggeredGridItemSpan.FullLine) {
                FiltersRow(
                    modifier = modifier,
                    viewModel = viewModel,
                )
            }

            if (state.filteredProductList.isEmpty()) {
                item("No products found", span = StaggeredGridItemSpan.FullLine) {
                    Text(
                        modifier = modifier
                            .padding(vertical = 16.dp)
                            .fillMaxSize(),
                        text = "Nessun prodotto trovato, prova a modificare i filtri",
                        textAlign = TextAlign.Center,
                    )
                }
                return@LazyVerticalStaggeredGrid
            }

            items(state.filteredProductList) { product ->
                ProductCard(
                    modifier = modifier,
                    product = product,
                    navLambda = navLambda,
                    settingsUiState = settingsUiState,
                )
            }
        }
    }
}

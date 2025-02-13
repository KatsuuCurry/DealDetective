package com.the_stilton_assistants.dealdetective.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.koalas.trackmybudget.ui.utils.getScrollBehaviorAndModifier
import com.the_stilton_assistants.dealdetective.ui.common.ErrorText
import com.the_stilton_assistants.dealdetective.ui.common.FiltersRow
import com.the_stilton_assistants.dealdetective.ui.common.FloatingActionComponent
import com.the_stilton_assistants.dealdetective.ui.common.LoadingComponent
import com.the_stilton_assistants.dealdetective.ui.common.ProductCard
import com.the_stilton_assistants.dealdetective.ui.common.TopBar
import com.the_stilton_assistants.dealdetective.ui.navigation.ScreenRoute
import com.the_stilton_assistants.dealdetective.viewmodel.SettingsUiState
import com.the_stilton_assistants.dealdetective.viewmodel.ShoppingListUiState
import com.the_stilton_assistants.dealdetective.viewmodel.ShoppingListViewModel
import kotlinx.coroutines.flow.StateFlow
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoppingListScreen(
    modifier: Modifier = Modifier,
    navLambda: (ScreenRoute) -> Unit,
    navBackLambda: () -> Boolean,
    settingsUiState: StateFlow<SettingsUiState>,
    viewModel: ShoppingListViewModel = viewModel(
        factory = ShoppingListViewModel.Factory
    ),
) {
    LaunchedEffect(Unit) {
        viewModel.initialize()
    }
    val (scrollBehavior, scaffoldModifier) = getScrollBehaviorAndModifier(modifier)
    val lazyStaggeredGridState = rememberLazyStaggeredGridState()
    val shoppingListUiState by viewModel.shoppingListUiState.collectAsStateWithLifecycle()
    Scaffold(
        modifier = scaffoldModifier,
        topBar = {
            TopBar(
                modifier = modifier,
                title = "Il Tuo Carrello",
                navBackLambda = navBackLambda,
                scrollBehavior = scrollBehavior,
            )
        },
        floatingActionButton = {
            FloatingActionComponent(
                modifier = modifier,
                lazyStaggeredGridState = lazyStaggeredGridState,
            )
        },
        bottomBar = {
            BottomAppBar(
                modifier = modifier,
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            ) {
                if (shoppingListUiState !is ShoppingListUiState.Display) {
                    Text(
                        text = "Caricamento...",
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxSize(),
                        style = MaterialTheme.typography.displaySmall,
                    )
                    return@BottomAppBar
                }
                if (shoppingListUiState is ShoppingListUiState.Display) {
                    val state = shoppingListUiState as ShoppingListUiState.Display
                    Text(
                        text = buildAnnotatedString {
                            withStyle(MaterialTheme.typography.titleLarge.toSpanStyle()) {
                                append("Totale: ")
                            }
                            withStyle(MaterialTheme.typography.displaySmall.toSpanStyle()) {
                                append(String.format(Locale.getDefault(), "%.2f", state.totalAmount))
                            }
                        },
                        textAlign = TextAlign.Right,
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxSize(),
                    )
                }
            }
        }
    ) { innerPadding ->
        if (shoppingListUiState is ShoppingListUiState.Loading) {
            LoadingComponent(
                modifier = modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
            )
            return@Scaffold
        }
        if (shoppingListUiState is ShoppingListUiState.Error) {
            ErrorText(
                modifier = modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                text = (shoppingListUiState as ShoppingListUiState.Error).message,
            )
            return@Scaffold
        }

        val state = shoppingListUiState as ShoppingListUiState.Display
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

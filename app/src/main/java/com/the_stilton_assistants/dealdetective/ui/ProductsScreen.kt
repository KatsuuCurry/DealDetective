package com.the_stilton_assistants.dealdetective.ui

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.koalas.trackmybudget.ui.utils.getScrollBehaviorAndModifier
import com.the_stilton_assistants.dealdetective.R
import com.the_stilton_assistants.dealdetective.model.StoreId
import com.the_stilton_assistants.dealdetective.ui.common.DealsTopBar
import com.the_stilton_assistants.dealdetective.ui.common.FiltersRow
import com.the_stilton_assistants.dealdetective.ui.common.FloatingActionComponent
import com.the_stilton_assistants.dealdetective.ui.common.LoadingComponent
import com.the_stilton_assistants.dealdetective.ui.common.NavigationBottomBar
import com.the_stilton_assistants.dealdetective.ui.common.ProductCard
import com.the_stilton_assistants.dealdetective.ui.navigation.ProductsRoute
import com.the_stilton_assistants.dealdetective.ui.navigation.ScreenRoute
import com.the_stilton_assistants.dealdetective.ui.navigation.ShoppingListRoute
import com.the_stilton_assistants.dealdetective.viewmodel.ProductsUiState
import com.the_stilton_assistants.dealdetective.viewmodel.ProductsViewModel
import com.the_stilton_assistants.dealdetective.viewmodel.SettingsUiState
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductsScreen(
    modifier: Modifier = Modifier,
    navLambda: (ScreenRoute) -> Unit,
    settingsUiState: StateFlow<SettingsUiState>,
    viewModel: ProductsViewModel = viewModel(
        factory = ProductsViewModel.Factory
    ),
) {
    LaunchedEffect(Unit) {
        viewModel.initialize()
    }

    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }

    val lazyStaggeredGridState = rememberLazyStaggeredGridState()
    val (scrollBehavior, scaffoldModifier) = getScrollBehaviorAndModifier(modifier)
    Scaffold(
        modifier = scaffoldModifier,
        bottomBar = {
            NavigationBottomBar(
                modifier = modifier,
                navLambda = navLambda,
                currentDestination = ProductsRoute,
            )
        },
        topBar = {
            DealsTopBar(
                modifier = modifier,
                title = "Prodotti",
                actions = { modifier ->
                    IconButton(
                        onClick = {
                            scope.launch {
                                showBottomSheet = true
                            }
                        },
                    ) {
                        Icon(
                            modifier = modifier.size(32.dp),
                            painter = painterResource(id = R.drawable.funnel_icon),
                            contentDescription = "Filters",
                        )
                    }
                },
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
        },
    ) { innerPadding ->
        if (showBottomSheet) {
            ProductsScreenModalSheet(
                modifier = modifier,
                sheetState = sheetState,
                viewModel = viewModel,
                closeSheetLambda = {
                    scope.launch {
                        showBottomSheet = false
                    }
                },
            )
        }

        val productsUiState by viewModel.productsUiState.collectAsStateWithLifecycle()
        if (productsUiState is ProductsUiState.Loading) {
            LoadingComponent(
                modifier = modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
            )
            return@Scaffold
        }
        if (productsUiState is ProductsUiState.Error) {
            Text(
                modifier = modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                text = (productsUiState as ProductsUiState.Error).message,
            )
            return@Scaffold
        }

        val state = productsUiState as ProductsUiState.Display
        val productsList = state.productList
        val filteredProductList = state.filteredProductList
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
            modifier = modifier
                .padding(innerPadding)
                .fillMaxSize(),
            columns = StaggeredGridCells.Adaptive(150.dp),
        ) {
            item("Filters", span = StaggeredGridItemSpan.FullLine) {
                FiltersRow(
                    modifier = modifier,
                    viewModel = viewModel,
                )
            }
            if (filteredProductList.isEmpty()) {
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
            items(filteredProductList) { product ->
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductsScreenModalSheet(
    modifier: Modifier = Modifier,
    sheetState: SheetState,
    viewModel: ProductsViewModel,
    closeSheetLambda: () -> Unit,
) {
    ModalBottomSheet(
        modifier = modifier,
        sheetState = sheetState,
        content = {
            var selectedIndex by rememberSaveable { mutableIntStateOf(0) }
            val options = listOf(
                Pair("Tutti", StoreId.UNKNOWN),
                Pair("Carrefour", StoreId.CARREFOUR),
                Pair("Esselunga", StoreId.ESSELUNGA),
                Pair("Tigros", StoreId.TIGROS),
            )

            val storeSelectedState by viewModel.storeSelectedState.collectAsStateWithLifecycle()

            Text(
                modifier = modifier.fillMaxWidth().padding(16.dp),
                text = "Seleziona il negozio",
                textAlign = TextAlign.Center,
            )
            SingleChoiceSegmentedButtonRow(
                modifier = modifier
                    .padding(16.dp)
                    .horizontalScroll(rememberScrollState())
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally),
            ) {
                options.forEachIndexed { index, (label, id) ->
                    SegmentedButton(
                        modifier = modifier,
                        shape = SegmentedButtonDefaults.itemShape(
                            index = index,
                            count = options.size
                        ),
                        onClick = {
                            selectedIndex = index
                            viewModel.selectStore(id)
                        },
                        selected = storeSelectedState == id,
                        label = { Text(label) }
                    )
                }
            }
            if (storeSelectedState == StoreId.UNKNOWN) {
                return@ModalBottomSheet
            }
            Text(
                modifier = modifier.fillMaxWidth().padding(16.dp),
                text = "Seleziona le categorie",
                textAlign = TextAlign.Center,
            )
            val categories = viewModel.getCategoryList(storeSelectedState)

            val gridState = rememberLazyGridState()
            val categoriesState by viewModel.categoriesState.collectAsStateWithLifecycle()
            LazyVerticalGrid(
                columns = GridCells.Adaptive(150.dp),
                state = gridState,
                contentPadding = PaddingValues(horizontal = 8.dp)
            ) {
                items(categories) { (id, label) ->
                    FilterChip(
                        modifier = modifier.padding(horizontal = 4.dp),
                        label = {
                            Text(
                                modifier = modifier.fillMaxWidth(),
                                text = label,
                                textAlign = TextAlign.Center,
                        ) },
                        onClick = {
                            if (categoriesState.contains(id)) {
                                viewModel.removeCategory(id)
                            } else {
                                viewModel.addCategory(id)
                            }
                        },
                        selected = categoriesState.contains(id),
                    )
                }
            }
        },
        onDismissRequest = {
            closeSheetLambda()
        },
    )
}

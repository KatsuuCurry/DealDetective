package com.the_stilton_assistants.dealdetective.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.koalas.trackmybudget.ui.utils.getColumnModifier
import com.koalas.trackmybudget.ui.utils.getScrollBehaviorAndModifier
import com.the_stilton_assistants.dealdetective.R
import com.the_stilton_assistants.dealdetective.model.StoreId
import com.the_stilton_assistants.dealdetective.ui.common.CarrefourProductInfo
import com.the_stilton_assistants.dealdetective.ui.common.ErrorText
import com.the_stilton_assistants.dealdetective.ui.common.EsselungaProductInfo
import com.the_stilton_assistants.dealdetective.ui.common.LoadingComponent
import com.the_stilton_assistants.dealdetective.ui.common.TigrosProductInfo
import com.the_stilton_assistants.dealdetective.ui.common.TopBar
import com.the_stilton_assistants.dealdetective.ui.utils.handleOperationState
import com.the_stilton_assistants.dealdetective.util.ProductUtils
import com.the_stilton_assistants.dealdetective.viewmodel.ProductUiState
import com.the_stilton_assistants.dealdetective.viewmodel.ProductViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductScreen(
    modifier: Modifier,
    storeId: Int,
    productName: String,
    navBackLambda: () -> Boolean,
    viewModel: ProductViewModel = viewModel(
        factory = ProductViewModel.Factory
    ),
) {
    LaunchedEffect(Unit) {
        viewModel.initialize(storeId, productName)
    }

    val enabled = handleOperationState(
        viewModel = viewModel,
    )

    val (scrollBehavior, scaffoldModifier) = getScrollBehaviorAndModifier(modifier)
    Scaffold(
        modifier = scaffoldModifier,
        topBar = {
            TopBar(
                modifier = modifier,
                title = "Dettagli del Prodotto",
                navBackLambda = navBackLambda,
                scrollBehavior = scrollBehavior,
            )
        },
    ) { innerPadding ->
        val productUiState by viewModel.productUiState.collectAsStateWithLifecycle()
        if (productUiState is ProductUiState.Loading) {
            LoadingComponent(
                modifier = modifier.padding(innerPadding),
            )
            return@Scaffold
        }
        if (productUiState is ProductUiState.Error) {
            ErrorText(
                modifier = modifier.padding(innerPadding),
                text = (productUiState as ProductUiState.Error).message,
            )
            return@Scaffold
        }
        val product = (productUiState as ProductUiState.Display).product

        Column(
            modifier = modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .fillMaxSize(),
        ) {
            Box(
                modifier = modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                var image = ProductUtils.getProductImage(product)
                if (image == null) {
                    image = R.drawable.app_logo
                }
                if (image is String) {
                    AsyncImage(
                        modifier = modifier
                            .padding(top = 16.dp, bottom = 16.dp)
                            .size(320.dp)
                            .fillMaxWidth(),
                        model = image,
                        contentDescription = "Product Image",
                    )
                } else if (image is Int) {
                    Image(
                        painter = painterResource(id = image),
                        contentDescription = "Product Image",
                        modifier = modifier
                            .padding(top = 16.dp, bottom = 16.dp)
                            .size(320.dp)
                            .fillMaxWidth(),
                    )
                }

                val id = when(product.storeId) {
                    StoreId.ESSELUNGA.value -> R.drawable.esselunga_logo
                    StoreId.CARREFOUR.value -> R.drawable.carrefour_logo
                    StoreId.TIGROS.value -> R.drawable.tigros_logo
                    else -> R.drawable.app_logo
                }

                Image(
                    painter = painterResource(id = id),
                    contentDescription = "Store Logo",
                    modifier = modifier
                        .padding(top = 16.dp, start = 16.dp)
                        .size(64.dp)
                        .align(Alignment.TopStart),
                )
            }

            Text(
                modifier = modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                text = product.productName,
                style = MaterialTheme.typography.titleLarge,
            )

            Text(
                modifier = modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                text = "Categoria: ${product.category}",
                style = MaterialTheme.typography.titleMedium,
            )

            val newModifier = modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            when (product.storeId) {
                StoreId.ESSELUNGA.value -> {
                    EsselungaProductInfo(
                        newModifier,
                        product,
                        MaterialTheme.typography.displaySmall,
                    )
                }

                StoreId.CARREFOUR.value -> {
                    CarrefourProductInfo(
                        newModifier,
                        product,
                        MaterialTheme.typography.displaySmall,
                    )
                }

                StoreId.TIGROS.value -> {
                    TigrosProductInfo(
                        newModifier,
                        product,
                        MaterialTheme.typography.displaySmall,
                    )
                }
            }

            Button(
                modifier = modifier
                    .padding(top = 16.dp)
                    .padding(horizontal = 16.dp)
                    .align(Alignment.CenterHorizontally),
                onClick = {
                    viewModel.updateFavoriteStatus(!product.isFavorite)
                },
                enabled = enabled,
            ) {
                val icon = if (product.isFavorite) {
                    Icons.Filled.ShoppingCart
                } else {
                    Icons.Outlined.ShoppingCart
                }
                Icon(
                    modifier = modifier,
                    imageVector = icon,
                    contentDescription = "Add to Cart",
                )
                Spacer(modifier.size(ButtonDefaults.IconSpacing))
                val text = if (product.isFavorite) {
                    "Rimuovi dal Carrello"
                } else {
                    "Aggiungi al Carrello"
                }
                Text(
                    modifier = modifier,
                    text = text,
                )
            }
        }
    }
}

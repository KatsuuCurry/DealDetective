package com.the_stilton_assistants.dealdetective.ui.common

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.the_stilton_assistants.dealdetective.model.ProductsOrder
import com.the_stilton_assistants.dealdetective.viewmodel.AProductsViewModel

@Composable
fun FiltersRow(
    modifier: Modifier,
    viewModel: AProductsViewModel,
) {
    Row(
        modifier = modifier.padding(4.dp).padding(bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val productsOrder by viewModel.productsOrderState.collectAsStateWithLifecycle()
        Button(
            modifier = modifier.padding(4.dp),
            onClick = {
                if (productsOrder == ProductsOrder.ASC)
                    viewModel.setProductsOrder(ProductsOrder.DESC)
                else
                    viewModel.setProductsOrder(ProductsOrder.ASC)
            }
        ) {
            Text(
                modifier = modifier,
                text = productsOrder.value,
            )
        }
        val searchQuery by viewModel.searchQueryState.collectAsStateWithLifecycle()
        TextField(
            modifier = modifier.padding(4.dp).fillMaxWidth(),
            value = searchQuery,
            onValueChange = { viewModel.setSearchQuery(it) },
            label = { Text("Cerca") },
            leadingIcon = {
                Icon(
                    modifier = modifier,
                    imageVector = Icons.Default.Search,
                    contentDescription = "Cerca",
                )
            },
            singleLine = true,
            placeholder = {
                Text("Cerca")
            },
            shape = RoundedCornerShape(16.dp),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )
    }
}

package com.the_stilton_assistants.dealdetective.ui.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.the_stilton_assistants.dealdetective.R
import com.the_stilton_assistants.dealdetective.model.ImagesSize
import com.the_stilton_assistants.dealdetective.model.Product
import com.the_stilton_assistants.dealdetective.model.StoreId
import com.the_stilton_assistants.dealdetective.ui.navigation.ProductRoute
import com.the_stilton_assistants.dealdetective.ui.navigation.ScreenRoute
import com.the_stilton_assistants.dealdetective.util.ProductUtils
import com.the_stilton_assistants.dealdetective.viewmodel.SettingsUiState
import kotlinx.coroutines.flow.StateFlow
import kotlin.math.roundToInt

@Composable
fun ProductCard(
    modifier: Modifier,
    product: Product,
    navLambda: (ScreenRoute) -> Unit,
    settingsUiState: StateFlow<SettingsUiState>,
) {
    Card(
        modifier = modifier.padding(2.dp),
        onClick = { navLambda(ProductRoute(product.storeId, product.productName)) },
    ) {
        val settingsUiState by settingsUiState.collectAsStateWithLifecycle()

        val imageSize = if (settingsUiState is SettingsUiState.Display) {
            (settingsUiState as SettingsUiState.Display).settings.imagesSize
        } else {
            ImagesSize.MEDIUM
        }

        val id = when(product.storeId) {
            StoreId.ESSELUNGA.value -> R.drawable.esselunga_logo
            StoreId.CARREFOUR.value -> R.drawable.carrefour_logo
            StoreId.TIGROS.value -> R.drawable.tigros_logo
            else -> R.drawable.app_logo
        }

        val size = when (imageSize) {
            ImagesSize.SMALL -> 50
            ImagesSize.MEDIUM -> 100
            ImagesSize.LARGE -> 150
            ImagesSize.UNRECOGNIZED -> 100
        }

        val image = ProductUtils.getProductImage(product)
        Box(
            modifier = modifier
                .padding(4.dp)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center,
        ) {
            if (image == null) {
                Image(
                    painter = painterResource(id = R.drawable.app_logo),
                    contentDescription = "Product Image",
                    modifier = modifier
                        .padding(4.dp)
                        .size(size.dp)
                        .fillMaxWidth(),
                )
            } else if (image is String) {
                AsyncImage(
                    modifier = modifier
                        .padding(4.dp)
                        .size(size.dp)
                        .fillMaxWidth(),
                    model = image,
                    contentDescription = "Product Image",
                )
            } else if (image is Int) {
                Image(
                    painter = painterResource(id = image),
                    contentDescription = "Product Image",
                    modifier = modifier
                        .padding(4.dp)
                        .size(size.dp)
                        .fillMaxWidth(),
                )
            }
            Image(
                painter = painterResource(id = id),
                contentDescription = "Store Logo",
                modifier = modifier
                    .size(32.dp)
                    .padding(4.dp)
                    .align(Alignment.TopStart),
            )
        }

        val boldText = if (settingsUiState is SettingsUiState.Display) {
            (settingsUiState as SettingsUiState.Display).settings.boldText
        } else {
            true
        }

        when (product.storeId) {
            StoreId.ESSELUNGA.value -> {
                EsselungaProductInfo(modifier, product)
            }

            StoreId.CARREFOUR.value -> {
                CarrefourProductInfo(modifier, product)
            }

            StoreId.TIGROS.value -> {
                TigrosProductInfo(modifier, product)
            }
        }

        Text(
            modifier = modifier.padding(4.dp),
            text = product.productName,
            fontWeight = if (boldText) FontWeight.Bold else FontWeight.Normal,
        )
    }
}

@Composable
fun TigrosProductInfo(
    modifier: Modifier,
    product: Product,
    style: TextStyle = MaterialTheme.typography.titleMedium,
) {
    val originalPrice = product.originalPrice
    if (originalPrice == null) {
        Text(
            modifier = modifier.padding(4.dp),
            text = "(Prezzo Speciale) ${product.discountedPrice} €",
            fontWeight = FontWeight.Bold,
            style = style,
        )
        return
    }
    val discountedPrice = product.discountedPrice

    if (originalPrice > discountedPrice) {
        val discountPercentage =
            (((originalPrice - discountedPrice) / originalPrice) * 100).roundToInt()
        Text(
            modifier = modifier.padding(4.dp),
            text = buildAnnotatedString {
                withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.error, textDecoration = TextDecoration.LineThrough)) {
                    append("$originalPrice € ")
                }
                append(" $discountedPrice € ")
                withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.secondary)) {
                    append("(-$discountPercentage%)")
                }
            },
            fontWeight = FontWeight.Bold,
            style = style,
        )
    } else {
        val temp = originalPrice * 2
        val discountPercentage =
            (((temp - discountedPrice) / temp) * 100).roundToInt()
        Text(
            modifier = modifier.padding(4.dp),
            text = buildAnnotatedString {
                append("1 Pezzo ")
                withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.error, textDecoration = TextDecoration.LineThrough)) {
                    append("$originalPrice € ")
                }
                append("\n2 Pezzi ")
                append("$discountedPrice € ")
                withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.secondary)) {
                    append("(-$discountPercentage%)")
                }
            },
            fontWeight = FontWeight.Bold,
            style = style,
        )
    }
}

@Composable
fun EsselungaProductInfo(
    modifier: Modifier,
    product: Product,
    style: TextStyle = MaterialTheme.typography.titleMedium,
) {
    val originalPrice = product.originalPrice!!
    val discountedPrice = product.discountedPrice

    if (originalPrice == discountedPrice) {
        Text(
            modifier = modifier.padding(4.dp),
            text = "(Prezzo Speciale) $originalPrice €",
            fontWeight = FontWeight.Bold,
            style = style,
        )
        return
    }

    if (originalPrice > discountedPrice) {
        var discountPercentage = product.json!!.getString("discountPercentage")
        if (discountPercentage.isBlank())
            discountPercentage = (((originalPrice - discountedPrice) / originalPrice) * 100).roundToInt().toString()
        Text(
            modifier = modifier.padding(4.dp),
            text = buildAnnotatedString {
                withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.error, textDecoration = TextDecoration.LineThrough)) {
                    append("$originalPrice € ")
                }
                append(" $discountedPrice € ")
                withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.secondary)) {
                    append("(-$discountPercentage%)")
                }
            },
            fontWeight = FontWeight.Bold,
            style = style,
        )
    } else {
        val temp = originalPrice * 2
        val discountPercentage =
            (((temp - discountedPrice) / temp) * 100).roundToInt()
        Text(
            modifier = modifier.padding(4.dp),
            text = buildAnnotatedString {
                append("1 Pezzo ")
                withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.error, textDecoration = TextDecoration.LineThrough)) {
                    append("$originalPrice € ")
                }
                append("\n2 Pezzi ")
                append("$discountedPrice € ")
                withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.secondary)) {
                    append("(-$discountPercentage%)")
                }
            },
            fontWeight = FontWeight.Bold,
            style = style,
        )
    }
}

@Composable
fun CarrefourProductInfo(
    modifier: Modifier,
    product: Product,
    style: TextStyle = MaterialTheme.typography.titleMedium,
) {
    val originalPrice = product.originalPrice
    if (originalPrice == null) {
        Text(
            modifier = modifier.padding(4.dp),
            text = "(Prezzo Speciale) ${product.discountedPrice} €",
            fontWeight = FontWeight.Bold,
            style = style,
        )
        return
    }
    val discountedPrice = product.discountedPrice

    if (originalPrice > discountedPrice) {
        val discountPercentage =
            (((originalPrice - discountedPrice) / originalPrice) * 100).roundToInt()
        Text(
            modifier = modifier.padding(4.dp),
            text = buildAnnotatedString {
                withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.error, textDecoration = TextDecoration.LineThrough)) {
                    append("$originalPrice € ")
                }
                append(" $discountedPrice € ")
                withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.secondary)) {
                    append("(-$discountPercentage%)")
                }
            },
            fontWeight = FontWeight.Bold,
            style = style,
        )
    } else {
        val temp = originalPrice * 2
        val discountPercentage =
            (((temp - discountedPrice) / temp) * 100).roundToInt()
        Text(
            modifier = modifier.padding(4.dp),
            text = buildAnnotatedString {
                append("1 Pezzo ")
                withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.error, textDecoration = TextDecoration.LineThrough)) {
                    append("$originalPrice € ")
                }
                append("\n2 Pezzi ")
                append("$discountedPrice € ")
                withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.secondary)) {
                    append("(-$discountPercentage%)")
                }
            },
            fontWeight = FontWeight.Bold,
            style = style,
        )
    }
}

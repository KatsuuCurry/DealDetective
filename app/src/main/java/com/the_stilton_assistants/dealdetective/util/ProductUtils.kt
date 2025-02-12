package com.the_stilton_assistants.dealdetective.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import com.the_stilton_assistants.dealdetective.model.Product
import com.the_stilton_assistants.dealdetective.model.StoreId
import com.the_stilton_assistants.dealdetective.service.stores.CarrefourCategory
import java.util.regex.Pattern

object ProductUtils {

    @Composable
    fun getProductImage(product: Product): Any? {
        if (product.json != null && product.json!!.has("imageUrl")) {
            return product.json!!.getString("imageUrl")
        } else if (product.storeId == StoreId.CARREFOUR.value) {
            return CarrefourCategory.getIcon(product.category)
        }
        return null
    }
}

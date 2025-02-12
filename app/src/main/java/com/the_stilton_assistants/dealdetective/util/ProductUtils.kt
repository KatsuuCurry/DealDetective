package com.the_stilton_assistants.dealdetective.util

import androidx.compose.runtime.Composable
import com.the_stilton_assistants.dealdetective.model.Product
import com.the_stilton_assistants.dealdetective.model.StoreId
import com.the_stilton_assistants.dealdetective.service.stores.CarrefourCategory

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

package com.example.dealdetective.repository

import com.example.application.EsselungaStore
import com.example.dealdetective.AppContainer
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import org.json.JSONObject

object TigrosHandler: IStoreHandler {
    private val categories = intArrayOf(148031509, 148031507, 148031512, 148031510, 148031517, 148031517, 148031520, 148031516, 148031518, 148031514, 148031515, 148031508, 148031513)

    override suspend fun getStoreDiscount(
        appContainer: AppContainer,
        storeCode: String,
        store: EsselungaStore
    ): Boolean {
        val client = HttpClient()
        val response = client.get(store.url) {

        }

        if (response.status.value != 200) {
            return false
        }


        var promoCode = -1

        for (i in 0..500) {
            val discountPage = client.get("https://www.tigros.it/ebsn/api/leaflet/product-search?parent_leaflet_id=$i&parent_category_id=148031513") {

            }

            val jsonResponse = JSONObject(discountPage.body<String>())
            val totItems = jsonResponse.getJSONObject("data").getJSONObject("page").getInt("totItems")
            promoCode = i

            if (totItems != 0) {
                break
            }
        }

        if (promoCode == -1) {
            // Non trovo volantino
            return false
        }

        categories.forEach { category ->
            val discountPage =
                client.get("https://www.tigros.it/ebsn/api/leaflet/product-search?parent_leaflet_id=$promoCode&parent_category_id=$category") {

                }

            val jsonResponse = JSONObject(discountPage.body<String>())
            val productArray = jsonResponse.getJSONObject("data").getJSONArray("products")

            for(i in 0..<productArray.length()) {
                val product = productArray.getJSONObject(i)
                val productId = product.getInt("id")
                val productName = product.getString("name")
                val originalPrice = product.getDouble("price")
                val discountedPrice = product.getDouble("priceDisplay")
                val brand = product.getString("shortDescr")
                val quantity = product.getString("description")
                // val discountPercentage

                val jsonDesc = JSONObject()
                jsonDesc.put("brand", brand)
                jsonDesc.put("quantity", quantity)
                // jsonDesc.put("discountPercentage", discountPercentage)

                val newProduct = com.example.dealdetective.storage.room.Product(productName, 3, originalPrice, discountedPrice, category.toString(), jsonDesc)
                appContainer.productsRepository.insertAllProducts(newProduct)
            }
        }

        return true
    }
}
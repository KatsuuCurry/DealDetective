package com.example.dealdetective.repository

import com.example.application.EsselungaStore
import com.example.dealdetective.AppContainer
import com.example.dealdetective.storage.room.Product
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import org.json.JSONObject

object CarrefourHandler: IStoreHandler {
    val categories = arrayOf("Acqua, succhi e bibite", "Animali", "Arredo Casa", "Articoli per bambini", "Articoli per la casa", "Biciclette", "Birra e liquori", "Carne", "Climatizzazione", "Condimenti e conserve", "Cura casa", "Cura persona", "Dolci e prima colazione", "Elettrodomestici cucina", "Elettrodomestici pulizia e stiro", "Fai da te", "Frutta e verdura", "Gastronomia", "Gelati e surgelati", "Giocattoli", "Giochi da esterno", "Pane e snack salati", "Pasta, riso e farina", "Pesce", "Prima Infanzia", "Salumi e formaggi", "Telefonia", "Uova, latte e latticini", "Vino")

    override suspend fun getStoreDiscount(appContainer: AppContainer, storeCode: String, store: EsselungaStore) : Boolean {
        val client = HttpClient()
        val response = client.get(store.url) {

        }

        categories.forEach { category ->
            val discountPage = client.get("https://www.carrefour.it/on/demandware.store/Sites-carrefour-IT-Site/it_IT/Search-ShowAjax?cgid=promozioni&prefn1=C4_PrimaryCategory&prefv1=$category&storeId=0415")  {

            }
            val jsonResponse = JSONObject(response.body<String>())
            val productArray = jsonResponse.getJSONArray("productIds")

            for(i in 0..productArray.length()) {
                val product = productArray.getJSONObject(i)
                val productName = product.getString("productName")
                val brand = product.getString("brand")
                val originalPrice = product.getJSONObject("price").getJSONObject("list").getDouble("value")
                val discountedPrice = product.getJSONObject("price").getJSONObject("sales").getDouble("value")
                val originalUnitPrice = product.getJSONObject("unitPrice").getJSONObject("list").getDouble("value")
                val discountedUnitPrice = product.getJSONObject("unitPrice").getJSONObject("sales").getDouble("value")
                val promotionName = product.getJSONObject("promotionInfo").getJSONObject("name")
                val promotionEndDate = product.getJSONObject("promotionInfo").getJSONObject("endDate")
                val discountPercentage = product.getDouble("discountPercentage")

                val jsonDesc = JSONObject()
                jsonDesc.put("brand", brand)
                jsonDesc.put("originalUnitPrice", originalUnitPrice)
                jsonDesc.put("discountedUnitPrice", discountedUnitPrice)
                jsonDesc.put("promotionName", promotionName)
                jsonDesc.put("promotionEndDate", promotionEndDate)
                jsonDesc.put("discountPercentage", discountPercentage)

                val newProduct = Product(productName, 1, originalPrice, discountedPrice, category, jsonDesc)
                appContainer.productsRepository.insertAllProducts(newProduct)
            }
        }

        return true
    }
}
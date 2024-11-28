package com.example.dealdetective.repository

import android.util.Log
import com.example.application.EsselungaStore
import com.example.dealdetective.AppContainer
import com.fleeksoft.ksoup.Ksoup
import com.fleeksoft.ksoup.nodes.Document
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.headers
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject

object EsselungaHandler: IStoreHandler {
    private val categories = intArrayOf(1769, 1770, 1771, 1774, 1781, 1773, 1772, 1778, 1777, 1775, 1776, 1779, 1780, 1788, 1782, 1790, 1785, 1786, 2261, 1784, 1783, 1787)

    private fun createExceptionHandler(callback: ((DataStoreOperation) -> Unit)?): CoroutineExceptionHandler {
        return CoroutineExceptionHandler { coroutineContext, throwable ->
            Log.e(EsselungaHandler::class.simpleName, coroutineContext.toString())
            Log.e(EsselungaHandler::class.simpleName, throwable.toString())
            Log.e(EsselungaHandler::class.simpleName, throwable.stackTraceToString())
            if (callback != null) {
                callback(DataStoreOperation.ERROR)
            }
        }
    }

    private val dispatcher: CoroutineDispatcher = Dispatchers.IO

    fun saveNewStore(scope: CoroutineScope, appContainer: AppContainer, url: String,
                     callback: (DataStoreOperation) -> Unit) {
        scope.launch(dispatcher + createExceptionHandler(callback)) {
            Log.d(EsselungaHandler::class.simpleName, "Saving new store")
            val storeCode = url.removeSuffix(".html").substringAfterLast(".").uppercase()
            var store: EsselungaStore? = null

            // The updateData function in Jetpack DataStore is a suspending function that takes a lambda as its parameter. This lambda receives the current state of the data as its argument and returns the new state of the data. This approach ensures that the data update is atomic and thread-safe.
            appContainer.esselungaStoresDataStore.updateData { stores ->
                if (stores.storesMap.containsKey(storeCode)) {
                    return@updateData stores
                }
                store = EsselungaStore.newBuilder().setCodPromo(0).setUrl(url).build()
                stores.toBuilder().putStores(storeCode, store).build()
            }

            if (store == null) {
                callback(DataStoreOperation.ERROR)
                return@launch
            }

            if (getStoreDiscount(appContainer, storeCode, store))
                callback(DataStoreOperation.SUCCESS)
            else
                callback(DataStoreOperation.FAILURE)
        }
    }

    override suspend fun getStoreDiscount(appContainer: AppContainer, storeCode: String, store: EsselungaStore) : Boolean {
        val client = HttpClient()
        val response = client.get(store.url) {

        }

        if (response.status.value != 200) {
            return false
        }

        val doc: Document = Ksoup.parse(html = response.body<String>())

        val codPromo = doc.select("div[data-cod-promo]").attr("data-cod-promo")

        if (codPromo.toInt() == store.codPromo) {
            return true
        }

        appContainer.esselungaStoresDataStore.updateData { stores ->
            val newStore = store.toBuilder().setCodPromo(codPromo.toInt()).build()
            stores.toBuilder().removeStores(storeCode).putStores(storeCode, newStore).build()
        }

        categories.forEach { category ->
            val discountPage = client.get("https://www.esselunga.it/services/istituzionale35/digital-grid.condition:nav_menu.abbrev:$storeCode.page:0.rows:1000.category:$category.codPromo:$codPromo.json") {
                headers {
                    append("Accept", "application/json")
                }
            }

            if (discountPage.status.value != 200) {
                return@forEach
            }

            println(discountPage.body<String>())
            val jsonResponse = JSONObject(discountPage.body<String>())
            val productArray = jsonResponse.getJSONArray("items")

            for(i in 0..<productArray.length()) {
                val product = productArray.getJSONObject(i)
                val productName = product.getString("title")
                val originalPrice = product.getDouble("prezzo")
                val discountedPrice = product.getJSONArray("promozioni_prezzoPromo").getDouble(0)
                val promotionName = product.getString("promozioni_desMeccanica")
                val discountPercentage = product.getJSONArray("promozioni_scontoPercentuale").getString(0)

                val jsonDesc = JSONObject()
                jsonDesc.put("promotionName", promotionName)
                jsonDesc.put("discountPercentage", discountPercentage)

                val newProduct = com.example.dealdetective.storage.room.Product(productName, 2, originalPrice, discountedPrice, category.toString(), jsonDesc)
                val oldProduct = appContainer.productsRepository.getProductById(newProduct.productName, newProduct.storeId)
                if (oldProduct != null) {
                    continue
                }
                appContainer.productsRepository.insertAllProducts(newProduct)
            }
        }

        return true
    }
}
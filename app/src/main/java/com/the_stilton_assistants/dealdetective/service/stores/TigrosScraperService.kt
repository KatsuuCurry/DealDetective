package com.the_stilton_assistants.dealdetective.service.stores

import android.util.Log
import com.the_stilton_assistants.dealdetective.model.Category
import com.the_stilton_assistants.dealdetective.model.Product
import com.the_stilton_assistants.dealdetective.model.Store
import com.the_stilton_assistants.dealdetective.model.StoreId
import com.the_stilton_assistants.dealdetective.model.StoreType
import com.the_stilton_assistants.dealdetective.repository.products.IProductsRepository
import com.the_stilton_assistants.dealdetective.repository.settings.IStoresSettingsRepository
import com.the_stilton_assistants.dealdetective.util.Result
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

private const val TAG = "TigrosHandler"

class TigrosScraperService(
    private val storesSettingsRepository: IStoresSettingsRepository,
    private val productsRepository: IProductsRepository,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
): IStoreScraperService {
    override val storeId: StoreId = StoreId.TIGROS
    override val storeType: StoreType = StoreType.Toggle

    override suspend fun getStoreDiscount(
        store: Store,
    ): Result<Int?> {
        try {
            HttpClient().use { client ->
                var promoCode = -1

                if (store.codPromo != -1) {
                    withContext(ioDispatcher) {
                        val discountPage = client.get("https://www.tigros.it/ebsn/api/leaflet/product-search?parent_leaflet_id=${store.codPromo}&parent_category_id=148031513") {

                        }

                        if (discountPage.status.value != 200) {
                            return@withContext
                        }

                        val jsonResponse = JSONObject(discountPage.body<String>())
                        val totItems = jsonResponse.getJSONObject("data").getJSONObject("page").getInt("totItems")

                        if (totItems != 0) {
                            promoCode = store.codPromo
                        }
                    }
                }

                if (promoCode == -1) {
                    withContext(ioDispatcher) {
                        for (i in 0..500) {
                            val discountPage = client.get("https://www.tigros.it/ebsn/api/leaflet/product-search?parent_leaflet_id=$i&parent_category_id=148031513") {

                            }

                            if (discountPage.status.value != 200) {
                                continue
                            }

                            val jsonResponse = JSONObject(discountPage.body<String>())
                            val totItems = jsonResponse.getJSONObject("data").getJSONObject("page").getInt("totItems")

                            if (totItems != 0) {
                                promoCode = i
                                break
                            }
                        }
                    }
                }

                if (promoCode == -1) {
                    Log.e(TAG, "Flyer not found")
                    return Result.ServiceLayerError(TigrosErrors.FlyerNotFound)
                }

                if (promoCode == store.codPromo) {
                    Log.d(TAG, "No new flyer found")
                    return Result.Success(null)
                }

                val result = productsRepository.deleteProductsByStoreType(storeId.value)
                if (result is Result.Error) {
                    Log.e(TAG, "Error while deleting products: " + result.error.message)
                    return result
                }
                TigrosCategory.idList.forEach { category ->
                    val discountPage =
                        withContext(ioDispatcher) {
                            client.get("https://www.tigros.it/ebsn/api/leaflet/product-search?parent_leaflet_id=$promoCode&parent_category_id=$category") {

                            }
                        }

                    val jsonResponse = JSONObject(discountPage.body<String>())
                    val productArray = jsonResponse.getJSONObject("data").getJSONArray("products")
                    val products = mutableListOf<Product>()

                    for(i in 0..<productArray.length()) {
                        val product = productArray.getJSONObject(i)
                        val productId = product.getInt("id")
                        val productName = product.getString("name").trim()
                        var originalPrice: Double? = product.getDouble("price")
                        val discountedPrice = product.getDouble("priceDisplay")
                        if (originalPrice == discountedPrice) {
                            originalPrice = null
                        }
                        val brand = product.getString("shortDescr")
                        val quantity = product.getString("description")

                        val jsonDesc = JSONObject()
                        jsonDesc.put("brand", brand)
                        jsonDesc.put("quantity", quantity)
                        jsonDesc.put("imageUrl", product.getString("mediaURL"))

                        val newProduct = Product(
                            productName,
                            storeId.value,
                            originalPrice,
                            discountedPrice,
                            category,
                            false,
                            jsonDesc
                        )
                        products.add(newProduct)
                    }
                    productsRepository.insertAllProducts(*products.toTypedArray())
                }

                return Result.Success(promoCode)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error while fetching data from Tigros", e)
            return Result.ServiceLayerError(ServiceError.UnknownError("Error while fetching data from Tigros"))
        }
    }

    sealed interface TigrosErrors: ServiceError {
        object FlyerNotFound : TigrosErrors {
            override val message: String = "Nessuna offerta trovata"
        }
    }
}

enum class TigrosCategory(override val id: String, override val displayName: String) : Category {
    FRUTTA_VERDURA("148031509", "Frutta e Verdura"),
    CARNE("148031507", "Carne"),
    PESCE_SUSHI("148031511", "Pesce e Sushi"),
    BANCO_GASTRONOMIA("148031512", "Banco Gastronomia"),
    PANE_PASTICCERIA("148031510", "Pane e Pasticceria"),
    FRESCHI_CONFEZIONATI("148031517", "Freschi Confezionati"),
    SURGELATI_GELATI("148031520", "Surgelati e Gelati"),
    DISPENSA("148031516", "Dispensa"),
    INFANZIA("148031518", "Infanzia"),
    BEVANDE("148031514", "Bevande"),
    CURA_CASA("148031515", "Cura Casa"),
    CURA_PERSONA("148031508", "Cura Persona"),
    ANIMALI("148031513", "Animali"),
    SPECIALITA_ETNICHE("1263", "Specialità Etniche"),;

    companion object {
        val idList = entries.map { it.id }
        val categories = entries.map { Pair(it.id, it.displayName) }
    }
}

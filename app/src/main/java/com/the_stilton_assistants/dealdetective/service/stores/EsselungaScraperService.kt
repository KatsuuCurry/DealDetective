package com.the_stilton_assistants.dealdetective.service.stores

import android.util.Log
import com.fleeksoft.ksoup.Ksoup
import com.fleeksoft.ksoup.nodes.Document
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
import io.ktor.http.headers
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

private const val TAG = "EsselungaHandler"

class EsselungaScraperService(
    private val storesSettingsRepository: IStoresSettingsRepository,
    private val productsRepository: IProductsRepository,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
): IStoreScraperService, ISelectableStoreScraperService {
    override val storeId: StoreId = StoreId.ESSELUNGA
    override val storeType: StoreType = StoreType.Selectable

    override suspend fun saveNewStore(
        url: String,
        appScope: CoroutineScope,
    ) : Result<Unit> {
        try {
            Log.d(TAG, "Saving new store")
            var store = Store.newBuilder()
                .setCodPromo(-1)
                .setUrl(url)
                .build()

            val result = storesSettingsRepository.insertStore(storeId.value, store)

            return result
        } catch (e: Exception) {
            Log.e(TAG, "Error saving new store", e)
            return Result.ServiceLayerError(ServiceError.UnknownError("Error saving new store"))
        }
    }

    override suspend fun getStoreDiscount(
        store: Store,
    ) : Result<Int?> {
        try {
            val storeCode = store.url.removeSuffix(".html").substringAfterLast(".").uppercase()
            HttpClient().use { client ->
                val response = withContext(ioDispatcher) {
                    client.get(store.url) {
                    }
                }

                if (response.status.value != 200) {
                    Log.e(TAG, "Incorrect response: " + response.status.value)
                    return Result.ServiceLayerError(IStoreScraperService.BaseErrors.IncorrectGetResponse)
                }

                val doc: Document = Ksoup.parse(html = response.body<String>())

                val codPromo = doc.select("div[data-cod-promo]").attr("data-cod-promo")

                if (codPromo.toInt() == store.codPromo) {
                    Log.d(TAG, "No new flyer found")
                    return Result.Success(null)
                }

                val result = productsRepository.deleteProductsByStoreType(storeId.value)
                if (result is Result.Error) {
                    Log.e(TAG, "Error while deleting products: " + result.error.message)
                    return result
                }

                EsselungaCategory.idList.forEach { category ->
                    val discountPage = withContext(ioDispatcher) {
                        client.get("https://www.esselunga.it/services/istituzionale35/digital-grid.condition:nav_menu.abbrev:$storeCode.page:0.rows:1000.category:$category.codPromo:$codPromo.json") {
                            headers {
                                append("Accept", "application/json")
                            }
                        }
                    }

                    if (discountPage.status.value != 200) {
                        return@forEach
                    }

                    val jsonResponse = JSONObject(discountPage.body<String>())
                    val productArray = jsonResponse.getJSONArray("items")

                    val products: MutableList<Product> = mutableListOf()
                    for (i in 0..<productArray.length()) {
                        val product = productArray.getJSONObject(i)
                        val productName = product.getString("title").trim()
                        val originalPrice = product.getDouble("prezzo")
                        val discountedPrice =
                            product.getJSONArray("promozioni_prezzoPromo").getDouble(0)
                        val promotionName = product.getString("promozioni_desMeccanica")
                        val discountPercentage =
                            product.getJSONArray("promozioni_scontoPercentuale").getString(0)

                        val jsonDesc = JSONObject()
                        jsonDesc.put("promotionName", promotionName)
                        jsonDesc.put("discountPercentage", discountPercentage)
                        jsonDesc.put("imageUrl", product.getString("imgUrl"))

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

                return Result.Success(codPromo.toInt())
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error while fetching data from Esselunga", e)
            return Result.ServiceLayerError(ServiceError.UnknownError("Error while fetching data from Esselunga"))
        }
    }
}

enum class EsselungaCategory(override val id: String, override val displayName: String) : Category {
    FRUTTA_VERDURA("1769", "Frutta e Verdura"),
    PESCE_SUSHI("1770", "Pesce e Sushi"),
    CARNE("1771", "Carne"),
    LATTICINI_SALUMI_FORMAGGI("1774", "Latticini, Salumi e Formaggi"),
    ALIMENTI_VEGETALI("1781", "Alimenti Vegetali"),
    PANE_PASTICCERIA("1773", "Pane e Pasticceria"),
    GASTRONOMIA_PIATTI_PRONTI("1772", "Gastronomia e Piatti Pronti"),
    PATATINE_CIOCCOLATO_CARAMELLE("1778", "Patatine, Cioccolato e Caramelle"),
    COLAZIONE("1777", "Colazione"),
    CONFEZIONATI_ALIMENTARI("1775", "Confezionati Alimentari"),
    SURGELATI_GELATI("1776", "Surgelati e Gelati"),
    ACQUA_BIRRA_BIBITE("1779", "Acqua, Birra e Bibite"),
    VINI_LIQUORI("1780", "Vini e Liquori"),
    IGENE_CURA_PERSONA_INTIMO("1788", "Igene, Cura Persona e Intimo"),
    INTEGRATORI_SANITARI("1782", "Integratori e Sanitari"),
    CURA_CASA("1790", "Cura Casa"),
    //CATEGORY17("1785", "Category17"),
    MULTIMEDIA_CARTE_RICARICHE("1786", "Multimedia, Carte e Ricariche"),
    //CATEGORY19("2261", "Category19"),
    AMICI_ANIMALI("1784", "Amici Animali"),
    MONDO_BIMBI("1783", "Mondo Bimbi");
    //CATEGORY22("1787", "Category22");

    companion object {
        val idList = entries.map { it.id }
        val categories = entries.map { Pair(it.id, it.displayName) }
    }
}

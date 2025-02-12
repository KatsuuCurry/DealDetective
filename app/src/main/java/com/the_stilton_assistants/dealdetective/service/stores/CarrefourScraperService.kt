package com.the_stilton_assistants.dealdetective.service.stores

import android.util.Log
import com.the_stilton_assistants.dealdetective.R
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
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

private const val TAG = "CarrefourHandler"

class CarrefourScraperService(
    private val storesSettingsRepository: IStoresSettingsRepository,
    private val productsRepository: IProductsRepository,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
): IStoreScraperService {
    override val storeId: StoreId = StoreId.CARREFOUR
    override val storeType: StoreType = StoreType.Toggle

    override suspend fun getStoreDiscount(
        store: Store,
    ) : Result<Int?> {
        try {
            HttpClient().use { client ->
                val result = productsRepository.deleteProductsByStoreType(storeId.value)
                if (result is Result.Error) {
                    Log.e(TAG, "Error while deleting products: " + result.error.message)
                    return result
                }

                CarrefourCategory.idList.forEach { category ->
                    val categoryUrl = URLEncoder.encode(category, StandardCharsets.UTF_8.toString())
                    val discountPage = withContext(ioDispatcher) {
                        client.get("https://www.carrefour.it/on/demandware.store/Sites-carrefour-IT-Site/it_IT/Search-ShowAjax?cgid=promozioni&prefn1=C4_PrimaryCategory&prefv1=$categoryUrl&storeId=0415") {

                        }
                    }
                    val jsonResponse = JSONObject(discountPage.body<String>())
                    val productArray = jsonResponse.getJSONArray("productIds")

                    val products = mutableListOf<Product>()
                    for(i in 0..<productArray.length()) {
                        val product = productArray.getJSONObject(i)
                        val productName = product.getString("productName").trim()
                        val brand = product.getString("brand")
                        val originalPriceObj = product.getJSONObject("price")
                        val discountedPrice = originalPriceObj.getJSONObject("sales").getDouble("value")
                        val originalPrice = if (originalPriceObj.isNull("list")) {
                            null
                        } else {
                            originalPriceObj.getJSONObject("list").getDouble("value")
                        }
                        val originalUnitPriceObj = product.getJSONObject("unitPrice")
                        val discountedUnitPrice = originalUnitPriceObj.getJSONObject("sales").getDouble("value")
                        val originalUnitPrice = if (originalUnitPriceObj.isNull("list")) {
                            null
                        } else {
                            originalUnitPriceObj.getJSONObject("list").getDouble("value")
                        }
                        var promotionName: String? = null
                        var promotionEndDate: String? = null
                        if (product.getJSONObject("promotionInfo").length() != 0) {
                            promotionName = product.getJSONObject("promotionInfo").getString("name")
                            promotionEndDate = product.getJSONObject("promotionInfo").getString("endDate")
                        }
                        val discountPercentage = product.getString("discountPercentage")

                        val jsonDesc = JSONObject()
                        jsonDesc.put("brand", brand)
                        jsonDesc.put("originalUnitPrice", originalUnitPrice)
                        jsonDesc.put("discountedUnitPrice", discountedUnitPrice)
                        promotionName.let {
                            jsonDesc.put("promotionName", promotionName)
                        }
                        promotionEndDate.let {
                            jsonDesc.put("promotionEndDate", promotionEndDate)
                        }
                        jsonDesc.put("discountPercentage", discountPercentage)

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
                return Result.Success(null)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error while fetching data from Carrefour", e)
            return Result.ServiceLayerError(ServiceError.UnknownError("Error while fetching data from Carrefour"))
        }
    }
}

enum class CarrefourCategory(override val id: String, override val displayName: String, val image: Int) : Category {
    ACQUA_SUCCHI_BIBITE("Acqua, succhi e bibite", "Acqua, Succhi e Bibite", R.drawable.acqua_succhi_bibite),
    ANIMALI("Animali", "Animali", R.drawable.animali),
    ARREDO_CASA("Arredo Casa", "Arredo Casa", R.drawable.arredo_casa),
    ARTICOLI_PER_BAMBINI("Articoli per bambini", "Articoli per Bambini", R.drawable.articoli_bambini),
    ARTICOLI_PER_LA_CASA("Articoli per la casa", "Articoli per la Casa", R.drawable.articoli_casa),
    BICICLETTE("Biciclette", "Biciclette", R.drawable.biciclette),
    BIRRA_E_LIQUORI("Birra e liquori", "Birra e Liquori", R.drawable.birra_liquori),
    CARNE("Carne", "Carne", R.drawable.carne),
    CLIMATIZZAZIONE("Climatizzazione", "Climatizzazione", R.drawable.climatizzazione),
    CONDIMENTI_E_CONSERVE("Condimenti e conserve", "Condimenti e Conserve", R.drawable.condimenti_conserve),
    CURA_CASA("Cura casa", "Cura casa", R.drawable.cura_casa),
    CURA_PERSONA("Cura persona", "Cura persona", R.drawable.cura_persona),
    DOLCI_E_PRIMA_COLAZIONE("Dolci e prima colazione", "Dolci e Prima Colazione", R.drawable.dolci_prima_colazione),
    ELETTRODOMESTICI_CUCINA("Elettrodomestici cucina", "Elettrodomestici Cucina", R.drawable.elettrodomestici_cucina),
    ELETTRODOMESTICI_PULIZIA_E_STIRO("Elettrodomestici pulizia e stiro", "Elettrodomestici Pulizia e Stiro", R.drawable.elettrodomestici_pulizia_stiro),
    FAI_DA_TE("Fai da te", "Fai da te", R.drawable.fai_da_te),
    FRUTTA_E_VERDURA("Frutta e verdura", "Frutta e Verdura", R.drawable.frutta_verdura),
    GASTRONOMIA("Gastronomia", "Gastronomia", R.drawable.gastronomia),
    GELATI_E_SURGELATI("Gelati e surgelati", "Gelati e Surgelati", R.drawable.surgelati_gelati),
    GIOCATTOLI("Giocattoli", "Giocattoli", R.drawable.articoli_bambini),
    GIOCHI_DA_ESTERNO("Giochi da esterno", "Giochi da Esterno", R.drawable.giochi_esterno),
    PANE_E_SNACK_SALATI("Pane e snack salati", "Pane e Snack Salati", R.drawable.pane_snack_salati),
    PASTA_RISO_E_FARINA("Pasta, riso e farina", "Pasta, Riso e Farina", R.drawable.pasta_riso_farina),
    PESCE("Pesce", "Pesce", R.drawable.pesce),
    PRIMA_INFANZIA("Prima Infanzia", "Prima Infanzia", R.drawable.prima_infanzia),
    SALUMI_E_FORMAGGI("Salumi e formaggi", "Salumi e Formaggi", R.drawable.salumi_formaggi),
    TELEFONIA("Telefonia", "Telefonia", R.drawable.telefonia),
    UOVA_LATTE_E_LATTICINI("Uova, latte e latticini", "Uova, Latte e Latticini", R.drawable.uova_latte_latticini),
    VINO("Vino", "Vino", R.drawable.vino),;

    companion object {
        val idList = entries.map { it.id }
        val categories = entries.map { Pair(it.id, it.displayName) }

        fun getIcon(category: String): Int {
            return entries.first { it.id == category }.image
        }
    }
}

package com.example.dealdetective

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.get
import kotlinx.coroutines.runBlocking
import org.json.JSONObject
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {

    @Test
    fun getScontiEsselunga() {

        val categories = intArrayOf(148031509, 148031507, 148031512, 148031510, 148031517, 148031517, 148031520, 148031516, 148031518, 148031514, 148031515, 148031508, 148031513)

        val client = HttpClient(CIO)

        runBlocking {
            /*val response = client.get("https://www.esselunga.it/it-it/promozioni/volantini.esselunga-di-gallarate.gal.html") {

            }

            println(response)
            val doc: Document = Ksoup.parse(html = response.body<String>())

            val codPromo = doc.select("div[data-cod-promo]").attr("data-cod-promo")

            println(doc.select("div[data-cod-promo]").attr("data-cod-promo"))
*/

            var promoCode = -1

            for (i in 0..500) {
                val discountPage = client.get("https://www.tigros.it/ebsn/api/leaflet/product-search?parent_leaflet_id=$i&parent_category_id=148031513") {

                }

                val jsonResponse = discountPage.body<String>()

                if (!jsonResponse.contains("\"totItems\":0")) {
                    promoCode = i
                    break
                }
            }

            if (promoCode == -1) {
                // Non trovo volantino
                return@runBlocking
            }

            categories.forEach { category ->
                val discountPage =
                    client.get("https://www.tigros.it/ebsn/api/leaflet/product-search?parent_leaflet_id=$promoCode&parent_category_id=$category") {

                    }

                var jsonString = discountPage.body<String>()
                var i = 0

                while (jsonString.contains("productInfos")) {
                    if (i % 2 == 0) {
                        jsonString = jsonString.replaceFirst("productInfos", "product-info-uff")
                        jsonString = jsonString.replaceFirst("dayLock", "day-lock-uff")
                    } else {
                        jsonString = jsonString.replaceFirst("productInfos", "fake-product-info")
                        jsonString = jsonString.replaceFirst("dayLock", "fake-day-lock")
                    }
                    i++
                }

                val jsonResponse = JSONObject(jsonString)

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
                    println(newProduct)
                }
            }
        }
    }
}
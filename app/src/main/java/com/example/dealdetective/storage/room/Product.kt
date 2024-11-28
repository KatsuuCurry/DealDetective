package com.example.dealdetective.storage.room
import androidx.room.ColumnInfo
import androidx.room.Entity
import org.json.JSONObject

@Entity(primaryKeys= ["productName", "storeId"], tableName = "Product")
data class Product (
    val productName: String,
    val storeId: Int,
    val originalPrice: Double?,
    val discountedPrice: Double,
    val category: String,

    /**
     * JSON object that may contains the brand, quantity, and discount percentage of the product.
     */

    @ColumnInfo(name = "json") val json: JSONObject,
)

package com.the_stilton_assistants.dealdetective.storage.room
import androidx.room.ColumnInfo
import androidx.room.Entity
import com.the_stilton_assistants.dealdetective.model.Product
import org.json.JSONObject

@Entity(primaryKeys = ["productName", "storeId"], tableName = "Product")
data class ProductRoom(
    override val productName: String,
    override val storeId: Int,
    override val originalPrice: Double?,
    override val discountedPrice: Double,
    override val category: String,
    override val isFavorite: Boolean = false,
    @ColumnInfo(name = "json") override val json: JSONObject?
): Product(productName, storeId, originalPrice, discountedPrice, category, isFavorite, json)

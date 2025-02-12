package com.the_stilton_assistants.dealdetective.model

import org.json.JSONObject

/**
 * Represents a product.
 */
open class Product (
    open val productName: String,
    open val storeId: Int,
    open val originalPrice: Double? = null,
    open val discountedPrice: Double,
    open val category: String,
    open val isFavorite: Boolean = false,

    /**
     * JSON object that may contains the brand, quantity, and discount percentage of the product.
     */
    open val json: JSONObject? = null,
)

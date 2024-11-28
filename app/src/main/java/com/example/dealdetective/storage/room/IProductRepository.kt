package com.example.dealdetective.storage.room

import kotlinx.coroutines.flow.Flow

/**
 * Repository that provides insert, update, delete, and retrieve of [Product] from a given data source.
 */
interface IProductRepository{
    /**
     * Retrieve all the Parcels from the the given data source.
     */
    fun getAllProducts(): List<Product>

    /**
     * Retrieve the flow of all the Parcels from the the given data source.
     */
    fun getAllProductsFlow(): Flow<List<Product>>

    /**
     * Retrieve a Parcel by its name and store id from the given data source.
     */
    fun getProductById(productName: String, storeId: Int): Product?

    /**
     * Retrieve all the Parcels by their store id and category from the given data source.
     */
    fun getProductsByStoreAndCategory(storeId: Int, category: String): List<Product>

    /**
     * Retrieve all the Parcels by their store id from the given data source.
     */
    fun getProductsByStore(storeId: Int): List<Product>

    /**
     * Insert Parcel in the data source.
     */
    fun insertAllProducts(vararg products: Product)

    /**
     * Delete Parcel from the data source.
     */
    fun delete(product: Product)

    /**
     * Delete a Parcel by its name and store id from the data source.
     */
    fun deleteProductById(productName: String, storeId: Int)

    /**
     * Delete all the Parcels from the data source.
     */
    fun deleteAllProducts()

}
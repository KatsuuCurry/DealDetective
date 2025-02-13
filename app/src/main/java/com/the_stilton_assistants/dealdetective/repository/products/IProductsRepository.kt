package com.the_stilton_assistants.dealdetective.repository.products

import com.the_stilton_assistants.dealdetective.model.Product
import com.the_stilton_assistants.dealdetective.util.Result
import kotlinx.coroutines.flow.Flow

/**
 * Repository that provides insert, update, delete, and retrieve of [Product] from a given data source.
 */
interface IProductsRepository{
    /**
     * Retrieve all the Products from the the given data source.
     */
    suspend fun getAllProducts(): Result<List<Product>>

    /**
     * Retrieve the flow of all the Products from the the given data source.
     */
    fun getAllProductsFlow(): Result<Flow<List<Product>>>

    /**
     * Retrieve the flow of 100 Products with the best discount from the the given data source.
     */
    fun getBestDiscountProducts(): Result<Flow<List<Product>>>

    /**
     * Retrieve a Product by its name and store id from the given data source.
     */
    suspend fun getProductById(storeId: Int, productName: String): Result<Product?>

    /**
     * Retrieve the flow of a Product by its name and store id from the given data source.
     */
    fun getProductByIdFlow(storeId: Int, productName: String): Result<Flow<Product?>>

    /**
     * Retrieve all the Products by their store id and category from the given data source.
     */
    suspend fun getProductsByStoreAndCategory(storeId: Int, category: String): Result<List<Product>>

    /**
     * Retrieve the flow of all the Products by their store id from the given data source as a flow.
     */
    fun getProductsByStoreFlow(storeId: Int): Result<Flow<List<Product>>>

    /**
     * Retrieve all the Products by their store id from the given data source.
     */
    suspend fun getProductsByStore(storeId: Int): Result<List<Product>>

    /**
     * Insert Products in the data source.
     */
    suspend fun insertAllProducts(vararg products: Product): Result<Unit>

    /**
     * Retrieve the flow of favorite Products from the the given data source.
     */
    fun getFavoriteProductsFlow(): Result<Flow<List<Product>>>

    /**
     * Update the favorite status of a Product in the data source.
     */
    suspend fun updateFavoriteStatus(product: Product, isFavorite: Boolean): Result<Unit>

    /**
     * Delete Product from the data source.
     */
    suspend fun delete(product: Product): Result<Unit>

    /**
     * Delete a Product by its name and store id from the data source.
     */
    suspend fun deleteProductById(productName: String, storeId: Int): Result<Unit>

    /**
     * Delete all the Products by their store id from the data source.
     */
    suspend fun deleteProductsByStoreType(storeId: Int): Result<Unit>

    /**
     * Delete all the Products from the data source.
     */
    suspend fun deleteAllProducts(): Result<Unit>
}

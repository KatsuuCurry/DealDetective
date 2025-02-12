package com.the_stilton_assistants.dealdetective.storage.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.RewriteQueriesToDropUnusedColumns
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {

    /*
     Return all the products in the database
     */
    @Query("SELECT * FROM product")
    suspend fun getAllProducts(): List<ProductRoom>

    /*
     Return all the products in the database
     */
    @Query("SELECT * FROM product")
    fun getAllProductsFlow(): Flow<List<ProductRoom>>

    /*
        Return 100 products with the best discount in the database
     */
    @RewriteQueriesToDropUnusedColumns
    @Query("""
        SELECT *, 
               ((COALESCE(originalPrice, 0) - discountedPrice) / COALESCE(originalPrice, 1)) * 100 AS discountPercentage
        FROM product
        WHERE originalPrice IS NOT NULL AND originalPrice > discountedPrice
        ORDER BY discountPercentage DESC
        LIMIT 100
    """)
    fun getBestDiscountProducts(): Flow<List<ProductRoom>>

    /*
     Return a product by its name and store id
     */
    @Query("SELECT * FROM product WHERE productName = :productName AND storeId = :storeId")
    fun getProductById(storeId: Int, productName: String): ProductRoom?

    /*
     Return a product by its name and store id as a flow
     */
    @Query("SELECT * FROM product WHERE productName = :productName AND storeId = :storeId")
    fun getProductByIdFlow(storeId: Int, productName: String): Flow<ProductRoom?>

    /*
     Return all the products by their store id and category
     */
    @Query("SELECT * FROM product WHERE storeId = :storeId AND category = :category")
    suspend fun getProductsByStoreAndCategory(storeId: Int, category: String): List<ProductRoom>

    /*
     Return all the products by their store id as a flow
     */
    @Query("SELECT * FROM product WHERE storeId = :storeId")
    fun getProductsByStoreFlow(storeId: Int): Flow<List<ProductRoom>>

    /*
     Return all the products by their store id
     */
    @Query("SELECT * FROM product WHERE storeId = :storeId")
    suspend fun getProductsByStore(storeId: Int): List<ProductRoom>

    /*
     Insert a list of products in the database
     */
    @Insert
    suspend fun insertAllProducts(vararg products: ProductRoom)

    /*
     Return all favorite products
     */
    @Query("SELECT * FROM product WHERE isFavorite = 1")
    fun getFavoriteProductsFlow(): Flow<List<ProductRoom>>

    /*
     Update the favorite status of a product
     */
    @Query("UPDATE product SET isFavorite = :isFavorite WHERE storeId = :storeId AND productName = :productName")
    suspend fun updateFavoriteStatus(storeId: Int, productName: String, isFavorite: Boolean)

    /*
     Delete a product from the database
     */
    @Delete
    suspend fun delete(product: ProductRoom)

    /*
     Delete a product by its name and store id
     */
    @Query("DELETE FROM product WHERE productName = :productName AND storeId = :storeId")
    suspend fun deleteProductById(productName: String, storeId: Int)

    /*
     Delete all the products by their store id
     */
    @Query("DELETE FROM product WHERE storeId = :storeId")
    suspend fun deleteProductsByStoreType(storeId: Int)

    /*
     Delete all the products from the database
     */
    @Query("DELETE FROM product")
    suspend fun deleteAllProducts()
}

package com.example.dealdetective.storage.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {

    /*
     Return all the products in the database
     */
    @Query("SELECT * FROM product")
    fun getAllProducts(): List<Product>

    /*
     Return all the products in the database
     */
    @Query("SELECT * FROM product")
    fun getAllProductsFlow(): Flow<List<Product>>

    /*
     Return a product by its name and store id
     */
    @Query("SELECT * FROM product WHERE productName = :productName AND storeId = :storeId")
    fun getProductById(productName: String, storeId: Int): Product?

    /*
     Return all the products by their store id and category
     */
    @Query("SELECT * FROM product WHERE storeId = :storeId AND category = :category")
    fun getProductsByStoreAndCategory(storeId: Int, category: String): List<Product>

    /*
     Return all the products by their store id
     */
    @Query("SELECT * FROM product WHERE storeId = :storeId")
    fun getProductsByStore(storeId: Int): List<Product>

    /*
     Insert a list of products in the database
     */
    @Insert
    fun insertAllProducts(vararg products: Product)

    /*
     Delete a product from the database
     */
    @Delete
    fun delete(product: Product)

    /*
     Delete a product by its name and store id
     */
    @Query("DELETE FROM product WHERE productName = :productName AND storeId = :storeId")
    fun deleteProductById(productName: String, storeId: Int)

    /*
     Delete all the products from the database
     */
    @Query("DELETE FROM product")
    fun deleteAllProducts()
}
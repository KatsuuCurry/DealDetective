package com.example.dealdetective.storage.room

import kotlinx.coroutines.flow.Flow

class ProductRepository(private val productDao: ProductDao) : IProductRepository {

    override fun getAllProducts(): List<Product> = productDao.getAllProducts()

    override fun getAllProductsFlow(): Flow<List<Product>> = productDao.getAllProductsFlow()

    override fun getProductById(productName: String, storeId: Int): Product? = productDao.getProductById(productName, storeId)

    override fun getProductsByStoreAndCategory(storeId: Int, category: String): List<Product> = productDao.getProductsByStoreAndCategory(storeId, category)

    override fun getProductsByStore(storeId: Int): List<Product> = productDao.getProductsByStore(storeId)

    override fun insertAllProducts(vararg products: Product) = productDao.insertAllProducts(*products)

    override fun delete(product: Product) = productDao.delete(product)

    override fun deleteProductById(productName: String, storeId: Int) = productDao.deleteProductById(productName, storeId)

    override fun deleteAllProducts() = productDao.deleteAllProducts()
}
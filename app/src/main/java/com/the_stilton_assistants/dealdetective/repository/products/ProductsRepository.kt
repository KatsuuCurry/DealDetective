package com.the_stilton_assistants.dealdetective.repository.products

import android.util.Log
import com.the_stilton_assistants.dealdetective.model.Product
import com.the_stilton_assistants.dealdetective.repository.RepositoryError
import com.the_stilton_assistants.dealdetective.storage.room.ProductDao
import com.the_stilton_assistants.dealdetective.storage.room.ProductRoom
import com.the_stilton_assistants.dealdetective.util.Result
import kotlinx.coroutines.flow.Flow

private const val TAG = "ProductsRepository"

class ProductsRepository(private val productDao: ProductDao) : IProductsRepository {

    override suspend fun getAllProducts(): Result<List<Product>> {
        return try {
            Result.Success(productDao.getAllProducts())
        } catch (e: Exception) {
            Log.e(TAG, "Error getting all products", e)
            Result.Error(RepositoryError.UnknownError("Error getting all products"))
        }
    }

    override fun getAllProductsFlow(): Result<Flow<List<Product>>> {
        return try {
            Result.Success(productDao.getAllProductsFlow())
        } catch (e: Exception) {
            Log.e(TAG, "Error getting all products flow", e)
            Result.Error(RepositoryError.UnknownError("Error getting all products flow"))
        }
    }

    override fun getBestDiscountProducts(): Result<Flow<List<Product>>> {
        return try {
            Result.Success(productDao.getBestDiscountProducts())
        } catch (e: Exception) {
            Log.e(TAG, "Error getting best discount products", e)
            Result.Error(RepositoryError.UnknownError("Error getting best discount products"))
        }
    }

    override suspend fun getProductById(storeId: Int, productName: String): Result<Product?> {
        return try {
            Result.Success(productDao.getProductById(storeId, productName))
        } catch (e: Exception) {
            Log.e(TAG, "Error getting product by id", e)
            Result.Error(RepositoryError.UnknownError("Error getting product by id"))
        }
    }


    override fun getProductByIdFlow(storeId: Int, productName: String): Result<Flow<Product?>> {
        return try {
            Result.Success(productDao.getProductByIdFlow(storeId, productName))
        } catch (e: Exception) {
            Log.e(TAG, "Error getting product by id", e)
            Result.Error(RepositoryError.UnknownError("Error getting product by id"))
        }
    }

    override suspend fun getProductsByStoreAndCategory(
        storeId: Int,
        category: String
    ): Result<List<Product>> {
        return try {
            Result.Success(productDao.getProductsByStoreAndCategory(storeId, category))
        } catch (e: Exception) {
            Log.e(TAG, "Error getting products by store and category", e)
            Result.Error(RepositoryError.UnknownError("Error getting products by store and category"))
        }
    }

    override fun getProductsByStoreFlow(storeId: Int): Result<Flow<List<Product>>> {
        return try {
            Result.Success(productDao.getProductsByStoreFlow(storeId))
        } catch (e: Exception) {
            Log.e(TAG, "Error getting products by store flow", e)
            Result.Error(RepositoryError.UnknownError("Error getting products by store flow"))
        }
    }

    override suspend fun getProductsByStore(storeId: Int): Result<List<Product>> {
        return try {
            Result.Success(productDao.getProductsByStore(storeId))
        } catch (e: Exception) {
            Log.e(TAG, "Error getting products by store", e)
            Result.Error(RepositoryError.UnknownError("Error getting products by store"))
        }
    }

    override suspend fun insertAllProducts(vararg products: Product): Result<Unit> {
        return try {
            val productRooms = products.map {
                ProductRoom(
                    it.productName,
                    it.storeId,
                    it.originalPrice,
                    it.discountedPrice,
                    it.category,
                    it.isFavorite,
                    it.json,
                )
            }.toTypedArray()
            productDao.insertAllProducts(*productRooms)
            Result.Success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error inserting all products", e)
            Result.Error(RepositoryError.UnknownError("Error inserting all products"))
        }
    }

    override fun getFavoriteProductsFlow(): Result<Flow<List<Product>>> {
        return try {
            Result.Success(productDao.getFavoriteProductsFlow())
        } catch (e: Exception) {
            Log.e(TAG, "Error getting favorite products flow", e)
            Result.Error(RepositoryError.UnknownError("Error getting favorite products flow"))
        }
    }

    override suspend fun updateFavoriteStatus(product: Product, isFavorite: Boolean): Result<Unit> {
        return try {
            productDao.updateFavoriteStatus(product.storeId, product.productName, isFavorite)
            Result.Success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error updating favorite status", e)
            Result.Error(RepositoryError.UnknownError("Error updating favorite status"))
        }
    }

    override suspend fun delete(product: Product): Result<Unit> {
        return try {
            productDao.delete(product as ProductRoom)
            Result.Success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting product", e)
            Result.Error(RepositoryError.UnknownError("Error deleting product"))
        }
    }

    override suspend fun deleteProductById(productName: String, storeId: Int): Result<Unit> {
        return try {
            productDao.deleteProductById(productName, storeId)
            Result.Success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting product by id", e)
            Result.Error(RepositoryError.UnknownError("Error deleting product by id"))
        }
    }

    override suspend fun deleteProductsByStoreType(storeId: Int): Result<Unit> {
        return try {
            productDao.deleteProductsByStoreType(storeId)
            Result.Success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting products by store type", e)
            Result.Error(RepositoryError.UnknownError("Error deleting products by store type"))
        }
    }

    override suspend fun deleteAllProducts(): Result<Unit> {
        return try {
            productDao.deleteAllProducts()
            Result.Success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting all products", e)
            Result.Error(RepositoryError.UnknownError("Error deleting all products"))
        }
    }
}

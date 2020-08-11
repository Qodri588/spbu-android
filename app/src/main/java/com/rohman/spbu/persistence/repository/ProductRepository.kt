package com.rohman.spbu.persistence.repository

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import com.rohman.spbu.persistence.dao.ProductDao
import com.rohman.spbu.model.Produk

class ProductRepository(private val productDao: ProductDao) {
    var allproducts: LiveData<List<Produk>> = productDao.getAllProducts()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun getProductNotReactive(): List<Produk>{
        return productDao.getProductNotReactive()
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(produk: Produk) {
        productDao.insert(produk)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun delete(produk: Produk) {
        productDao.delete(produk)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun update(produk: Produk) {
        productDao.update(produk)
    }

}
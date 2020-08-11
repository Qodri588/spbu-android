package com.rohman.spbu.persistence.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.rohman.spbu.model.Produk


@Dao
interface ProductDao {
    @Insert
    fun insert(produk: Produk)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(produk: Produk)

    @Delete
    fun delete(produk: Produk)

    @Query("SELECT * FROM product_table")
    fun getAllProducts(): LiveData<List<Produk>>

    @Query("SELECT * FROM product_table")
    fun getProductNotReactive(): List<Produk>

}
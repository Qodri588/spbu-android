package com.rohman.spbu.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "product_table")
data class Produk(
    @PrimaryKey(autoGenerate = true)
    var id: Int?,
    var nama: String = "",
    var harga: Double = 0.0
){

    override fun toString(): String {
        return this.nama
    }
}
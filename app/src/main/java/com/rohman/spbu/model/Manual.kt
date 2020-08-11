package com.rohman.spbu.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "manual_table")
data class Manual(
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null,
    var logo: String = "",
    var nomor: String = "",
    var nama: String = "",
    var alamat: String = "",
    var shift: Int = 0,
    var no_transaksi: Int = 0,
    var waktu: String = "",
    var pompa: Int = 0,
    var produk: String = "",
    var harga_per_liter: Double = 0.0,
    var volume: Double = 0.0,
    var total_harga: Double = 0.0,
    var operator: String = "",
    var cash: Double = 0.0,
    var status: Boolean=false,
    var no_plat: String = "",
    var odometer: String = ""
) : Parcelable
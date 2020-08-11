package com.rohman.spbu.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "template_table")
data class Template(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    var logo: String= "",
    var nomor: String = "",
    var nama: String = "",
    var alamat: String = "",
    var operator: String = ""
)
package com.rohman.spbu.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "foto_table")
data class Foto(
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null,
    var date: String = "",
    var foto: String = "",
    var status: Boolean = false
)
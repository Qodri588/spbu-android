package com.rohman.spbu.persistence.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.rohman.spbu.model.Foto

@Dao
interface FotoDao{

    @Query("SELECT * FROM foto_table ORDER BY date DESC LIMIT 5")
    fun getFoto(): LiveData<List<Foto>>

    @Query("SELECT * FROM foto_table ORDER BY date DESC")
    fun getAllFoto(): LiveData<List<Foto>>

    @Insert
    fun insert(foto: Foto)

    @Delete
    fun delete(foto: Foto)

}
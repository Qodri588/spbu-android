package com.rohman.spbu.persistence.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.rohman.spbu.model.Manual

@Dao
interface ManualDao {

    @Insert
    fun insert(manual: Manual)

    @Delete
    fun delete(manual: Manual)

    @Query("SELECT * FROM manual_table ORDER BY waktu DESC LIMIT 5")
    fun getManual(): LiveData<List<Manual>>

    @Query("SELECT * FROM manual_table ORDER BY waktu DESC LIMIT 5")
    fun getAllManual(): LiveData<List<Manual>>

}
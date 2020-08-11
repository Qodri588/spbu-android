package com.rohman.spbu.persistence.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.rohman.spbu.model.Template

@Dao
interface TemplateDao {

    @Update
    fun update(template: Template)

    @Insert
    fun insert(template: Template)

    @Query("SELECT * FROM template_table LIMIT 1")
    fun getTemplate(): LiveData<Template>
}
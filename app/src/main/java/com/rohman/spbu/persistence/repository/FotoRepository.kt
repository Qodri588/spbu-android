package com.rohman.spbu.persistence.repository

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.room.RoomDatabase
import com.rohman.spbu.model.Foto
import com.rohman.spbu.persistence.dao.FotoDao

class FotoRepository(private val fotoDao: FotoDao){

    var foto = fotoDao.getFoto()

    var allFoto = fotoDao.getAllFoto()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(foto: Foto){
        fotoDao.insert(foto)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun delete(foto: Foto){
        fotoDao.delete(foto)
    }

}
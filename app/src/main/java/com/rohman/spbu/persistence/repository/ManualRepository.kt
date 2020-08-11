package com.rohman.spbu.persistence.repository

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import com.rohman.spbu.model.Manual
import com.rohman.spbu.persistence.dao.ManualDao

class ManualRepository(private val manualDao: ManualDao) {
    var manual: LiveData<List<Manual>> = manualDao.getManual()
    var allManual: LiveData<List<Manual>> = manualDao.getAllManual()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(manual: Manual) {
        manualDao.insert(manual)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun delete(manual: Manual) {
        manualDao.delete(manual)
    }
}
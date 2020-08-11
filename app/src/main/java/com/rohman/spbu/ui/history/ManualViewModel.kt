package com.rohman.spbu.ui.history

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.rohman.spbu.model.Manual
import com.rohman.spbu.persistence.local.AppDatabase
import com.rohman.spbu.persistence.repository.ManualRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ManualViewModel(private val app: Application) : AndroidViewModel(app){
    var manualRepository: ManualRepository
    var manual: LiveData<List<Manual>>
    var allManual: LiveData<List<Manual>>

    init {
        val manualDao = AppDatabase.getInstance(app,viewModelScope).manualDao()
        manualRepository = ManualRepository(manualDao)
        manual = manualRepository.manual
        allManual = manualRepository.allManual
    }

    fun delete(manual:Manual) = viewModelScope.launch(Dispatchers.IO) {
        manualRepository.delete(manual)
    }

}
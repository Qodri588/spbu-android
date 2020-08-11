package com.rohman.spbu.ui.historyView

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.rohman.spbu.model.Foto
import com.rohman.spbu.model.Manual
import com.rohman.spbu.persistence.local.AppDatabase
import com.rohman.spbu.persistence.repository.FotoRepository
import com.rohman.spbu.persistence.repository.ManualRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HistoryViewViewModel (private val app: Application): AndroidViewModel(app){

    var repository: FotoRepository
    var manualRepo: ManualRepository
    var foto: LiveData<List<Foto>>
    var allFoto: LiveData<List<Foto>>
    var manual: LiveData<List<Manual>>

    init {
        val fotoDao = AppDatabase.getInstance(app,viewModelScope).fotoDao()
        repository = FotoRepository(fotoDao)
        foto = repository.foto
        allFoto = repository.allFoto
        val manualDao = AppDatabase.getInstance(app,viewModelScope).manualDao()
        manualRepo = ManualRepository(manualDao)
        manual = manualRepo.allManual
    }

    fun insert(foto: Foto) = viewModelScope.launch(Dispatchers.IO){
        repository.insert(foto)
    }

    fun delete(foto: Foto) = viewModelScope.launch(Dispatchers.IO){
        repository.delete(foto)
    }

}
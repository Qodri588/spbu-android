package com.rohman.spbu.ui.history

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.rohman.spbu.model.Foto
import com.rohman.spbu.persistence.local.AppDatabase
import com.rohman.spbu.persistence.repository.FotoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HistoryFotoViewModel (private val app: Application): AndroidViewModel(app){

    var repository: FotoRepository
    var foto: LiveData<List<Foto>>
    var allFoto: LiveData<List<Foto>>

    init {
        val fotoDao = AppDatabase.getInstance(app,viewModelScope).fotoDao()
        repository = FotoRepository(fotoDao)
        foto = repository.foto
        allFoto = repository.allFoto
    }

    fun insert(foto: Foto) = viewModelScope.launch(Dispatchers.IO){
        repository.insert(foto)
    }

    fun delete(foto: Foto) = viewModelScope.launch(Dispatchers.IO){
        repository.delete(foto)
    }

}
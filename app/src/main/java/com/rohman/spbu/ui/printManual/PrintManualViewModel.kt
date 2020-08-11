package com.rohman.spbu.ui.printManual

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.rohman.spbu.model.Manual
import com.rohman.spbu.model.Produk
import com.rohman.spbu.model.Template
import com.rohman.spbu.persistence.local.AppDatabase
import com.rohman.spbu.persistence.repository.ManualRepository
import com.rohman.spbu.persistence.repository.ProductRepository
import com.rohman.spbu.persistence.repository.TemplateRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PrintManualViewModel(app: Application) : AndroidViewModel(app) {
    private var repository: ProductRepository
    var products: LiveData<List<Produk>>
    var template: LiveData<Template>
    private var templateRepository: TemplateRepository
    var manualRepository: ManualRepository

    init {
        val templateDao = AppDatabase.getInstance(app, viewModelScope).templateDao()
        templateRepository = TemplateRepository(templateDao)
        template = templateRepository.template

        val manualDao = AppDatabase.getInstance(app, viewModelScope).manualDao()
        manualRepository = ManualRepository(manualDao)

        val productDao = AppDatabase.getInstance(app, viewModelScope).productDao()
        repository =
            ProductRepository(productDao)
        products = repository.allproducts
    }

    fun insert(manual: Manual) = viewModelScope.launch(Dispatchers.IO) {
        manualRepository.insert(manual)
    }

}
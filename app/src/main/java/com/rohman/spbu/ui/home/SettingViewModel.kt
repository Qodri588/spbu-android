package com.rohman.spbu.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.rohman.spbu.model.Produk
import com.rohman.spbu.model.Template
import com.rohman.spbu.persistence.dao.ProductDao
import com.rohman.spbu.persistence.local.AppDatabase
import com.rohman.spbu.persistence.repository.ProductRepository
import com.rohman.spbu.persistence.repository.TemplateRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SettingViewModel(val app: Application) : AndroidViewModel(app) {
    private var repository: ProductRepository
    var products: LiveData<List<Produk>>
    var productsNotReactive = MutableLiveData<List<Produk>>()
    var templateNotReactive = MutableLiveData<Template>()
    var deviceConnection = MutableLiveData<Boolean>()
    private var productDao: ProductDao

    var template: LiveData<Template>? = null
    private var templateRepository: TemplateRepository


    init {
        val templateDao = AppDatabase.getInstance(app, viewModelScope).templateDao()
        templateRepository = TemplateRepository(templateDao)
        template = templateRepository.template

        productDao = AppDatabase.getInstance(app, viewModelScope).productDao()
        repository =
            ProductRepository(productDao)
        products = repository.allproducts
    }

    fun getNotReactiveProduct() = viewModelScope.launch(Dispatchers.IO){
        productsNotReactive.postValue(repository.getProductNotReactive())
    }

    fun getNotReactiveTemplate() = viewModelScope.launch(Dispatchers.IO){
        templateNotReactive.postValue(templateRepository.getTemplateNotReactive())
    }

    fun updateTemplate(template: Template) = viewModelScope.launch(Dispatchers.IO) {
        templateRepository.update(template = template)
    }

    fun insert(produk: Produk) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(produk)
        productsNotReactive.postValue(productDao.getProductNotReactive())
    }

    fun delete(produk: Produk) = viewModelScope.launch(Dispatchers.IO) {
        repository.delete(produk)
        productsNotReactive.postValue(productDao.getProductNotReactive())
    }

    fun update(produk: Produk) = viewModelScope.launch(Dispatchers.IO) {
        repository.update(produk)
    }

}
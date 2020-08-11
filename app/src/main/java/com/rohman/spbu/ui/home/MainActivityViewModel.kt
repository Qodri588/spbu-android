package com.rohman.spbu.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainActivityViewModel : ViewModel(){

    var connection_status =  MutableLiveData<Boolean>()

    init {
        connection_status.value = false
    }

    fun setConnectionStatus(state: Boolean){
        connection_status.value = state
    }

    fun getConnectionStatus(): LiveData<Boolean>{
        return connection_status
    }

}
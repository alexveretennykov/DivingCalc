package com.example.divecalculator

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class ViewModel(app: Application) : AndroidViewModel(app)  {

    private val _clicks = MutableLiveData<Int>()
    var clicks: LiveData<Int> = _clicks

    init {
        _clicks.value = 0
    }

    fun addClick(){
        _clicks.value = _clicks.value!! + 1
    }

    fun runAd(): Boolean{
        return _clicks.value!! % 3 == 0
    }
}
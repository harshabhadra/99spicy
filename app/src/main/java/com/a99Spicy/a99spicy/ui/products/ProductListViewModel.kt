package com.a99Spicy.a99spicy.ui.products

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

private var quantity = 0
class ProductListViewModel : ViewModel() {

    private val viewModelJob = Job()

    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private var _productQtyMutableLiveData = MutableLiveData<Int>()
    val productQtyLiveData:LiveData<Int>
    get() = _productQtyMutableLiveData

    init {
        _productQtyMutableLiveData.value = 0
    }

    fun addQuantity(){
        quantity++
        _productQtyMutableLiveData.value = quantity
    }

    fun minusQuantity(){
        if(quantity>=1){
            quantity--
            _productQtyMutableLiveData.value = quantity
        }
    }
    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}
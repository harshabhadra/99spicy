package com.a99Spicy.a99spicy.ui.order

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.a99Spicy.a99spicy.network.Api
import com.a99Spicy.a99spicy.network.OrderResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber

enum class MyOrdersLoading{
    LOADING_SUCCESS, LOADING_FAILED, LOADING_PENDING
}

class MyOrdersViewModel :ViewModel(){

    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)
    private val apiService = Api.retrofitService

    //Store all orders
    private var _allOrdersMutableLiveData = MutableLiveData<List<OrderResponse>>()
    val allOrdersLiveData: LiveData<List<OrderResponse>>
        get() = _allOrdersMutableLiveData

    private var _loadingMutableLiveData = MutableLiveData<MyOrdersLoading>()
    val loadingLiveData:LiveData<MyOrdersLoading>
    get() = _loadingMutableLiveData

    init {
        _allOrdersMutableLiveData.value = null
        _loadingMutableLiveData.value = null
    }

    //Get all orders
    fun getAllOrders(customerId:Int){
        uiScope.launch {
            val responseDeferred = apiService.getAllOrdersAsync(customerId)
            try {
                _loadingMutableLiveData.value = MyOrdersLoading.LOADING_PENDING
                val response = responseDeferred.await()
                _allOrdersMutableLiveData.value = response
                _loadingMutableLiveData.value = MyOrdersLoading.LOADING_SUCCESS
                Timber.e("Successfully received all orders")
            }catch (e:Exception){
                Timber.e("Failed to get orders: ${e.message}")
                _allOrdersMutableLiveData.value = null
                _loadingMutableLiveData.value = MyOrdersLoading.LOADING_FAILED
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}
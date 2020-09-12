package com.a99Spicy.a99spicy.ui.order

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.a99Spicy.a99spicy.network.Api
import com.a99Spicy.a99spicy.network.OrderResponse
import com.a99Spicy.a99spicy.network.PlaceOrder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber

enum class Loading{
    SUCCESS, FAILED, PENDING
}

class OrderViewModel : ViewModel() {

    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)
    private val apiService = Api.retrofitService

    //Store single order response
    private var _orderMutableLiveData = MutableLiveData<OrderResponse>()
    val orderLiveData: LiveData<OrderResponse>
        get() = _orderMutableLiveData

    private var _loadingMutableLiveData = MutableLiveData<Loading>()
    val loadingLiveData:LiveData<Loading>
    get() = _loadingMutableLiveData

    init {
        _loadingMutableLiveData.value = null
        _orderMutableLiveData.value = null
    }

    //Place order
    fun placeOrder(order: PlaceOrder){
        uiScope.launch {
            val responseDeferred = apiService.placeOrderAsync(order)
            try {
                _loadingMutableLiveData.value = Loading.PENDING
                val response = responseDeferred.await()
                _orderMutableLiveData.value = response
                _loadingMutableLiveData.value = Loading.SUCCESS
            }catch (e:Exception){
                Timber.e("Failed to place order: ${e.message}")
                _orderMutableLiveData.value = null
                _loadingMutableLiveData.value = Loading.FAILED
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}
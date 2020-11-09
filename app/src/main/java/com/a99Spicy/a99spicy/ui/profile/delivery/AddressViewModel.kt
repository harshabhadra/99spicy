package com.a99Spicy.a99spicy.ui.profile.delivery

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.a99Spicy.a99spicy.network.Address
import com.a99Spicy.a99spicy.network.Api
import com.a99Spicy.a99spicy.network.Profile
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import timber.log.Timber
import java.lang.Exception

enum class AddressLoading{
    SUCCESS, FAILED, PENDING
}

class AddressViewModel :ViewModel(){

    private val apiService = Api.retrofitService

    private var _updateShippingMutableLiveData = MutableLiveData<Profile>()
    val updateShippingLiveData:LiveData<Profile>
    get() = _updateShippingMutableLiveData

    private var _loadingMutableLiveData = MutableLiveData<AddressLoading>()
    val loadingLiveData:LiveData<AddressLoading>
    get() = _loadingMutableLiveData

    init {
        _loadingMutableLiveData.value = null
        _updateShippingMutableLiveData.value = null
    }

    //Update shipping
    fun updateShipping(id:String, address:Address){
        viewModelScope.launch {
            val responseDeferred = apiService.setDeliveryAddressAsync(id, address)
            _loadingMutableLiveData.value = AddressLoading.PENDING
            try {
                val response = responseDeferred.await()
                Timber.e("Shipping update response: ${response.shipping.city}")
                _loadingMutableLiveData.value = AddressLoading.SUCCESS
                _updateShippingMutableLiveData.value = response
            }catch (e:Exception){
                _loadingMutableLiveData.value = AddressLoading.FAILED
                Timber.e("Failed to update shipping: ${e.message}")
                _updateShippingMutableLiveData.value = null
            }
        }
    }
    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
    }
}
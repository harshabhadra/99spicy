package com.a99Spicy.a99spicy.user

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.a99Spicy.a99spicy.network.Address
import com.a99Spicy.a99spicy.network.Api
import com.a99Spicy.a99spicy.network.Profile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber

enum class LocationLoading {
    SUCCESS, PENDING, FAILED
}

class LocationViewModel : ViewModel() {

    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)
    private val apiService = Api.retrofitService

    //Store address data
    private var _addressMutableLiveData = MutableLiveData<Profile>()
    val addressLiveData: LiveData<Profile>
        get() = _addressMutableLiveData

    private var _loadingMutableLiveData = MutableLiveData<LocationLoading>()
    val loadingLiveData: LiveData<LocationLoading>
        get() = _loadingMutableLiveData

    init {
        _addressMutableLiveData.value = null
        _loadingMutableLiveData.value = null
    }

    //Update profile address
    fun setAddress(id: Int, shipping: Address) {
        uiScope.launch {
            val addressDeferred = apiService.setDeliveryAddressAsync(id.toString(), shipping)
            try {
                _loadingMutableLiveData.value = LocationLoading.PENDING
                val response = addressDeferred.await()
                _addressMutableLiveData.value = response
                _loadingMutableLiveData.value = LocationLoading.SUCCESS
                Timber.e("Address saved response received successfully: ${response.shipping.firstName}")
            } catch (e: Exception) {
                _loadingMutableLiveData.value = LocationLoading.FAILED
                Timber.e("Failed to update address: ${e.message}")
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}
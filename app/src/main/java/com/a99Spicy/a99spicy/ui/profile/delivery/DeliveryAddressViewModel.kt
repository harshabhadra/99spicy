package com.a99Spicy.a99spicy.ui.profile.delivery

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.a99Spicy.a99spicy.MyApplication
import com.a99Spicy.a99spicy.Repository
import com.a99Spicy.a99spicy.database.DatabaseShipping
import com.a99Spicy.a99spicy.database.MyDatabase
import com.a99Spicy.a99spicy.domain.DeliveryAddress
import com.a99Spicy.a99spicy.network.Address
import com.a99Spicy.a99spicy.network.Api
import com.a99Spicy.a99spicy.network.Profile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber

class DeliveryAddressViewModel(private val application: MyApplication) : ViewModel() {

    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)
    private val database = MyDatabase.getDatabase(application)
    private val repository = Repository(database)

    private val apiService = Api.retrofitService

    //Store address data
    private var _addressMutableLiveData = MutableLiveData<Profile>()
    val addressLiveData: LiveData<Profile>
        get() = _addressMutableLiveData

    val deliveryAddressListLiveData:LiveData<List<DeliveryAddress>> = repository.deliveryAddressList

    init {
        _addressMutableLiveData.value = null
    }

    fun setAddress(id: Int, shipping: Address) {
        uiScope.launch {
            val addressDeferred = apiService.setDeliveryAddressAsync(id.toString(), shipping)
            try {
                val response = addressDeferred.await()
                _addressMutableLiveData.value = response
                Timber.e("Address saved response received successfully: ${response.shipping.firstName}")
            } catch (e: Exception) {
                Timber.e("Failed to update address: ${e.message}")
            }
        }
    }

    //Add address
    fun addAddress(databaseShipping: DatabaseShipping){
        uiScope.launch {
            repository.addAddress(databaseShipping)
        }
    }

    //Delete address
    fun removeAddress(databaseShipping: DatabaseShipping){
        uiScope.launch {
            repository.removeAddress(databaseShipping)
        }
    }
}

class DeliveryAddressViewModelFactory(private val application: MyApplication) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DeliveryAddressViewModel::class.java)) {
            return DeliveryAddressViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}
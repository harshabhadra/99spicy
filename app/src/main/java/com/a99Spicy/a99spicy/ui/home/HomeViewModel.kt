package com.a99Spicy.a99spicy.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.a99Spicy.a99spicy.domain.LocationDetails
import com.a99Spicy.a99spicy.network.Api
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

class HomeViewModel : ViewModel() {

    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private val apiService = Api.retroService

    private var _locationMutableLiveData = MutableLiveData<LocationDetails>()
    val locationLiveData:LiveData<LocationDetails>
    get() = _locationMutableLiveData

    private var _ordersMutableLiveData = MutableLiveData<String>()
    val ordersLiveData:LiveData<String>
    get() = _ordersMutableLiveData

    init {
        _locationMutableLiveData.value = null
        _ordersMutableLiveData.value = null
    }

    fun getOrders(){

        uiScope.launch {

            apiService.getOrders("", "")
                .enqueue(object :Callback<String>{
                    override fun onFailure(call: Call<String>, t: Throwable) {
                        Timber.e("Failed to get response: ${t.message}")
                        _ordersMutableLiveData.value = null
                    }

                    override fun onResponse(call: Call<String>, response: Response<String>) {
                        response.body()?.let {
                            if (response.isSuccessful){
                                Timber.e("Response received successfully: $it")
                                _ordersMutableLiveData.value = it
                            }else{
                                Timber.e("Response received unsuccessful: ${response.errorBody()}")
                                _ordersMutableLiveData.value = null
                            }
                        }?:let {
                            Timber.e("Empty response: ${response.body().toString()}")
                        }
                    }
                })
        }
    }
    fun setLocationData(locationDetails: LocationDetails){
        _locationMutableLiveData.value = locationDetails
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}
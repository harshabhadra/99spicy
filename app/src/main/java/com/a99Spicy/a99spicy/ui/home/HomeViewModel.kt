package com.a99Spicy.a99spicy.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.a99Spicy.a99spicy.domain.LocationDetails

class HomeViewModel : ViewModel() {

    private var _locationMutableLiveData = MutableLiveData<LocationDetails>()
    val locationLiveData:LiveData<LocationDetails>
    get() = _locationMutableLiveData

    init {
        _locationMutableLiveData.value = null
    }

    fun setLocationData(locationDetails: LocationDetails){
        _locationMutableLiveData.value = locationDetails
    }
}
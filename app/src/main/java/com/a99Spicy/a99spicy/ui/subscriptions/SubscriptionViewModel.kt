package com.a99Spicy.a99spicy.ui.subscriptions

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.a99Spicy.a99spicy.network.Api
import com.a99Spicy.a99spicy.network.Subscription
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import timber.log.Timber

enum class Loading {
    SUCCESS, FAILED, PENDING
}

private var page = 1
private var endFetch = false
private val subsList: MutableList<Subscription> = mutableListOf()

class SubscriptionViewModel : ViewModel() {

    private val apiService = Api.subscribeService

    private var _subscriptionsMutableLiveData = MutableLiveData<List<Subscription>>()
    val subscriptionsLiveData: LiveData<List<Subscription>>
        get() = _subscriptionsMutableLiveData

    private var _loadingMutableLiveData = MutableLiveData<Loading>()
    val loadingLiveData: LiveData<Loading>
        get() = _loadingMutableLiveData

    init {
        subsList.clear()
        _loadingMutableLiveData.value = null
        _subscriptionsMutableLiveData.value = null
    }

    //Get Subscription list
    fun getAllSubscriptions(customerId:String) {
        viewModelScope.launch {
                _loadingMutableLiveData.value = Loading.PENDING
                val responseDeferred = apiService.getAllSubscriptionsAsync(customerId,page, 100)
                try {
                    val response = responseDeferred.await()
                    subsList.addAll(response)
                    _subscriptionsMutableLiveData.value = subsList.toList()
                    _loadingMutableLiveData.value = Loading.SUCCESS
                } catch (e: Exception) {
                    Timber.e("Failed to get subscriptions: ${e.message}")
                    _subscriptionsMutableLiveData.value = null
                    _loadingMutableLiveData.value = Loading.FAILED
                }
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
    }
}
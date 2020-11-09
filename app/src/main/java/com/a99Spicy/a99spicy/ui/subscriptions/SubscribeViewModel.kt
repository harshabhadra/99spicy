package com.a99Spicy.a99spicy.ui.subscriptions

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.a99Spicy.a99spicy.network.*
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import java.lang.Exception

enum class SubsLoading{
    SUCCESS, FAILED, PENDING
}
private var quantity = 1
class SubscribeViewModel : ViewModel() {

    private val apiService = Api.subscribeService
    private val walletService = Api.walletService

    private val _subscribeResponseMutableLiveData = MutableLiveData<SubscribeResponse>()
    val subscribeResponseLiveData:LiveData<SubscribeResponse>
    get() = _subscribeResponseMutableLiveData

    private val _subsLoadingMutableLiveData = MutableLiveData<SubsLoading>()
    val subsLoadingLiveData:LiveData<SubsLoading>
    get() = _subsLoadingMutableLiveData

    private val _qtyMutableLiveData = MutableLiveData<Int>()
    val qtyLiveData:LiveData<Int>
    get() = _qtyMutableLiveData

    private var _walletBalanceMutableLiveData = MutableLiveData<String>()
    val walletBalanceLiveData: LiveData<String>
        get() = _walletBalanceMutableLiveData

    private var _walletResponseMutableLiveData = MutableLiveData<WalletResponse>()
    val walletResponseLiveData: LiveData<WalletResponse>
        get() = _walletResponseMutableLiveData

    init {
        _qtyMutableLiveData.value = quantity
        _subsLoadingMutableLiveData.value = null
        _subscribeResponseMutableLiveData.value = null
        _walletBalanceMutableLiveData.value = null
        _walletResponseMutableLiveData.value = null
    }

    //Subscribe product
    fun subscribeProduct(subscribeProduct: SubscribeProduct){
        viewModelScope.launch {
            val responseDeferred = apiService.subscribeProductAsync(subscribeProduct)
            try {
                _subsLoadingMutableLiveData.value = SubsLoading.PENDING
                val response = responseDeferred.await()
                _subscribeResponseMutableLiveData.value = response
                _subsLoadingMutableLiveData.value = SubsLoading.SUCCESS
                Timber.e("Subscribe product response: ${response.status}")
            }catch (e:Exception){
                Timber.e("Failed to subscribe product: ${e.message}")
                _subscribeResponseMutableLiveData.value = null
                _subsLoadingMutableLiveData.value = SubsLoading.FAILED
            }
//            _subsLoadingMutableLiveData.value = SubsLoading.PENDING
//            apiService.subscribeProductAsync(subscribeProduct).enqueue(object :Callback<String>{
//                override fun onResponse(call: Call<String>, response: Response<String>) {
//                    if (response.isSuccessful){
//                        Timber.e("subscribe Response successful")
//                        response.body()?.let {
//                            _subsLoadingMutableLiveData.value = SubsLoading.SUCCESS
//                            Timber.e("subscribe Response is: $it")
//                        }?:let {
//                            Timber.e("Response body is null")
//                            _subsLoadingMutableLiveData.value = SubsLoading.FAILED
//                        }
//                    }else{
//                        _subsLoadingMutableLiveData.value = SubsLoading.FAILED
//                        Timber.e("Subscribe response unsuccessful:${response.errorBody()?.string()}")
//                    }
//                }
//
//                override fun onFailure(call: Call<String>, t: Throwable) {
//                    Timber.e("Failed to subscribe product: ${t.message}")
//                    _subsLoadingMutableLiveData.value = SubsLoading.FAILED
//                }
//            })
        }
    }

    //Get wallet balance
    fun getWalletBalance(id: String) {
        viewModelScope.launch {
            walletService.getWalletBalance(id).enqueue(object : Callback<String> {
                override fun onResponse(call: Call<String>, response: Response<String>) {
                    if (response.isSuccessful) {
                        response.body()?.let {
                            _walletBalanceMutableLiveData.value = it
                        }
                    }
                }

                override fun onFailure(call: Call<String>, t: Throwable) {
                    Timber.e("Failed to get wallet balance: ${t.message}")
                    _walletBalanceMutableLiveData.value = null
                }
            })
        }
    }

    //Debit/Credit to wallet
    fun cdWallet(id: String, walletRequest: WalletRequest) {
        viewModelScope.launch {
            val responseDeferred = walletService.cDWalletAsync(id, walletRequest)
            try {
                val response = responseDeferred.await()
                _walletResponseMutableLiveData.value = response
            } catch (e: Exception) {
                Timber.e("Failed to credit/debit to wallet: ${e.message}")
                _walletResponseMutableLiveData.value = null
            }
        }
    }

    fun resetWallet() {
        _walletResponseMutableLiveData.value = null
    }

    fun resetWalletBalance() {
        _walletBalanceMutableLiveData.value = null
    }

    //Add qty
    fun addQty(){
        quantity++
        _qtyMutableLiveData.value = quantity
    }

    //Remove qty
    fun removeQty(){
        if (quantity>1) {
            quantity--
            _qtyMutableLiveData.value = quantity
        }
    }

    fun resetQty(){
        quantity = 1
        _qtyMutableLiveData.value = quantity
    }
    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
    }
}
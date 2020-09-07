package com.a99Spicy.a99spicy.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.a99Spicy.a99spicy.network.Api
import com.a99Spicy.a99spicy.network.WalletRequest
import com.a99Spicy.a99spicy.network.WalletResponse
import com.a99Spicy.a99spicy.network.WalletTransaction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

enum class WalletLoading{
    SUCCESS, FAILED, PENDING
}

class WalletViewModel : ViewModel() {

    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)
    private val apiService = Api.walletService

    private var _walletTransactionsMutableLiveData = MutableLiveData<List<WalletTransaction>>()
    val walletTransactionLiveData: LiveData<List<WalletTransaction>>
        get() = _walletTransactionsMutableLiveData

    private var _walletBalanceMutableLiveData = MutableLiveData<String>()
    val walletBalanceLiveData: LiveData<String>
        get() = _walletBalanceMutableLiveData

    private var _walletResponseMutableLiveData = MutableLiveData<WalletResponse>()
    val walletResponseLiveData: LiveData<WalletResponse>
        get() = _walletResponseMutableLiveData

    private var _loadingMutableLiveData = MutableLiveData<WalletLoading>()
    val loadingLiveData:LiveData<WalletLoading>
    get() = _loadingMutableLiveData

    init {
        _loadingMutableLiveData.value = null
        _walletBalanceMutableLiveData.value = null
        _walletResponseMutableLiveData.value = null
        _walletTransactionsMutableLiveData.value = null
    }

    //Get wallet transactions
    fun getWalletTransactions(id: String) {
        uiScope.launch {
            val responseDeferred = apiService.getWalletTransactionsAsync(id)
            try {
                _loadingMutableLiveData.value = WalletLoading.PENDING
                val response = responseDeferred.await()
                _walletTransactionsMutableLiveData.value = response
                _loadingMutableLiveData.value = WalletLoading.SUCCESS
            } catch (e: Exception) {
                Timber.e("Failed to get wallet transaction: ${e.message}")
                _walletTransactionsMutableLiveData.value = null
                _loadingMutableLiveData.value = WalletLoading.FAILED
            }
        }
    }

    //Get wallet balance
    fun getWalletBalance(id: String) {
        uiScope.launch {
            apiService.getWalletBalance(id).enqueue(object : Callback<String> {
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
        uiScope.launch {
            val responseDeferred = apiService.cDWalletAsync(id, walletRequest)
            try {
                val response = responseDeferred.await()
                _walletResponseMutableLiveData.value = response
            } catch (e: Exception) {
                Timber.e("Failed to credit/debit to wallet: ${e.message}")
                _walletResponseMutableLiveData.value = null
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}
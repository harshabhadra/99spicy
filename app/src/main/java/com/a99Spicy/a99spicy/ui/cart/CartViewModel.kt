package com.a99Spicy.a99spicy.ui.cart

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.a99Spicy.a99spicy.MyApplication
import com.a99Spicy.a99spicy.Repository
import com.a99Spicy.a99spicy.database.DatabaseCart
import com.a99Spicy.a99spicy.database.DatabaseCoupon
import com.a99Spicy.a99spicy.database.MyDatabase
import com.a99Spicy.a99spicy.network.Api
import com.a99Spicy.a99spicy.network.Profile
import com.a99Spicy.a99spicy.network.WalletRequest
import com.a99Spicy.a99spicy.network.WalletResponse
import com.a99Spicy.a99spicy.ui.profile.Loading
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

enum class Loading {
    SUCCESS, FAILED, PENDING
}

class CartViewModel(application: MyApplication) : ViewModel() {

    private val viewModelJob = Job()

    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)
    private val apiService = Api.retrofitService
    private val walletService = Api.walletService

    private val database = MyDatabase.getDatabase(application)
    private val repository = Repository(database)

    val cartItemsLiveData = repository.cartItemsList

    private var _profileMutableLiveData = MutableLiveData<Profile>()
    val profileLiveData: LiveData<Profile>
        get() = _profileMutableLiveData

    private var _loadingMutableLiveData = MutableLiveData<Loading>()
    val loadingLiveData: LiveData<Loading>
        get() = _loadingMutableLiveData

    private var _walletBalanceMutableLiveData = MutableLiveData<String>()
    val walletBalanceLiveData: LiveData<String>
        get() = _walletBalanceMutableLiveData

    private var _walletResponseMutableLiveData = MutableLiveData<WalletResponse>()
    val walletResponseLiveData: LiveData<WalletResponse>
        get() = _walletResponseMutableLiveData

    private var _totalMutableLiveData = MutableLiveData<String>()
    val totalLiveData: LiveData<String>
        get() = _totalMutableLiveData

    init {
        _profileMutableLiveData.value = null
        _loadingMutableLiveData.value = null
        _walletBalanceMutableLiveData.value = null
        _walletResponseMutableLiveData.value = null
        _totalMutableLiveData.value = null
    }

    //Remove coupon
    fun removeCoupon(coupon: DatabaseCoupon) {
        uiScope.launch {
            repository.removeCoupon(coupon)
        }
    }

    //Clear cart
    fun clearCart() {
        uiScope.launch {
            repository.removeAllCart()
        }
    }

    //set total amount
    fun setTotal(total: String) {
        _totalMutableLiveData.value = total
    }

    //Get wallet balance
    fun getWalletBalance(id: String) {
        uiScope.launch {
            _loadingMutableLiveData.value = Loading.PENDING
            walletService.getWalletBalance(id).enqueue(object : Callback<String> {
                override fun onResponse(call: Call<String>, response: Response<String>) {
                    if (response.isSuccessful) {
                        response.body()?.let {
                            _walletBalanceMutableLiveData.value = it
                            _loadingMutableLiveData.value = Loading.PENDING
                        } ?: let {
                            _loadingMutableLiveData.value = Loading.FAILED
                        }
                    } else {
                        _loadingMutableLiveData.value = Loading.FAILED
                    }
                }

                override fun onFailure(call: Call<String>, t: Throwable) {
                    Timber.e("Failed to get wallet balance: ${t.message}")
                    _walletBalanceMutableLiveData.value = null
                    _loadingMutableLiveData.value = Loading.FAILED
                }
            })
        }
    }

    //Debit/Credit to wallet
    fun cdWallet(id: String, walletRequest: WalletRequest) {
        uiScope.launch {
            _loadingMutableLiveData.value = Loading.PENDING
            val responseDeferred = walletService.cDWalletAsync(id, walletRequest)
            try {
                val response = responseDeferred.await()
                _walletResponseMutableLiveData.value = response
                _loadingMutableLiveData.value = Loading.SUCCESS
            } catch (e: Exception) {
                _loadingMutableLiveData.value = Loading.FAILED
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

    //Add item to cart
    fun addItemToCart(databaseCart: DatabaseCart) {
        uiScope.launch {
            repository.addToCart(databaseCart)
        }
    }

    //Remove item from cart
    fun removeItemFromCart(databaseCart: DatabaseCart) {
        uiScope.launch {
            repository.deleteCart(databaseCart)
        }
    }

    //Update cart item
    fun updateCartItem(databaseCart: DatabaseCart) {
        uiScope.launch {
            repository.updateCartItem(databaseCart)
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}

class CartViewModelFactory(private val application: MyApplication) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CartViewModel::class.java)) {
            return CartViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}
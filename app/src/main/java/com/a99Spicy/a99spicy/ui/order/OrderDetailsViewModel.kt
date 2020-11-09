package com.a99Spicy.a99spicy.ui.order

import androidx.lifecycle.*
import com.a99Spicy.a99spicy.MyApplication
import com.a99Spicy.a99spicy.Repository
import com.a99Spicy.a99spicy.database.DatabaseCoupon
import com.a99Spicy.a99spicy.database.MyDatabase
import com.a99Spicy.a99spicy.network.Api
import com.a99Spicy.a99spicy.network.Coupon
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

enum class CouponLoading {
    SUCCESS, FAILED, PENDING
}

class OrderDetailsViewModel(private val application: MyApplication) : ViewModel() {

    private val apiService = Api.retrofitService
    private val retroService = Api.retroService
    private val database = MyDatabase.getDatabase(application)
    private val repository = Repository(database)

    private var _couponsMutableLiveData = MutableLiveData<List<Coupon>>()
    val couponLiveData: LiveData<List<Coupon>>
        get() = _couponsMutableLiveData

    private var _couponLoadingMutableLiveData = MutableLiveData<CouponLoading>()
    val couponLoadingLiveData: LiveData<CouponLoading>
        get() = _couponLoadingMutableLiveData

    val couponsList = repository.couponsList

    init {
        _couponLoadingMutableLiveData.value = null
        _couponsMutableLiveData.value = null
    }

    fun getAllCoupons() {
        _couponLoadingMutableLiveData.value = CouponLoading.PENDING
        retroService.getCoupons().enqueue(object : Callback<List<Coupon>> {
            override fun onResponse(call: Call<List<Coupon>>, response: Response<List<Coupon>>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        Timber.e("Coupons response: $it")
                        _couponLoadingMutableLiveData.value = CouponLoading.SUCCESS
                        _couponsMutableLiveData.value = it
                    } ?:let {
                        _couponLoadingMutableLiveData.value = CouponLoading.FAILED
                        Timber.e("Coupon response is null")}
                } else {
                    _couponLoadingMutableLiveData.value = CouponLoading.FAILED
                    Timber.e("coupons reponse failed: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<List<Coupon>>, t: Throwable) {
                Timber.e("Failed to get couponse: ${t.message}")
                _couponLoadingMutableLiveData.value = CouponLoading.FAILED
            }
        })
    }

    fun addCoupon(coupon: DatabaseCoupon) {
        viewModelScope.launch {
            repository.addCoupon(coupon)
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
    }
}

class OrderDetailsViewModelFactory(private val application: MyApplication) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OrderDetailsViewModel::class.java)) {
            return OrderDetailsViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}
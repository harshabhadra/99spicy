package com.a99Spicy.a99spicy.ui.payment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.a99Spicy.a99spicy.network.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

class PaymentViewModel : ViewModel() {
    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private val apiService = Api.razorPayService
    private val payTmService = ApiUtills.getApiServices()

    private var _razorPayMutableLiveData = MutableLiveData<RazorPaymentResponse>()
    val razorPayLiveData: LiveData<RazorPaymentResponse>
        get() = _razorPayMutableLiveData

    private var _payTimTxnTokenMutableLiveData = MutableLiveData<PaytmResponse>()
    val payTmTxnTokenLiveData: LiveData<PaytmResponse>
        get() = _payTimTxnTokenMutableLiveData

    init {
        _razorPayMutableLiveData.value = null
        _payTimTxnTokenMutableLiveData.value = null
    }

    //PayTm
    fun generateTxnToken(orderId: String, customerId: String, amount: Double) {
        uiScope.launch {
            uiScope.launch {

                payTmService?.generateTxnIdAsync(orderId, customerId, amount.toString())
                    ?.enqueue(object : Callback<PaytmResponse> {
                        override fun onFailure(call: Call<PaytmResponse>, t: Throwable) {
                            Timber.e("Failed to get txn token : ${t.message}")
                        }

                        override fun onResponse(
                            call: Call<PaytmResponse>,
                            response: Response<PaytmResponse>
                        ) {
                            if (response.isSuccessful) {
                                response.body()?.let {
                                    Timber.e("Txn token received successfully: ${it.body.txnToken}")
                                    _payTimTxnTokenMutableLiveData.value = response.body()
                                } ?: let {
                                    Timber.e("Empty response")
                                }
                            } else {
                                Timber.e(
                                    "Response unsuccessful: ${response.errorBody().toString()}"
                                )
                            }
                        }

                    })


            }
        }
    }

    //Generate order id for razor pay
    fun generateOrderId(razorPayPayment: RazorPayPayment) {
        uiScope.launch {
            apiService.generateOrderIdAsync(razorPayPayment)
                .enqueue(object : Callback<RazorPaymentResponse> {
                    override fun onFailure(call: Call<RazorPaymentResponse>, t: Throwable) {
                        Timber.e("Failed to generate order id: ${t.message}")
                    }

                    override fun onResponse(
                        call: Call<RazorPaymentResponse>,
                        response: Response<RazorPaymentResponse>
                    ) {
                        if (response.isSuccessful) {
                            response.body()?.let {
                                _razorPayMutableLiveData.value = it
                            } ?: let {
                                Timber.e("Empty response:")
                            }
                        } else {
                            Timber.e("Failed to get response: ${response.errorBody()}")
                        }
                    }
                })
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}
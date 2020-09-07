package com.a99Spicy.a99spicy.network

import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface Operators {

    @POST("Paytm/pay.php")
    @FormUrlEncoded
    fun generateTxnIdAsync(
        @Field("orderid") orderId: String,
        @Field("custid") customerId: String,
        @Field("txnamount") txnAmount: String
    ): Call<PaytmResponse>
}
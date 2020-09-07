package com.a99Spicy.a99spicy.network

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


data class RazorPayPayment(
    @SerializedName("amount")
    @Expose
    val amount:String,
    @SerializedName("currency")
    @Expose
    val currency:String = "INR",
    @SerializedName("payment_capture")
    @Expose
    val payment_capture:Int = 1
)

data class RazorPaymentResponse(
    @SerializedName("id")
    @Expose
    var id: String,
    @SerializedName("entity")
    @Expose
    var entity: String,
    @SerializedName("amount")
    @Expose
    var amount: Int,
    @SerializedName("amount_paid")
    @Expose
    var amountPaid: Int,
    @SerializedName("amount_due")
    @Expose
    var amountDue: Int,
    @SerializedName("currency")
    @Expose
    var currency: String,
    @SerializedName("status")
    @Expose
    var status: String
)
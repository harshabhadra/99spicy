package com.a99Spicy.a99spicy.network

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.squareup.moshi.Json

data class PaytmResponse(
    @SerializedName("body")
    @Expose
    var body: PaytmBody
)

data class PaytmBody(
    @SerializedName("resultInfo")
    @Expose
    var resultInfo: ResultInfo,
    @SerializedName( "txnToken")
    @Expose
    var txnToken: String,
    @SerializedName("isPromoCodeValid")
    @Expose
    var isPromoCodeValid: Boolean,
    @SerializedName("authenticated")
    @Expose
    var authenticated: Boolean
)

data class ResultInfo(
    @SerializedName("resultStatus")
    @Expose
    var resultStatus: String,
    @SerializedName("resultCode")
    @Expose
    var resultCode: String,
    @SerializedName("resultMsg")
    @Expose
    var resultMsg: String
)

data class PaytmHead(
    @Json(name = "responseTimestamp")
    var responseTimestamp: String,
    @Json(name = "version")
    var version: String,
    @Json(name = "clientId")
    var clientId: String,
    @Json(name = "signature")
    var signature: String
)
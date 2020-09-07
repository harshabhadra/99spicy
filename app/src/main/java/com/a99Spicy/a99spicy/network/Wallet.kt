package com.a99Spicy.a99spicy.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

data class WalletTransaction(
    @Json(name = "transaction_id")
    var transactionId: String,
    @Json(name = "blog_id")
    var blogId: String,
    @Json(name = "user_id")
    var userId: String,
    @Json(name = "type")
    var type: String,
    @Json(name = "amount")
    var amount: String,
    @Json(name = "balance")
    var balance: String,
    @Json(name = "currency")
    var currency: String,
    @Json(name = "details")
    var details: String,
    @Json(name = "created_by")
    var createdBy: String,
    @Json(name = "deleted")
    var deleted: String,
    @Json(name = "date")
    var date: String
)

data class WalletRequest(
    @JsonClass(generateAdapter = true)
    val type:String,
    @JsonClass(generateAdapter = true)
    val amount:Double,
    @JsonClass(generateAdapter = true)
    val details:String
)

data class WalletResponse(
    @Json(name = "response")
    val response:String,
    @Json(name = "id")
    val id:Int
)
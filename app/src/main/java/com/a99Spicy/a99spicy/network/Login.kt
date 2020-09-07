package com.a99Spicy.a99spicy.network

import com.squareup.moshi.JsonClass

data class LoginRequest(
    @JsonClass(generateAdapter = true)
    val customerKey:String,
    @JsonClass(generateAdapter = true)
    val phone:String,
    @JsonClass(generateAdapter = true)
    val email:String,
    @JsonClass(generateAdapter = true)
    val authType:String = "SMS",
    @JsonClass(generateAdapter = true)
    val transactionName:String = "CUSTOM-OTP-VERIFICATION"
)

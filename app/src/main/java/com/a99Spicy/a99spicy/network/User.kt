package com.a99Spicy.a99spicy.network

import com.squareup.moshi.Json

data class SignUpResponse(
    @Json(name = "success")
    var success: Boolean,
    @Json(name = "data")
    var data: SignUpData
)

data class SignUpData(
    @Json(name = "user_id")
    var userId: String,
    @Json(name = "access_token")
    var accessToken: String,
    @Json(name = "token_type")
    var tokenType: String
)

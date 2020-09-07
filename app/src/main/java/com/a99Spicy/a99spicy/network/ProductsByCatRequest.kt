package com.a99Spicy.a99spicy.network

import com.squareup.moshi.JsonClass

data class ProductsByCatRequest(
    @JsonClass(generateAdapter = true)
    val page:Int,
    @JsonClass(generateAdapter = true)
    val per_page:Int,
    @JsonClass(generateAdapter = true)
    val category:String
)
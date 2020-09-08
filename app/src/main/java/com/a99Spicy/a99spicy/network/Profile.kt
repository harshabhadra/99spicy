package com.a99Spicy.a99spicy.network

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Profile(
    @Json(name = "id")
    var id: Int,
    @Json(name = "email")
    var email: String,
    @Json(name = "first_name")
    var firstName: String,
    @Json(name = "last_name")
    var lastName: String,
    @Json(name = "role")
    var role: String,
    @Json(name = "username")
    var username: String,
    @Json(name = "billing")
    var billing: Billing,
    @Json(name = "shipping")
    var shipping: Shipping,
    @Json(name = "is_paying_customer")
    var isPayingCustomer: Boolean,
    @Json(name = "avatar_url")
    var avatarUrl: String
): Parcelable

@Parcelize
data class Billing (
    @Json(name = "first_name")
    var firstName: String,
    @Json(name = "last_name")
    var lastName: String,
    @Json(name = "company")
    var company: String,
    @Json(name = "address_1")
    var address1: String,
    @Json(name = "address_2")
    var address2: String,
    @Json(name = "city")
    var city: String,
    @Json(name = "postcode")
    var postcode: String,
    @Json(name = "country")
    var country: String,
    @Json(name = "state")
    var state: String,
    @Json(name = "email")
    var email: String,
    @Json(name = "phone")
    var phone: String
): Parcelable

@Parcelize
data class Shipping(
    @Json(name = "first_name")
    var firstName: String,
    @Json(name = "last_name")
    var lastName: String,
    @Json(name = "company")
    var company: String,
    @Json(name = "address_1")
    var address1: String?="",
    @Json(name = "address_2")
    var address2: String,
    @Json(name = "city")
    var city: String,
    @Json(name = "postcode")
    var postcode: String,
    @Json(name = "country")
    var country: String,
    @Json(name = "state")
    var state: String
): Parcelable

data class Address(
    @JsonClass(generateAdapter = true)
    val shipping:ShippingDetail
)

@Parcelize
data class ShippingDetail(
    @JsonClass(generateAdapter = true)
    var first_name: String,
    @JsonClass(generateAdapter = true)
    var last_name: String,
    @JsonClass(generateAdapter = true)
    var company: String,
    @JsonClass(generateAdapter = true)
    var address_1: String,
    @JsonClass(generateAdapter = true)
    var address_2: String,
    @JsonClass(generateAdapter = true)
    var city: String,
    @JsonClass(generateAdapter = true)
    var postcode: String,
    @JsonClass(generateAdapter = true)
    var country: String,
    @JsonClass(generateAdapter = true)
    var state: String
):Parcelable

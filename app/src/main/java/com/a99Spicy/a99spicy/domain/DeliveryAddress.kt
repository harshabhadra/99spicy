package com.a99Spicy.a99spicy.domain

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DeliveryAddress(
    var id:Int,
    var firstName: String,
    var lastName: String,
    var company: String,
    var address1: String,
    var address2: String,
    var city: String,
    var postcode: String,
    var country: String,
    var state: String
):Parcelable
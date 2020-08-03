package com.a99Spicy.a99spicy.domain

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DomainProducts(
    val productList:List<DomainProduct>
):Parcelable

@Parcelize
data class DomainProduct(
    val productId:String,
    val productName:String,
    val productQuantity:String,
    val productPrice:String,
    val productDiscount:String,
    val productImg:Int
):Parcelable
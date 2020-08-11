package com.a99Spicy.a99spicy.domain

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DomainDummyProducts(
    val dummyProductList:List<DomainDummyProduct>
):Parcelable

@Parcelize
data class DomainDummyProduct(
    val productId:String,
    val productName:String,
    val productQuantity:String,
    val productPrice:String,
    val productDiscount:String,
    val productImg:Int
):Parcelable
package com.a99Spicy.a99spicy.domain

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DomainCategoryItem(
    val catImage:Int,
    val catName:String,
    val dummyProducts:DomainDummyProducts? = null
):Parcelable
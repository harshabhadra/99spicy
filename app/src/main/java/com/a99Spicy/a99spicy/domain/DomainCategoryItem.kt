package com.a99Spicy.a99spicy.domain

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DomainCategoryItems(
    val categoryList:List<DomainCategoryItem>
):Parcelable

@Parcelize
data class DomainCategoryItem(
    val catId:Int,
    val parentId:Int,
    val catImage:String?="",
    val catName:String
):Parcelable
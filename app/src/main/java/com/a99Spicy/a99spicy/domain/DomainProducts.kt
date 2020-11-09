package com.a99Spicy.a99spicy.domain

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DomainProducts(
    val productList:List<DomainProduct>
):Parcelable

@Parcelize
data class DomainProduct(
    var id: Int,
    var name: String,
    var slug: String,
    var dateCreated: String,
    var dateModified: String,
    var status: String,
    var featured: Boolean,
    var description: String? = "",
    var shortDescription: String? = "",
    var sku: String? = "",
    var price: String? = "",
    var regularPrice: String? = "",
    var salePrice: String? = "",
    var onSale: Boolean,
    var purchasable: Boolean,
    var totalSales: Int,
    var taxStatus: String? = "",
    var taxClass: String? = "",
    var stockQuantity: String? = "",
    var stockStatus: String? = "",
    var weight: String? = "",
    var reviewsAllowed: Boolean,
    var averageRating: String? = "",
    var ratingCount: Int,
    var relatedIds: List<Int>? = null,
    var purchaseNote: String? = "",
    var categories: List<DomainCategory>,
    var images: List<DomainImage>,
    var metaData: List<DomainMetaDatam>,
    var index:Int?= -1
):Parcelable

@Parcelize
data class DomainCategory(
    var id: Int,
    var name: String,
    var slug: String
) : Parcelable

@Parcelize
data class DomainImage(
    var id: Int,
    var src: String,
    var name: String
) : Parcelable

@Parcelize
data class DomainMetaDatam(
    var id: Int,
    var key: String,
    var value: String
) : Parcelable
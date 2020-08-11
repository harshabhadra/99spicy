package com.a99Spicy.a99spicy.domain

data class DomainSortedProducts(
    val sortedProductList:List<DomainSortedCategory>?
)

data class DomainSortedCategory(
    val catName:String,
    val catimg:String?,
    val subCatList:List<String>?
)

data class DomainSubCat(
    val subCatName:String

)

data class DomainSortedProduct(
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
    var productImg:String?="",
    var metaData: List<DomainMetaDatam>
)
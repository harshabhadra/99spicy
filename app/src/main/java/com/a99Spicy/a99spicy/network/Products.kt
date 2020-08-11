package com.a99Spicy.a99spicy.network

import android.os.Parcelable
import com.a99Spicy.a99spicy.database.DataBasImage
import com.a99Spicy.a99spicy.database.DataBaseCategory
import com.a99Spicy.a99spicy.database.DataBaseMetaDatam
import com.a99Spicy.a99spicy.database.DatabaseProduct
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Products(
    val productList:List<Product>
):Parcelable

@Parcelize
data class Product(
    @Json(name = "id")
    var id: Int,
    @Json(name = "name")
    var name: String,
    @Json(name = "slug")
    var slug: String,
    @Json(name = "date_created")
    var dateCreated: String,
    @Json(name = "date_modified")
    var dateModified: String,
    @Json(name = "status")
    var status: String,
    @Json(name = "featured")
    var featured: Boolean,
    @Json(name = "description")
    var description: String? ="",
    @Json(name = "short_description")
    var shortDescription: String? ="",
    @Json(name = "sku")
    var sku: String? = "",
    @Json(name = "price")
    var price: String? = "",
    @Json(name = "regular_price")
    var regularPrice: String? ="",
    @Json(name = "sale_price")
    var salePrice: String? = "",
    @Json(name = "on_sale")
    var onSale: Boolean,
    @Json(name = "purchasable")
    var purchasable: Boolean,
    @Json(name = "total_sales")
    var totalSales: Int,
    @Json(name = "tax_status")
    var taxStatus: String? ="",
    @Json(name = "tax_class")
    var taxClass: String? = "",
    @Json(name = "stock_quantity")
    var stockQuantity: String? = "",
    @Json(name = "stock_status")
    var stockStatus: String? = "",
    @Json(name = "weight")
    var weight: String? = "",
    @Json(name = "reviews_allowed")
    var reviewsAllowed: Boolean,
    @Json(name = "average_rating")
    var averageRating: String? = "",
    @Json(name = "rating_count")
    var ratingCount: Int,
    @Json(name = "related_ids")
    var relatedIds: List<Int>? = null,
    @Json(name = "purchase_note")
    var purchaseNote: String? ="",
    @Json(name = "categories")
    var categories: List<Category>,
    @Json(name = "images")
    var images: List<Image>,
    @Json(name = "meta_data")
    var metaData: List<MetaDatam>
):Parcelable

@Parcelize
data class Category(
    @Json(name = "id")
    var id: Int,
    @Json(name = "name")
    var name: String,
    @Json(name = "slug")
    var slug: String
):Parcelable

@Parcelize
data class Image (
    @Json(name = "id")
    var id: Int,
    @Json(name = "src")
    var src: String,
    @Json(name = "name")
    var name: String
):Parcelable

@Parcelize
data class MetaDatam(
    @Json(name = "id")
    var id: Int,
    @Json(name = "key")
    var key: String,
    @Json(name = "value")
    var value: String
):Parcelable

//Extension function to convert List of dummyProducts to List of DataBaseProducts
fun List<Product>.asDataBaseProducts():Array<DatabaseProduct>{

    return map {
        DatabaseProduct(
            id = it.id,
            name = it.name,
            slug = it.slug,
            dateCreated = it.dateCreated,
            dateModified = it.dateModified,
            status = it.status,
            featured = it.featured,
            description = it.description,
            shortDescription = it.shortDescription,
            sku = it.sku,
            price = it.price,
            regularPrice = it.regularPrice,
            salePrice = it.salePrice,
            onSale = it.onSale,
            purchasable = it.purchasable,
            totalSales = it.totalSales,
            taxStatus = it.taxStatus,
            taxClass = it.taxClass,
            stockQuantity = it.stockQuantity,
            stockStatus = it.stockStatus,
            weight = it.weight,
            reviewsAllowed = it.reviewsAllowed,
            averageRating = it.averageRating,
            ratingCount = it.ratingCount,
            relatedIds = it.relatedIds,
            purchaseNote = it.purchaseNote,
            categories = it.categories.asDataBaseCategories(),
            images = it.images.asDataBaseImage(),
            metaData = it.metaData.asDataBaseMetaDatam()
        )
    }.toTypedArray()
}

fun List<Category>.asDataBaseCategories():List<DataBaseCategory>{
    return map {
        DataBaseCategory(
            id = it.id,
            name = it.name,
            slug = it.slug
        )
    }
}

fun List<Image>.asDataBaseImage():List<DataBasImage>{
    return map {
        DataBasImage(
            id = it.id,
            src = it.src,
            name = it.name
        )
    }
}

fun List<MetaDatam>.asDataBaseMetaDatam():List<DataBaseMetaDatam>{
    return map {
        DataBaseMetaDatam(
            id = it.id,
            key = it.key,
            value = it.value
        )
    }
}


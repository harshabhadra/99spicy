package com.a99Spicy.a99spicy.database


import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.a99Spicy.a99spicy.domain.DomainCategory
import com.a99Spicy.a99spicy.domain.DomainImage
import com.a99Spicy.a99spicy.domain.DomainMetaDatam
import com.a99Spicy.a99spicy.domain.DomainProduct
import kotlinx.android.parcel.Parcelize
import java.sql.DatabaseMetaData

@Parcelize
data class DatabaseProducts(
    val productList: List<DatabaseProduct>
) : Parcelable

@Entity(tableName = "products_table")
@Parcelize
data class DatabaseProduct(
    @PrimaryKey
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
    var categories: List<DataBaseCategory>,
    var images: List<DataBasImage>,
    var metaData: List<DataBaseMetaDatam>
) : Parcelable

@Parcelize
data class DataBaseCategory(
    var id: Int,
    var name: String,
    var slug: String
) : Parcelable

@Parcelize
data class DataBasImage(
    var id: Int,
    var src: String,
    var name: String
) : Parcelable

@Parcelize
data class DataBaseMetaDatam(
    var id: Int,
    var key: String,
    var value: String
) : Parcelable

//Extension functions to convert database product to domain products
fun List<DatabaseProduct>.asDomainProductList():List<DomainProduct>{

    return map {
        DomainProduct(
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
            categories = it.categories.asDomainCategoryList(),
            images = it.images.asDomainImageList(),
            metaData = it.metaData.asDomainMetaDataList()
        )
    }
}

fun List<DataBaseCategory>.asDomainCategoryList():List<DomainCategory>{

    return map {
        DomainCategory(
            id = it.id,
            name = it.name,
            slug = it.slug
        )
    }
}

fun List<DataBasImage>.asDomainImageList():List<DomainImage>{
    return map {
        DomainImage(
            id = it.id,
            src = it.src,
            name = it.name
        )
    }
}

fun List<DataBaseMetaDatam>.asDomainMetaDataList():List<DomainMetaDatam>{
    return map {
        DomainMetaDatam(
            id = it.id,
            key = it.key,
            value = it.value
        )
    }
}
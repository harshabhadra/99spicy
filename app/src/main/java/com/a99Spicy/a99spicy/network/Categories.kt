package com.a99Spicy.a99spicy.network

import androidx.room.ColumnInfo
import com.a99Spicy.a99spicy.database.DatabaseProductCategory
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

data class CategoryRequest(
    @JsonClass(generateAdapter = true)
    val per_page:Int
)

data class Categories(
    val categoryList:List<ProductCategory>
)

data class ProductCategory(
    @Json(name = "id")
    var id: Int,
    @Json(name = "name")
    var name: String,
    @Json(name = "slug")
    var slug: String,
    @Json(name = "parent")
    var parent: Int,
    @Json(name = "description")
    var description: String? = "",
    @Json(name = "image")
    var image: CatImg? = null
)

data class CatImg(
    @Json(name = "id")
    @ColumnInfo(name = "pcat_image_id")
    val imgId: Int,
    @Json(name = "src")
    @ColumnInfo(name="pcat_image_src")
    val src: String
)

//Extension function to convert Categories to Database categories
fun List<ProductCategory>.asDataBaseProductCategoryList():Array<DatabaseProductCategory>{

    return map {
        DatabaseProductCategory(
            id = it.id,
            name = it.name,
            slug = it.slug,
            parent = it.parent,
            description = it.description,
            image = it.image
        )
    }.toTypedArray()
}
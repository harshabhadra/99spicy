package com.a99Spicy.a99spicy.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.a99Spicy.a99spicy.domain.DomainCategoryItem
import com.a99Spicy.a99spicy.network.CatImg

data class DatabaseCategories(
    val categoryList:List<DatabaseProductCategory>
)

@Entity(tableName = "category_table")
data class DatabaseProductCategory(
    @PrimaryKey
    @ColumnInfo(name="pcat_id")
    var id: Int,
    @ColumnInfo(name="pcat_name")
    var name: String,
    @ColumnInfo(name="pcat_slug")
    var slug: String,
    @ColumnInfo(name="pcat_parent")
    var parent: Int,
    @ColumnInfo(name="pcat_description")
    var description: String? = "",
    @ColumnInfo(name="pcat_image")
    var image: CatImg? = null
)

fun List<DatabaseProductCategory>.asDomainProductCategories():List<DomainCategoryItem>{
    return map {
        DomainCategoryItem(
            catId = it.id,
            parentId = it.parent,
            catImage = it.image?.src,
            catName = it.name
        )
    }
}
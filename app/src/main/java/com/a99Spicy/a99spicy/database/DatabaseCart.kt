package com.a99Spicy.a99spicy.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.a99Spicy.a99spicy.network.LineItem
import kotlin.math.roundToInt

@Entity(tableName = "cart_table")
data class DatabaseCart(
    @PrimaryKey
    val productId:Int,
    val name:String,
    val regularPrice:String?="",
    val salePrice:String?="",
    val image:String,
    val quantity:Int,
    val catId:Int,
    val subCatId:Int
)

fun List<DatabaseCart>.asLineItems():List<LineItem>{
    return map {
        LineItem(
            productId = it.productId,
            productName = it.name,
            quantity = it.quantity,
            total = it.salePrice!!.toDouble().roundToInt().times(it.quantity).toString()
        )
    }
}
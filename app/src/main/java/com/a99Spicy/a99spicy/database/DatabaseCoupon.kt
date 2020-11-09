package com.a99Spicy.a99spicy.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json

@Entity(tableName = "coupon_table")
data class DatabaseCoupon(
    @PrimaryKey
    var id: Int,
    var code: String,
    var amount: String,
    var dateCreated: String,
    var dateCreatedGmt: String,
    var dateModified: String,
    var dateModifiedGmt: String,
    var discountType: String,
    var description: String,
    var dateExpires: String,
    var dateExpiresGmt: String,
    var usageCount: Int,
    var individualUse: Boolean,
    var minimumAmount: String,
    var maximumAmount: String,
    var product_categories:List<Int>? = null
)
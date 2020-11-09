package com.a99Spicy.a99spicy.network

import com.a99Spicy.a99spicy.database.DatabaseCoupon
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Coupon(
    @SerializedName("id")
    @Expose
    var id: Int,
    @SerializedName("code")
    @Expose
    var code: String,
    @SerializedName("amount")
    @Expose
    var amount: String,
    @SerializedName("date_created")
    @Expose
    var dateCreated: String,
    @SerializedName("date_created_gmt")
    @Expose
    var dateCreatedGmt: String,
    @SerializedName("date_modified")
    @Expose
    var dateModified: String,
    @SerializedName("date_modified_gmt")
    @Expose
    var dateModifiedGmt: String,
    @SerializedName("discount_type")
    @Expose
    var discountType: String,
    @SerializedName("description")
    @Expose
    var description: String,
    @SerializedName("date_expires")
    @Expose
    var dateExpires: String,
    @SerializedName("date_expires_gmt")
    @Expose
    var dateExpiresGmt: String,
    @SerializedName("usage_count")
    @Expose
    var usageCount: Int,
    @SerializedName("individual_use")
    @Expose
    var individualUse: Boolean,
    @SerializedName("minimum_amount")
    @Expose
    var minimumAmount: String,
    @SerializedName("maximum_amount")
    @Expose
    var maximumAmount: String,
    @SerializedName("used_by")
    @Expose
    var used_by: List<String>? = null,
    @SerializedName("product_categories")
    @Expose
    var product_categories: List<Int>? = null
)

fun List<Coupon>.asDatabaseCoupons(): List<DatabaseCoupon> {
    return map {
        DatabaseCoupon(
            id = it.id,
            code = it.code,
            amount = it.amount,
            dateCreated = it.dateCreated,
            dateCreatedGmt = it.dateCreatedGmt,
            dateModified = it.dateModified,
            dateModifiedGmt = it.dateModifiedGmt,
            discountType = it.discountType,
            description = it.description,
            dateExpires = it.dateExpires,
            dateExpiresGmt = it.dateExpiresGmt,
            usageCount = it.usageCount,
            individualUse = it.individualUse,
            minimumAmount = it.minimumAmount,
            maximumAmount = it.maximumAmount,
            product_categories = it.product_categories
        )
    }.toList()
}

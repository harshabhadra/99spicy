package com.a99Spicy.a99spicy.network

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PlaceOrder(
    @Json(name = "currency")
    var currency: String,
    @Json(name = "customer_id")
    var customerId: Int,
    @Json(name ="billing")
    var billing: Billing,
    @Json(name = "payment_method")
    var paymentMethod: String,
    @Json(name = "payment_method_title")
    var paymentMethodTitle: String,
    @Json(name = "transaction_id")
    var transactionId: String,
    @Json(name = "line_items")
    var lineItems: List<LineItem>,
    @Json(name = "set_paid")
    var setPaid: Boolean,
    @Json(name = "customer_note")
    var customer_note: String,
    @Json(name = "coupon_lines")
    var coupon_lines:List<CouponLine>?
) : Parcelable

@Parcelize
data class LineItem(
    @Json(name = "product_id")
    var productId: Int,
    @Json(name = "name")
    var productName: String,
    @Json(name = "quantity")
    var quantity: Int? = 1,
    @Json(name = "total")
    var total:String
) : Parcelable

@Parcelize
data class CouponLine(
    @Json(name = "code")
    var code: String
) : Parcelable

@Parcelize
data class OrderResponse(
    @Json(name = "id")
    var id: Int,
    @Json(name = "parent_id")
    var parentId: Int,
    @Json(name = "number")
    var number: String,
    @Json(name = "order_key")
    var orderKey: String,
    @Json(name = "status")
    var status: String,
    @Json(name = "currency")
    var currency: String,
    @Json(name = "date_created")
    var dateCreated: String,
    @Json(name = "date_created_gmt")
    var dateCreatedGmt: String,
    @Json(name = "date_modified")
    var dateModified: String,
    @Json(name = "date_modified_gmt")
    var dateModifiedGmt: String,
    @Json(name = "discount_total")
    var discountTotal: String,
    @Json(name = "discount_tax")
    var discountTax: String,
    @Json(name = "shipping_total")
    var shippingTotal: String,
    @Json(name = "shipping_tax")
    var shippingTax: String,
    @Json(name = "cart_tax")
    var cartTax: String,
    @Json(name = "total")
    var total: String,
    @Json(name = "total_tax")
    var totalTax: String,
    @Json(name = "prices_include_tax")
    var pricesIncludeTax: Boolean,
    @Json(name = "customer_id")
    var customerId: Int,
    @Json(name = "customer_note")
    var customerNote: String,
    @Json(name ="billing")
    var billing: Billing,
    @Json(name = "payment_method")
    var paymentMethod: String,
    @Json(name = "payment_method_title")
    var paymentMethodTitle: String,
    @Json(name = "transaction_id")
    var transactionId: String,
    @Json(name = "date_paid")
    var datePaid: String? = "",
    @Json(name = "date_paid_gmt")
    var datePaidGmt: String? = "",
    @Json(name = "date_completed")
    var dateCompleted: String? = "",
    @Json(name = "date_completed_gmt")
    var dateCompletedGmt: String? = "",
    @Json(name = "line_items")
    var lineItems: List<ResponseLineItem>,
    @Json(name = "coupon_lines")
    var coupon_lines:List<CouponResponse>?
) : Parcelable

@Parcelize
data class ResponseLineItem(
    @Json(name = "id")
    var id: Int,
    @Json(name = "name")
    var name: String,
    @Json(name = "product_id")
    var productId: Int,
    @Json(name = "variation_id")
    var variationId: Int,
    @Json(name = "quantity")
    var quantity: Int,
    @Json(name = "tax_class")
    var taxClass: String,
    @Json(name = "subtotal")
    var subtotal: String,
    @Json(name = "subtotal_tax")
    var subtotalTax: String,
    @Json(name = "total")
    var total: String,
    @Json(name = "total_tax")
    var totalTax: String,
    @Json(name = "sku")
    var sku: String,
    @Json(name = "price")
    var price: Int
) : Parcelable

@Parcelize
data class CouponResponse(
    @Json(name = "id")
    var id: Int,
    @Json(name = "code")
    var code: String,
    @Json(name = "discount")
    var discount: String,
    @Json(name = "discount_tax")
    var discount_tax: String,
    @Json(name = "meta_data")
    var couponMetaList: List<MetaDatam>?
) : Parcelable
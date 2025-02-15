package com.a99Spicy.a99spicy.network

import com.squareup.moshi.Json

data class Subscription(
    @Json(name = "id")
    var id: Int,
    @Json(name = "parent_id")
    var parentId: Int,
    @Json(name = "status")
    var status: String,
    @Json(name = "order_key")
    var orderKey: String,
    @Json(name = "number")
    var number: String,
    @Json(name = "currency")
    var currency: String,
    @Json(name = "version")
    var version: String,
    @Json(name = "prices_include_tax")
    var pricesIncludeTax: Boolean,
    @Json(name = "date_created")
    var dateCreated: String,
    @Json(name = "date_modified")
    var dateModified: String,
    @Json(name = "customer_id")
    var customerId: Int,
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
    @Json(name = "billing")
    var billing: Billing,
    @Json(name = "shipping")
    var shipping: Shipping,
    @Json(name = "payment_method")
    var paymentMethod: String,
    @Json(name = "payment_method_title")
    var paymentMethodTitle: String,
    @Json(name = "transaction_id")
    var transactionId: String,
    @Json(name = "customer_note")
    var customerNote: String,
    @Json(name = "date_completed")
    var dateCompleted: String?,
    @Json(name = "date_paid")
    var datePaid: String?,
    @Json(name = "cart_hash")
    var cartHash: String,
    @Json(name = "line_items")
    var lineItems: List<SubscriptionsLineItem>,
    @Json(name = "billing_period")
    var billingPeriod: String?,
    @Json(name = "billing_interval")
    var billingInterval: String,
    @Json(name = "start_date")
    var startDate: String,
    @Json(name = "next_payment_date")
    var nextPaymentDate: String,
    @Json(name = "end_date")
    var endDate: String
)

data class SubscriptionsLineItem(
    @Json(name = "id")
    var id: Int,
    @Json(name = "name")
    var name: String,
    @Json(name = "sku")
    var sku: String,
    @Json(name = "product_id")
    var productId: Int,
    @Json(name = "variation_id")
    var variationId: Int,
    @Json(name = "quantity")
    var quantity: Int,
    @Json(name = "tax_class")
    var taxClass: String,
    @Json(name = "price")
    var price: String,
    @Json(name = "subtotal")
    var subtotal: String,
    @Json(name = "subtotal_tax")
    var subtotalTax: String,
    @Json(name = "total")
    var total: String,
    @Json(name = "total_tax")
    var totalTax: String
)
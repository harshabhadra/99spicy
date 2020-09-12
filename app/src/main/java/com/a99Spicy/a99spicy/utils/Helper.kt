package com.a99Spicy.a99spicy.utils

import com.a99Spicy.a99spicy.database.DataBaseCategory
import com.a99Spicy.a99spicy.network.LineItem
import com.a99Spicy.a99spicy.network.Shipping
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Helper {

    fun fromShippingToString(shipping:Shipping):String{
        val gson = Gson()
        return gson.toJson(shipping)
    }

    fun fromStringToShipping(string: String):Shipping{
        val gson = Gson()
        return gson.fromJson(string, Shipping::class.java)
    }

    fun fromLineItemsToString(lineItems: List<LineItem>):String{
        val gson = Gson()
        return gson.toJson(lineItems)
    }

    fun fromStringToLineItems(string: String):List<LineItem>{
        val gson = Gson()
        val type = object : TypeToken<List<LineItem>?>() {}.type
        return gson.fromJson(string, type)
    }
}
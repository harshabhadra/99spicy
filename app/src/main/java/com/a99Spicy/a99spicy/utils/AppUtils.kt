package com.a99Spicy.a99spicy.utils

import android.content.Context
import com.a99Spicy.a99spicy.R
import com.a99Spicy.a99spicy.domain.DomainBannerItem
import com.a99Spicy.a99spicy.domain.DomainCategoryItem
import com.a99Spicy.a99spicy.domain.DomainProduct
import com.a99Spicy.a99spicy.domain.DomainProducts

class AppUtils {

    companion object {

        //Dummy banner list
        fun getBannerList(): List<DomainBannerItem> {
            val bannerList: MutableList<DomainBannerItem> = mutableListOf()
            bannerList.add(DomainBannerItem("", R.drawable.banner_a))
            bannerList.add(DomainBannerItem("", R.drawable.banner_b))
            return bannerList
        }

        //Dummy category list
        fun getCategoryList(context: Context): List<DomainCategoryItem> {

            val productList: MutableList<DomainProduct> = mutableListOf()
            productList.add(
                DomainProduct(
                    "0",
                    context.getString(R.string.dawat_biriyani_basmati_rice),
                    "5Kg",
                    "99.00",
                    "50",
                    R.drawable.grocery_place_holder
                )
            )
            productList.add(
                DomainProduct(
                    "0",
                    context.getString(R.string.dawat_biriyani_basmati_rice),
                    "5Kg",
                    "99.00",
                    "50",
                    R.drawable.grocery_place_holder
                )
            )
            productList.add(
                DomainProduct(
                    "0",
                    context.getString(R.string.dawat_biriyani_basmati_rice),
                    "5Kg",
                    "99.00",
                    "50",
                    R.drawable.grocery_place_holder
                )
            )
            productList.add(
                DomainProduct(
                    "0",
                    context.getString(R.string.dawat_biriyani_basmati_rice),
                    "5Kg",
                    "99.00",
                    "50",
                    R.drawable.grocery_place_holder
                )
            )
            productList.add(
                DomainProduct(
                    "0",
                    context.getString(R.string.dawat_biriyani_basmati_rice),
                    "5Kg",
                    "99.00",
                    "50",
                    R.drawable.grocery_place_holder
                )
            )
            productList.add(
                DomainProduct(
                    "0",
                    context.getString(R.string.dawat_biriyani_basmati_rice),
                    "5Kg",
                    "99.00",
                    "50",
                    R.drawable.grocery_place_holder
                )
            )
            productList.add(
                DomainProduct(
                    "0",
                    context.getString(R.string.dawat_biriyani_basmati_rice),
                    "5Kg",
                    "99.00",
                    "50",
                    R.drawable.grocery_place_holder
                )
            )

            val catList: MutableList<DomainCategoryItem> = mutableListOf()
            catList.add(
                DomainCategoryItem(
                    R.drawable.grocery_place_holder,
                    "Grocery",
                    DomainProducts(productList.toList())
                )
            )
            catList.add(
                DomainCategoryItem(
                    R.drawable.milk_placeholder,
                    "Milk",
                    DomainProducts(productList.toList())
                )
            )
            catList.add(
                DomainCategoryItem(
                    R.drawable.grocery_place_holder,
                    "Vegetables",
                    DomainProducts(productList.toList())
                )
            )
            catList.add(
                DomainCategoryItem(
                    R.drawable.grocery_place_holder,
                    "Fruits",
                    DomainProducts(productList.toList())
                )
            )
            catList.add(
                DomainCategoryItem(
                    R.drawable.grocery_place_holder,
                    "Dairy",
                    DomainProducts(productList.toList())
                )
            )
            catList.add(
                DomainCategoryItem(
                    R.drawable.grocery_place_holder,
                    "Bread",
                    DomainProducts(productList.toList())
                )
            )
            catList.add(
                DomainCategoryItem(
                    R.drawable.grocery_place_holder,
                    "Branded Foods",
                    DomainProducts(productList.toList())
                )
            )
            catList.add(
                DomainCategoryItem(
                    R.drawable.grocery_place_holder,
                    "Breakfast",
                    DomainProducts(productList.toList())
                )
            )
            catList.add(
                DomainCategoryItem(
                    R.drawable.grocery_place_holder,
                    "Meat",
                    DomainProducts(productList.toList())
                )
            )
            catList.add(
                DomainCategoryItem(
                    R.drawable.grocery_place_holder,
                    "Chicken",
                    DomainProducts(productList.toList())
                )
            )

            return catList
        }

        //dummy Profile items list
        fun getProfileItemsList(context: Context):List<String>{
            val list:MutableList<String> = mutableListOf()
            list.add(context.getString(R.string.wallet))
            list.add(context.getString(R.string.delivery_add))
            list.add(context.getString(R.string.orders))
            list.add(context.getString(R.string.settings))
            list.add(context.getString(R.string.shareandearn))
            list.add(context.getString(R.string.rate_us))

            return list
        }
    }
}
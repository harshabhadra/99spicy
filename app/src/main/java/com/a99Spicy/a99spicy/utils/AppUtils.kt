package com.a99Spicy.a99spicy.utils

import android.content.Context
import com.a99Spicy.a99spicy.R
import com.a99Spicy.a99spicy.domain.DomainBannerItem
import com.a99Spicy.a99spicy.domain.DomainCategoryItem
import com.a99Spicy.a99spicy.domain.DomainDummyProduct
import com.a99Spicy.a99spicy.domain.DomainDummyProducts

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

            val dummyProductList: MutableList<DomainDummyProduct> = mutableListOf()
            dummyProductList.add(
                DomainDummyProduct(
                    "0",
                    context.getString(R.string.dawat_biriyani_basmati_rice),
                    "5Kg",
                    "99.00",
                    "50",
                    R.drawable.grocery_place_holder
                )
            )
            dummyProductList.add(
                DomainDummyProduct(
                    "0",
                    context.getString(R.string.dawat_biriyani_basmati_rice),
                    "5Kg",
                    "99.00",
                    "50",
                    R.drawable.grocery_place_holder
                )
            )
            dummyProductList.add(
                DomainDummyProduct(
                    "0",
                    context.getString(R.string.dawat_biriyani_basmati_rice),
                    "5Kg",
                    "99.00",
                    "50",
                    R.drawable.grocery_place_holder
                )
            )
            dummyProductList.add(
                DomainDummyProduct(
                    "0",
                    context.getString(R.string.dawat_biriyani_basmati_rice),
                    "5Kg",
                    "99.00",
                    "50",
                    R.drawable.grocery_place_holder
                )
            )
            dummyProductList.add(
                DomainDummyProduct(
                    "0",
                    context.getString(R.string.dawat_biriyani_basmati_rice),
                    "5Kg",
                    "99.00",
                    "50",
                    R.drawable.grocery_place_holder
                )
            )
            dummyProductList.add(
                DomainDummyProduct(
                    "0",
                    context.getString(R.string.dawat_biriyani_basmati_rice),
                    "5Kg",
                    "99.00",
                    "50",
                    R.drawable.grocery_place_holder
                )
            )
            dummyProductList.add(
                DomainDummyProduct(
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
                    DomainDummyProducts(dummyProductList.toList())
                )
            )
            catList.add(
                DomainCategoryItem(
                    R.drawable.milk_placeholder,
                    "Milk",
                    DomainDummyProducts(dummyProductList.toList())
                )
            )
            catList.add(
                DomainCategoryItem(
                    R.drawable.grocery_place_holder,
                    "Vegetables",
                    DomainDummyProducts(dummyProductList.toList())
                )
            )
            catList.add(
                DomainCategoryItem(
                    R.drawable.grocery_place_holder,
                    "Fruits",
                    DomainDummyProducts(dummyProductList.toList())
                )
            )
            catList.add(
                DomainCategoryItem(
                    R.drawable.grocery_place_holder,
                    "Dairy",
                    DomainDummyProducts(dummyProductList.toList())
                )
            )
            catList.add(
                DomainCategoryItem(
                    R.drawable.grocery_place_holder,
                    "Bread",
                    DomainDummyProducts(dummyProductList.toList())
                )
            )
            catList.add(
                DomainCategoryItem(
                    R.drawable.grocery_place_holder,
                    "Branded Foods",
                    DomainDummyProducts(dummyProductList.toList())
                )
            )
            catList.add(
                DomainCategoryItem(
                    R.drawable.grocery_place_holder,
                    "Breakfast",
                    DomainDummyProducts(dummyProductList.toList())
                )
            )
            catList.add(
                DomainCategoryItem(
                    R.drawable.grocery_place_holder,
                    "Meat",
                    DomainDummyProducts(dummyProductList.toList())
                )
            )
            catList.add(
                DomainCategoryItem(
                    R.drawable.grocery_place_holder,
                    "Chicken",
                    DomainDummyProducts(dummyProductList.toList())
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
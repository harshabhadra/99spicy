package com.a99Spicy.a99spicy.utils

import com.a99Spicy.a99spicy.R
import com.a99Spicy.a99spicy.domain.DomainBannerItem
import com.a99Spicy.a99spicy.domain.DomainCategoryItem

class AppUtils {

    companion object{

        //Dummy banner list
        fun getBannerList():List<DomainBannerItem>{
            val bannerList:MutableList<DomainBannerItem> = mutableListOf()
            bannerList.add(DomainBannerItem("",R.drawable.banner_a))
            bannerList.add(DomainBannerItem("",R.drawable.banner_b))
            return bannerList
        }

        //Dummy category list
        fun getCategoryList():List<DomainCategoryItem>{
            val catList:MutableList<DomainCategoryItem> = mutableListOf()
            catList.add(DomainCategoryItem(R.drawable.grocery_place_holder,"Grocery"))
            catList.add(DomainCategoryItem(R.drawable.milk_placeholder,"Milk"))
            catList.add(DomainCategoryItem(R.drawable.grocery_place_holder,"Vegetables"))
            catList.add(DomainCategoryItem(R.drawable.grocery_place_holder,"Fruits"))
            catList.add(DomainCategoryItem(R.drawable.grocery_place_holder,"Dairy"))
            catList.add(DomainCategoryItem(R.drawable.grocery_place_holder,"Bread"))
            catList.add(DomainCategoryItem(R.drawable.grocery_place_holder,"Branded Foods"))
            catList.add(DomainCategoryItem(R.drawable.grocery_place_holder,"Breakfast"))
            catList.add(DomainCategoryItem(R.drawable.grocery_place_holder,"Meat"))
            catList.add(DomainCategoryItem(R.drawable.grocery_place_holder,"Chicken"))

            return catList
        }
    }
}
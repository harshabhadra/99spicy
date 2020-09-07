package com.a99Spicy.a99spicy.utils

import android.content.Context
import com.a99Spicy.a99spicy.R
import com.a99Spicy.a99spicy.domain.DomainBannerItem
import com.a99Spicy.a99spicy.domain.DomainCategoryItem
import com.a99Spicy.a99spicy.domain.DomainDummyProduct
import com.a99Spicy.a99spicy.domain.DomainDummyProducts
import kotlin.random.Random

class AppUtils {

    companion object {

        //Dummy banner list
        fun getBannerList(): List<DomainBannerItem> {
            val bannerList: MutableList<DomainBannerItem> = mutableListOf()
            bannerList.add(DomainBannerItem("", R.drawable.banner_a))
            bannerList.add(DomainBannerItem("", R.drawable.banner_b))
            return bannerList
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

        fun generatePaytmOrderId(): String{
            val random = Random(10000)
            return "ORDER_ID_${System.currentTimeMillis()}_${random.nextLong()}"
        }

        fun createUserName(name:String):String{
            val random = Random(10000)
            return "name_${System.currentTimeMillis()}${random.nextInt()}"
        }
    }
}
package com.a99Spicy.a99spicy.utils

import android.content.Context
import android.graphics.Color
import com.a99Spicy.a99spicy.R
import com.a99Spicy.a99spicy.domain.DomainBannerItem
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random

class AppUtils {

    companion object {

        //Dummy banner list
        fun getBannerList(): List<DomainBannerItem> {
            val bannerList: MutableList<DomainBannerItem> = mutableListOf()
            bannerList.add(DomainBannerItem("", R.drawable.banner_a))
            bannerList.add(DomainBannerItem("", R.drawable.banner_c))
            bannerList.add(DomainBannerItem("", R.drawable.banner_d))
            bannerList.add(DomainBannerItem("", R.drawable.banner_e))
            return bannerList
        }

        //dummy Profile items list
        fun getProfileItemsList(context: Context): List<String> {
            val list: MutableList<String> = mutableListOf()
            list.add(context.getString(R.string.wallet))
            list.add(context.getString(R.string.delivery_add))
            list.add(context.getString(R.string.orders))
            list.add(context.getString(R.string.shareandearn))
            list.add(context.getString(R.string.rate_us))

            return list
        }

        fun generatePaytmOrderId(): String {
            val random = Random(10000)
            return "ORDER_ID_${System.currentTimeMillis()}_${random.nextLong()}"
        }

        fun createUserName(name: String): String {
            val random = Random(10000)
            return "${name}_${System.currentTimeMillis()}${random.nextInt()}"
        }

        fun getRandomColor(): Int {
            val colorList: MutableList<Int> = mutableListOf()
            colorList.add(Color.parseColor("#ffebee"))
            colorList.add(Color.parseColor("#e8eaf6"))
            colorList.add(Color.parseColor("#fff9c4"))
            colorList.add(Color.parseColor("#c8e6c9"))
            colorList.shuffle()
            return colorList[0]
        }

        fun getCurrentDate(format: String): String {
            val date = Calendar.getInstance().time
            return date.toString(format)
        }

        private fun Date.toString(format: String, locale: Locale = Locale.getDefault()): String {
            val formatter = SimpleDateFormat(format, locale)
            return formatter.format(this)
        }

        fun getDaysAfter(days: Int): String {
            val calender = Calendar.getInstance()
            calender.add(Calendar.DAY_OF_YEAR, +days)
            val date = calender.time
            return date.toString("YYYY-MM-dd hh:mm:ss")
        }

        //Generate transaction id
        fun getTransactionId():String{
            return "TID${System.currentTimeMillis().toString()}"
        }
    }
}
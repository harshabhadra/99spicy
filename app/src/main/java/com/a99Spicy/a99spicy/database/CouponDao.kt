package com.a99Spicy.a99spicy.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface CouponDao {

    @Query("SELECT * from coupon_table")
    fun getAllCoupons():LiveData<List<DatabaseCoupon>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addCoupons(coupon: DatabaseCoupon)

    @Delete
    fun removeCoupon(coupon: DatabaseCoupon)
}
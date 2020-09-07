package com.a99Spicy.a99spicy.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface CartDao {

    @Query("SELECT * from cart_table")
    fun getCartItems():LiveData<List<DatabaseCart>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addItemToCart(databaseCart: DatabaseCart)

    @Delete
    fun deleteCartItem(databaseCart: DatabaseCart)
}
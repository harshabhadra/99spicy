package com.a99Spicy.a99spicy.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ProductDao {

    @Query("SELECT * from products_table")
    fun getAllProducts():LiveData<List<DatabaseProduct>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertProducts(vararg product: DatabaseProduct)

    @Query("DELETE from products_table")
    fun deleteAllProduct()
}
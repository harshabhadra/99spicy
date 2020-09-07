package com.a99Spicy.a99spicy.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface AddressDao{

    @Query("SELECT * from address_table")
    fun getAllAddress():LiveData<List<DatabaseShipping>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addAddress(databaseShipping: DatabaseShipping)

    @Delete
    fun removeAddress(databaseShipping: DatabaseShipping)
}
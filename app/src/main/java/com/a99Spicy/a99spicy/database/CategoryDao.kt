package com.a99Spicy.a99spicy.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CategoryDao {

    @Query("SELECT * from category_table")
    fun getAllCategories(): LiveData<List<DatabaseProductCategory>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCategory(vararg product: DatabaseProductCategory)

    @Query("DELETE from category_table")
    fun deleteAllCategories()
}
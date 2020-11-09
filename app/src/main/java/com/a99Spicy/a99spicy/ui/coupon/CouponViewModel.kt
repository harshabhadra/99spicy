package com.a99Spicy.a99spicy.ui.coupon

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.a99Spicy.a99spicy.MyApplication
import com.a99Spicy.a99spicy.Repository
import com.a99Spicy.a99spicy.database.MyDatabase

class CouponViewModel(private val application: MyApplication) : ViewModel() {

    private val database = MyDatabase.getDatabase(application)
    private val repository = Repository(database)

    val couponsList = repository.couponsList
}

class CouponViewModelFactory(private val application: MyApplication) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CouponViewModel::class.java)) {
            return CouponViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}
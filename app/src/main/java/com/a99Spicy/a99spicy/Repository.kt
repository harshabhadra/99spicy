package com.a99Spicy.a99spicy

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.a99Spicy.a99spicy.database.DatabaseProduct
import com.a99Spicy.a99spicy.database.MyDatabase
import com.a99Spicy.a99spicy.database.asDomainProductList
import com.a99Spicy.a99spicy.domain.DomainProduct
import com.a99Spicy.a99spicy.network.Api
import com.a99Spicy.a99spicy.network.asDataBaseProducts
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.lang.Exception

class Repository(val database: MyDatabase) {

    val productList:LiveData<List<DomainProduct>> =
        Transformations.map(database.productDao.getAllProducts()){
            it.asDomainProductList()
        }


    //Get all Products from server and store it into local database
    suspend fun refreshProducts(){
        withContext(Dispatchers.IO){
            val productsDeferred = Api.retrofitService.getProductsAsync()

            try {
                Timber.e("Product list received from server successfully")
                val products = productsDeferred.await()
                database.productDao.deleteAllProduct()
                database.productDao.insertProducts(*products.asDataBaseProducts())
            }catch (e:Exception){
                Timber.e("Failed to get products from server")
            }
        }
    }
}
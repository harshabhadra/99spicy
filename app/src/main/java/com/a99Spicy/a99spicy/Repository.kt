package com.a99Spicy.a99spicy

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.a99Spicy.a99spicy.database.*
import com.a99Spicy.a99spicy.domain.DeliveryAddress
import com.a99Spicy.a99spicy.domain.DomainCategoryItem
import com.a99Spicy.a99spicy.domain.DomainProduct
import com.a99Spicy.a99spicy.network.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

class Repository(val database: MyDatabase) {

    private var endCategories = false
    private var pageC = 1
    private var catList:MutableList<ProductCategory> = mutableListOf()

    private var endProducts = false
    private var pageP = 1
    private var allProductList:MutableList<Product> = mutableListOf()

    //Get products
    val productList: LiveData<List<DomainProduct>> =
        Transformations.map(database.productDao.getAllProducts()) {
            it.asDomainProductList()
        }

    //Get all categories
    val categoryList: LiveData<List<DomainCategoryItem>> =
        Transformations.map(database.categoryDao.getAllCategories()) {
            it.asDomainProductCategories()
        }

    //Get all cart items
    val cartItemsList:LiveData<List<DatabaseCart>> = database.cartDao.getCartItems()

    //Get all delivery address
    val deliveryAddressList:LiveData<List<DeliveryAddress>> =
        Transformations.map(database.addressDao.getAllAddress()){
            it.asDomainDeliveryAddress()
        }

    //Insert product to cart
    suspend fun addToCart(databaseCart: DatabaseCart){
        withContext(Dispatchers.IO){
            database.cartDao.addItemToCart(databaseCart)
        }
    }

    //Delete product from cart
    suspend fun deleteCart(databaseCart: DatabaseCart){
        withContext(Dispatchers.IO){
            database.cartDao.deleteCartItem(databaseCart)
        }
    }

    //Add delivery address
    suspend fun addAddress(databaseShipping: DatabaseShipping){
        withContext(Dispatchers.IO){
            database.addressDao.addAddress(databaseShipping)
        }
    }

    //Remove address
    suspend fun removeAddress(databaseShipping: DatabaseShipping){
        withContext(Dispatchers.IO){
            database.addressDao.removeAddress(databaseShipping)
        }
    }

    //Get all Products from server and store it into local database
    suspend fun refreshProducts() {
        withContext(Dispatchers.IO) {

            while (!endProducts) {
                val productsDeferred = Api.retrofitService.getProductsAsync(pageP, 100)
                try {
                    Timber.e("Product list received from server successfully")
                    val products = productsDeferred.await()
                    if (products.isNotEmpty()){
                        pageP ++
                        endProducts = false
                    }else{
                        endProducts = true
                        pageP = 1
                    }
                    allProductList.addAll(products)
                    database.productDao.insertProducts(*products.asDataBaseProducts())
                } catch (e: Exception) {
                    Timber.e("Failed to get products from server")
                }
            }
        }
    }

    //Refresh categories
    suspend fun refreshCategories() {
        withContext(Dispatchers.IO) {

            while (!endCategories) {
                val categoryDeferred =
                    Api.retrofitService.getCategoriesAsync(pageC, 100)
                try {
                    val categories = categoryDeferred.await()
                    if (categories.isNotEmpty()){
                        pageC++
                        endCategories = false
                    }else{
                        endCategories = true
                        pageC = 1
                    }
                    catList.addAll(categories)
//                    database.categoryDao.deleteAllCategories()
                    database.categoryDao.insertCategory(*catList.asDataBaseProductCategoryList())
                } catch (e: Exception) {
                    Timber.e("Failed to get categories: ${e.message}")
                }
            }
        }
    }
}
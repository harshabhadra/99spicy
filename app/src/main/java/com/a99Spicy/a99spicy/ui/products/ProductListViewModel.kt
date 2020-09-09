package com.a99Spicy.a99spicy.ui.products

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.a99Spicy.a99spicy.MyApplication
import com.a99Spicy.a99spicy.Repository
import com.a99Spicy.a99spicy.database.DatabaseCart
import com.a99Spicy.a99spicy.database.MyDatabase
import com.a99Spicy.a99spicy.database.asDomainProductList
import com.a99Spicy.a99spicy.domain.DomainProduct
import com.a99Spicy.a99spicy.domain.DomainProducts
import com.a99Spicy.a99spicy.network.Api
import com.a99Spicy.a99spicy.network.asDataBaseProducts
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber

private var quantity = 0
private var page = 1
private var endProducts = false
private var productList: MutableList<DomainProduct> = mutableListOf()
private var domainProductsList: MutableList<DomainProducts> = mutableListOf()

enum class ProductLoading {
    SUCCESS, PENDING, FAILED
}

class ProductListViewModel(application: MyApplication) : ViewModel() {

    private val viewModelJob = Job()

    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)
    private val database = MyDatabase.getDatabase(application)
    private val repository = Repository(database)

    private val apiService = Api.retrofitService
    private var _productQtyMutableLiveData = MutableLiveData<Int>()
    val productQtyLiveData: LiveData<Int>
        get() = _productQtyMutableLiveData

    private var _productsByCatMutableLiveData = MutableLiveData<List<DomainProduct>>()
    val productsByCatLiveData: LiveData<List<DomainProduct>>
        get() = _productsByCatMutableLiveData

    private var _productsByCategoryMutableLiveData = MutableLiveData<List<DomainProducts>>()
    val productsByCategoryLiveData: LiveData<List<DomainProducts>>
        get() = _productsByCategoryMutableLiveData

    val cartItemsLiveData = repository.cartItemsList

    init {
        _productsByCatMutableLiveData.value = null
        _productQtyMutableLiveData.value = 0
        _productsByCategoryMutableLiveData.value = null
    }

    fun addItemToCart(databaseCart: DatabaseCart) {
        uiScope.launch {
            repository.addToCart(databaseCart)
        }
    }

    fun removeItemFromCart(databaseCart: DatabaseCart) {
        uiScope.launch {
            repository.deleteCart(databaseCart)
        }
    }

    fun setCategoryProductList(mProductList:List<DomainProduct>){
        Timber.e("product List size in viewModel: ${mProductList.size}")
        domainProductsList.add(DomainProducts(mProductList))
        _productsByCategoryMutableLiveData.value = domainProductsList
    }

    fun resetProductsList(){
        domainProductsList.clear()
    }

    fun addQuantity() {
        quantity++
        _productQtyMutableLiveData.value = quantity
    }

    fun minusQuantity() {
        if (quantity >= 1) {
            quantity--
            _productQtyMutableLiveData.value = quantity
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}

class ProductListViewModelFactory(private val application: MyApplication) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProductListViewModel::class.java)) {
            return ProductListViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
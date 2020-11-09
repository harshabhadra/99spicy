package com.a99Spicy.a99spicy.ui.home

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
import com.a99Spicy.a99spicy.network.Api
import com.a99Spicy.a99spicy.network.Profile
import kotlinx.coroutines.*
import timber.log.Timber

enum class HomeLoading {
    SUCCESS, FAILED, PENDING
}

class HomeViewModel(application: MyApplication) : ViewModel() {

    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private val apiService = Api.retrofitService

    private val database = MyDatabase.getDatabase(application)
    private val repository = Repository(database)

    private var _profileMutableLiveData = MutableLiveData<Profile>()
    val profileLiveData: LiveData<Profile>
        get() = _profileMutableLiveData

    private var _loadingMutableLiveData = MutableLiveData<HomeLoading>()
    val loadingLiveData: LiveData<HomeLoading>
        get() = _loadingMutableLiveData

    private var _searchMutableLiveData = MutableLiveData<List<DomainProduct>>()
    val searchLiveData: LiveData<List<DomainProduct>>
        get() = _searchMutableLiveData

    init {
        _searchMutableLiveData.value = null
        uiScope.launch {
            repository.refreshProducts()
            repository.refreshCategories()
        }
    }

    //Getting all categories
    val categoriesLiveData = repository.categoryList

    //Get product list
    val productListLiveData = repository.productList

    //Get cart items
    val cartItemListLiveData = repository.cartItemsList

    //Get user profile
    fun getProfile(userId: String) {
        uiScope.launch {
            val responseDeferred = apiService.getSingleCustomerAsync(userId)
            try {
                _loadingMutableLiveData.value = HomeLoading.PENDING
                val response = responseDeferred.await()
                _profileMutableLiveData.value = response
                _loadingMutableLiveData.value = HomeLoading.SUCCESS
                Timber.e("Successfully received profile")
            } catch (e: Exception) {
                Timber.e("Failed to get user profile: ${e.message}")
                _profileMutableLiveData.value = null
                _loadingMutableLiveData.value = HomeLoading.FAILED
            }
        }
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

    fun getProductsByName(name: String) {
        uiScope.launch {
            _searchMutableLiveData.value =
                withContext(Dispatchers.IO) {
                    database.productDao.getProductsByName(name).asDomainProductList()
                }
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}

class HomeViewModelFactory(private val application: MyApplication) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}
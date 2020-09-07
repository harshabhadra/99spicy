package com.a99Spicy.a99spicy.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.a99Spicy.a99spicy.MyApplication
import com.a99Spicy.a99spicy.Repository
import com.a99Spicy.a99spicy.database.MyDatabase
import com.a99Spicy.a99spicy.domain.DomainProduct
import com.a99Spicy.a99spicy.domain.DomainProducts
import com.a99Spicy.a99spicy.domain.DomainSortedCategory
import com.a99Spicy.a99spicy.domain.LocationDetails
import com.a99Spicy.a99spicy.network.Api
import com.a99Spicy.a99spicy.network.ProductCategory
import com.a99Spicy.a99spicy.network.Profile
import com.a99Spicy.a99spicy.ui.profile.Loading
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber

enum class HomeLoading{
    SUCCESS, FAILED, PENDING
}

class HomeViewModel(application: MyApplication) : ViewModel() {

    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private val apiService = Api.retrofitService

    private val database = MyDatabase.getDatabase(application)
    private val repository = Repository(database)

    private var _domainProductsMutableLiveData = MutableLiveData<List<DomainProducts>>()
    val domainProductsLiveData: LiveData<List<DomainProducts>>
        get() = _domainProductsMutableLiveData

    private var _subCategoriesMutableLiveData = MutableLiveData<Set<ProductCategory>>()
    val subCategoriesLiveData: LiveData<Set<ProductCategory>>
        get() = _subCategoriesMutableLiveData

    private var _profileMutableLiveData = MutableLiveData<Profile>()
    val profileLiveData: LiveData<Profile>
        get() = _profileMutableLiveData

    private var _loadingMutableLiveData = MutableLiveData<HomeLoading>()
    val loadingLiveData:LiveData<HomeLoading>
        get() = _loadingMutableLiveData

    init {
        _domainProductsMutableLiveData.value = null
        _subCategoriesMutableLiveData.value = null
        uiScope.launch {
            repository.refreshProducts()
            repository.refreshCategories()
        }
    }

    //Getting all categories
    val categoriesLiveData = repository.categoryList

    val productListLiveData = repository.productList
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

    fun getSubCategories(parentId: Int) {
        uiScope.launch {
            val subCategoriesDeferred =
                apiService.getSubCategoriesAsync(parentId)
            try {
                _subCategoriesMutableLiveData.value = subCategoriesDeferred.await().toSet()
            }catch (e:Exception){
                Timber.e("Failed to get subcategoreis: ${e.message}")
            }
        }
    }

    fun setDomainProductList(list: List<DomainProducts>) {
        _domainProductsMutableLiveData.value = list
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
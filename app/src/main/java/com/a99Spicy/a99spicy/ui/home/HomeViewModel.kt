package com.a99Spicy.a99spicy.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.a99Spicy.a99spicy.MyApplication
import com.a99Spicy.a99spicy.Repository
import com.a99Spicy.a99spicy.database.MyDatabase
import com.a99Spicy.a99spicy.domain.DomainProduct
import com.a99Spicy.a99spicy.domain.DomainSortedCategory
import com.a99Spicy.a99spicy.domain.LocationDetails
import com.a99Spicy.a99spicy.network.Api
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

private var pList: MutableSet<DomainProduct> = mutableSetOf()
private var sortedCatList:MutableSet<DomainSortedCategory> = mutableSetOf()

class HomeViewModel(application: MyApplication) : ViewModel() {

    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private val apiService = Api.retroService

    private val database = MyDatabase.getDatabase(application)
    private val repository = Repository(database)

    private var _locationMutableLiveData = MutableLiveData<LocationDetails>()
    val locationLiveData: LiveData<LocationDetails>
        get() = _locationMutableLiveData

    private var _categoryListMutableLiveData = MutableLiveData<Set<String>>()
    val categoryListLiveData: LiveData<Set<String>>
        get() = _categoryListMutableLiveData

    private var _sortedCatListMutableLiveData = MutableLiveData<List<DomainSortedCategory>>()
    val sortedCategoryLiveData:LiveData<List<DomainSortedCategory>>
    get() = _sortedCatListMutableLiveData

    init {

        _categoryListMutableLiveData.value = null
        _locationMutableLiveData.value = null
        _sortedCatListMutableLiveData.value = null

        uiScope.launch {
            repository.refreshProducts()
        }
    }

    //Getting all dummyProducts
    val productListLiveData = repository.productList

    fun setLocationData(locationDetails: LocationDetails) {
        _locationMutableLiveData.value = locationDetails
    }

    fun setCategoryList(catList: Set<String>) {
        _categoryListMutableLiveData.value = catList
    }

    fun setSortedCatList(domainSortedCategory: DomainSortedCategory){
        sortedCatList.add(domainSortedCategory)
        _sortedCatListMutableLiveData.value = sortedCatList.toList()
    }

    fun resetSortedCatList(){
        _sortedCatListMutableLiveData.value = null
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
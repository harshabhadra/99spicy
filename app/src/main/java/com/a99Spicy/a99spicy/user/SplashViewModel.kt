package com.a99Spicy.a99spicy.user

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.a99Spicy.a99spicy.network.Api
import com.a99Spicy.a99spicy.network.SignUpResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber

class SplashViewModel : ViewModel() {

    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private val apiService = Api.loginService

    private var _createUserMutableLiveData = MutableLiveData<SignUpResponse>()
    val createUserLiveData: LiveData<SignUpResponse>
        get() = _createUserMutableLiveData

    private var _loginMutableLiveData = MutableLiveData<SignUpResponse>()
    val loginLiveData: LiveData<SignUpResponse>
        get() = _loginMutableLiveData

    private var _fTokenMutableLiveData = MutableLiveData<String>()
    val fTokenLiveData:LiveData<String>
    get() = _fTokenMutableLiveData

    init {
        _createUserMutableLiveData.value = null
        _loginMutableLiveData.value = null
        _fTokenMutableLiveData.value = null
    }

    fun createUser(
        name: String,
        countryCode: String,
        mobile: String,
        userName: String,
        fToken: String
    ) {
        uiScope.launch {

            val responseDeferred =
                apiService.createUserAsync(name, countryCode, mobile, userName, fToken)
            try {
                val response = responseDeferred.await()
                _createUserMutableLiveData.value = response
            } catch (e: Exception) {
                _createUserMutableLiveData.value = null
                Timber.e("Failed to create account: ${e.message}")
            }
        }
    }

    fun loginUser(phone: String, countryCode: String, fToken: String, otp:String) {
        uiScope.launch {
            val responseDeferred = apiService.loginUserAsync(phone, countryCode, fToken, otp)

            try {
                val response = responseDeferred.await()
                _loginMutableLiveData.value = response
            } catch (e: Exception) {
                Timber.e("Failed to login: ${e.message}")
            }
        }
    }

    fun setFToken(fToken: String){
        _fTokenMutableLiveData.value = fToken
    }

    fun resetFToken(){
        _fTokenMutableLiveData.value = null
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}
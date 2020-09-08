package com.a99Spicy.a99spicy.user

import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.a99Spicy.a99spicy.network.Api
import com.a99Spicy.a99spicy.network.Profile
import com.a99Spicy.a99spicy.network.SignUpResponse
import com.a99Spicy.a99spicy.ui.profile.Loading
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber

enum class LoginLoading{
    LOGIN_SUCCESS, LOGIN_PENDING, LOGIN_FAILED
}

enum class SignUpLoading{
    SIGNUP_SUCCESS, SIGNUP_PENDING, SIGNUP_FAILED
}

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

    private var _loginLoadingMutableLiveData = MutableLiveData<LoginLoading>()
    val loginLoadingLiveData:LiveData<LoginLoading>
    get() = _loginLoadingMutableLiveData

    private var _signUpLoadingMutableLiveData = MutableLiveData<SignUpLoading>()
    val signUpLoadingLiveData:LiveData<SignUpLoading>
    get() = _signUpLoadingMutableLiveData

    init {
        _createUserMutableLiveData.value = null
        _loginMutableLiveData.value = null
        _fTokenMutableLiveData.value = null
        _loginLoadingMutableLiveData.value = null
        _signUpLoadingMutableLiveData.value = null
    }

    //Create user
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
                _signUpLoadingMutableLiveData.value = SignUpLoading.SIGNUP_PENDING
                val response = responseDeferred.await()
                _createUserMutableLiveData.value = response
                _signUpLoadingMutableLiveData.value = SignUpLoading.SIGNUP_SUCCESS
            } catch (e: Exception) {
                _createUserMutableLiveData.value = null
                _signUpLoadingMutableLiveData.value = SignUpLoading.SIGNUP_FAILED
                Timber.e("Failed to create account: ${e.message}")
            }
        }
    }

    //Log in user
    fun loginUser(phone: String, countryCode: String, fToken: String, otp:String) {
        uiScope.launch {
            val responseDeferred = apiService.loginUserAsync(phone, countryCode, fToken, otp)
            try {
                _loginLoadingMutableLiveData.value = LoginLoading.LOGIN_PENDING
                val response = responseDeferred.await()
                _loginMutableLiveData.value = response
                _loginLoadingMutableLiveData.value = LoginLoading.LOGIN_SUCCESS
            } catch (e: Exception) {
                _loginMutableLiveData.value = null
                Timber.e("Failed to login: ${e.message}")
                _loginLoadingMutableLiveData.value = LoginLoading.LOGIN_FAILED
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
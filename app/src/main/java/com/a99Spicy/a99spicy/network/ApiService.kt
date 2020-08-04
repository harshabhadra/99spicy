package com.a99Spicy.a99spicy.network

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

private const val BASE_URL = "http://www.99spicy.com/wp-json/wp/v2/"

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

class RetrofitClient() {
    companion object {
        fun getClient(): Retrofit {
            return Retrofit.Builder()
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .baseUrl(BASE_URL)
                .build()
        }

        fun getRetrofitClient(): Retrofit {
            return Retrofit.Builder()
                .addConverterFactory(ScalarsConverterFactory.create())
                .baseUrl(BASE_URL)
                .build()
        }
    }
}

object Api{
    val retrofitService: ApiService by lazy {
        RetrofitClient.getClient().create(ApiService::class.java)
    }
    val retroService: ApiService by lazy {
        RetrofitClient.getRetrofitClient().create(ApiService::class.java)
    }
}

interface ApiService {

    @GET("product")
    fun getOrders(
        @Query("username") userName:String,
        @Query("password ") passowrd:String
    ): Call<String>
}
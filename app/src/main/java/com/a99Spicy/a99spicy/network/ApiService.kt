package com.a99Spicy.a99spicy.network

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Deferred
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query


private const val BASE_URL = "https://99spicy.com/wp-json/wc/v3/"

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

var clientBuilder = OkHttpClient.Builder()
    .addInterceptor(
    BasicAuthInterceptor(
        "",
        ""
    )
)


class RetrofitClient() {
    companion object {
        fun getClient(): Retrofit {
            return Retrofit.Builder()
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .baseUrl(BASE_URL)
                .client(clientBuilder.build())
                .build()
        }

        fun getRetrofitClient(): Retrofit {
            return Retrofit.Builder()
                .addConverterFactory(ScalarsConverterFactory.create())
                .baseUrl(BASE_URL)
                .client(clientBuilder.build())
                .build()
        }
    }
}

object Api {
    val retrofitService: ApiService by lazy {
        RetrofitClient.getClient().create(ApiService::class.java)
    }
    val retroService: ApiService by lazy {
        RetrofitClient.getRetrofitClient().create(ApiService::class.java)
    }
}

interface ApiService {

    //Get All Products
    @GET("products")
    fun getProductsAsync():Deferred<List<Product>>

    //Get products by category
    @GET("products")
    fun getProductsByCatAsync(@Query("category")catId:Int):Deferred<List<Product>>
}
package com.a99Spicy.a99spicy.network

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Deferred
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.*


private const val BASE_URL = ""
private const val LOGIN_URL = ""
private const val RAZOR_PAY_URL = ""
private const val WALLET_URL = ""

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private var clientBuilder = OkHttpClient.Builder()
    .addInterceptor(
        BasicAuthInterceptor(
            "",
            ""
        )
    )

private var razorPayClientBuilder = OkHttpClient.Builder()
    .addInterceptor(
        RazorPayAuthInterceptor()
    )

private var walletClientBuilder = OkHttpClient.Builder()
    .addInterceptor(
        WalletAuthInterceptor()
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

        fun getLoginClient(): Retrofit {
            return Retrofit.Builder()
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .baseUrl(LOGIN_URL)
                .build()
        }

        fun getRazorPayClient(): Retrofit {
            return Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .client(razorPayClientBuilder.build())
                .baseUrl(RAZOR_PAY_URL)
                .build()
        }

        fun getWalletClient(): Retrofit {
            return Retrofit.Builder()
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .addConverterFactory(ScalarsConverterFactory.create())
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .client(walletClientBuilder.build())
                .baseUrl(WALLET_URL)
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
    val loginService: ApiService by lazy {
        RetrofitClient.getLoginClient().create(ApiService::class.java)
    }

    val razorPayService: ApiService by lazy {
        RetrofitClient.getRazorPayClient().create(ApiService::class.java)
    }

    val walletService: ApiService by lazy {
        RetrofitClient.getWalletClient().create(ApiService::class.java)
    }
}

interface ApiService {

    //Get All Products
    @GET("products")
    fun getProductsAsync(
        @Query("page") page: Int,
        @Query("per_page") per_page: Int
    ): Deferred<List<Product>>

    //Get products by category
    @GET("products")
    fun getProductsByCatAsync(
        @Query("category") catId: String,
        @Query("page") page: Int,
        @Query("per_page") per_page: Int
    ): Deferred<List<Product>>

    //Get products categories
    @GET("products/categories")
    fun getCategoriesAsync(
        @Query("page") page: Int,
        @Query("per_page") per_page: Int
    ): Deferred<List<ProductCategory>>

    //Get product sub-categories
    @GET("products/categories")
    fun getSubCategoriesAsync(
        @Query("parent") parentId: Int
    ): Deferred<List<ProductCategory>>

    //Razor pay order id
    @POST("v1/orders")
    @Headers("Content-Type: application/json")
    fun generateOrderIdAsync(@Body razorPayPayment: RazorPayPayment):
            Call<RazorPaymentResponse>

    //Sign up user
    @POST("v1/create_user")
    @FormUrlEncoded
    fun createUserAsync(
        @Field("digits_reg_name") name: String,
        @Field("digits_reg_countrycode") countryCode: String,
        @Field("digits_reg_mobile") mobile: String,
        @Field("digits_reg_username") userName: String,
        @Field("ftoken") fToken: String
    ): Deferred<SignUpResponse>

    //Log in user
    @POST("v1/login_user")
    @FormUrlEncoded
    fun loginUserAsync(
        @Field("user") phone: String,
        @Field("countrycode") countryCode: String,
        @Field("ftoken") fToken: String,
        @Field("otp") otp: String
    ): Deferred<SignUpResponse>

    //Get a single customer
    @GET("customers/{id}")
    fun getSingleCustomerAsync(@Path("id") id: String): Deferred<Profile>

    //Set delivery address
    @PUT("customers/{id}")
    fun setDeliveryAddressAsync(
        @Path("id") id: String,
        @Body address: Address
    ): Deferred<Profile>

    //Create order
    @POST("orders")
    fun placeOrderAsync(@Body order: PlaceOrder): Deferred<OrderResponse>

    //Get all orders
    @GET("orders")
    fun getAllOrdersAsync(@Query("customer") customerId: Int):
            Deferred<List<OrderResponse>>

    //Get wallet transactions
    @GET("wallet/{id}")
    fun getWalletTransactionsAsync(
        @Path("id") id: String
    ): Deferred<List<WalletTransaction>>

    //Get wallet balance
    @GET("current_balance/{id}")
    fun getWalletBalance(@Path("id") id: String): Call<String>

    //Credit or debit
    @POST("wallet/{id}")
    fun cDWalletAsync(
        @Path("id") id: String,
        @Body walletRequest: WalletRequest
    ): Deferred<WalletResponse>
}
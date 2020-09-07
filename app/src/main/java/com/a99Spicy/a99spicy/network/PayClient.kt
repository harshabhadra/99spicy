package com.a99Spicy.a99spicy.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

class PayClient {

    companion object {
        private var retrofit: Retrofit? = null
        fun getRetrofit(baseUrl: String): Retrofit? {
            if (retrofit == null) {
                retrofit = Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .build()
            }
            return retrofit
        }
    }
}
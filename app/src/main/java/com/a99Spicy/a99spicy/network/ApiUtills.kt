package com.a99Spicy.a99spicy.network

class ApiUtills {

    companion object {
        const val PAYTM_URL = "https://99spicy.com/"

        fun getApiServices(): Operators? {
            return PayClient.getRetrofit(PAYTM_URL)?.create(Operators::class.java)
        }
    }
}
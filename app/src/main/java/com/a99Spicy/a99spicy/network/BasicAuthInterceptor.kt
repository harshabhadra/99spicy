package com.a99Spicy.a99spicy.network

import okhttp3.Credentials
import okhttp3.Headers
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException


class BasicAuthInterceptor(user: String, password: String) :
    Interceptor {
    private val authToken: String = Credentials.basic(user, password)

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        val headers: Headers =
            request.headers().newBuilder().add("Authorization", authToken).build()
        request = request.newBuilder().headers(headers).build()
        return chain.proceed(request)
    }

}
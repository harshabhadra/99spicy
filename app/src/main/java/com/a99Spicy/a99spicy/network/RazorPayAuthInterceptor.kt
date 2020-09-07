package com.a99Spicy.a99spicy.network

import okhttp3.Headers
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class RazorPayAuthInterceptor : Interceptor {

    private val authToken: String =
        okhttp3.Credentials.basic("rzp_live_oX0LM5QcdnNfXj", "dI4IBnlUtcl9zJ7aQ8BQg9t1")

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        val headers: Headers =
            request.headers.newBuilder().add("Authorization", authToken).build()
        request = request.newBuilder().headers(headers).build()
        return chain.proceed(request)
    }
}
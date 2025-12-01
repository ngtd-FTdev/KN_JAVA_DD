package com.example.frontend_nhom1.network

import com.example.frontend_nhom1.util.PreferencesHelper
import okhttp3.Interceptor
import okhttp3.Response

class TokenInterceptor(private val prefs: PreferencesHelper) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()
        val token = prefs.getToken()
        if (!token.isNullOrEmpty()) {
            requestBuilder.addHeader("Authorization", "Bearer $token")
        }
        return chain.proceed(requestBuilder.build())
    }
}

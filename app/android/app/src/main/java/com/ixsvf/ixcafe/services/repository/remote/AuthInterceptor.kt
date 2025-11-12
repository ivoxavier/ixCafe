package com.ixsvf.ixcafe.services.repository.remote

import com.ixsvf.ixcafe.services.repository.Settings
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val settings: Settings) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        // 1. Usamos runBlocking para chamar a função suspend a partir de um contexto síncrono
        val token = runBlocking {
            settings.fetchAuthToken()
        }

        val requestBuilder = chain.request().newBuilder()


        if (!token.isNullOrEmpty()) {
            requestBuilder.addHeader("Authorization", "Bearer $token")
        }


        return chain.proceed(requestBuilder.build())
    }
}
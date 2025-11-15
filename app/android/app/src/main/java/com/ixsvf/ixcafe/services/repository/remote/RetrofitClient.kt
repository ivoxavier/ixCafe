package com.ixsvf.ixcafe.services.repository.remote

import com.ixsvf.ixcafe.constants.IxCafeConstants
import com.ixsvf.ixcafe.services.repository.Settings
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit

object RetrofitClient {

    // Configuração do parser JSON para ignorar chaves desconhecidas na resposta da API
    private val json = Json {
        ignoreUnknownKeys = true
    }

    private lateinit var retrofit: Retrofit

    fun initialize(settings: Settings) {

        val httpClient = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(settings))
            .build()

        // Constrói a instância do Retrofit
        retrofit = Retrofit.Builder()
            .baseUrl(IxCafeConstants.ENDPOINTS_ROUTES.BASE_URL)
            .client(httpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    }


    fun <T> create(service: Class<T>): T {
        // Verificação de segurança para garantir que initialize() foi chamado antes
        if (!::retrofit.isInitialized) {
            throw UninitializedPropertyAccessException("RetrofitClient must be initialized in the Application class.")
        }
        return retrofit.create(service)
    }
}
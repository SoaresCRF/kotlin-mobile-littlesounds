package com.dev.soarescrf.littlesounds.core.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Singleton responsável por configurar e fornecer uma instância do Retrofit
 * para comunicação com a API.
 */
object RetrofitClient {

    private const val BASE_URL = "https://nodejs-backend-littlesounds.onrender.com/"

    /**
     * Instância preguiçosa do [ApiService] configurada com o Retrofit,
     * para uso nas chamadas de API.
     */
    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}

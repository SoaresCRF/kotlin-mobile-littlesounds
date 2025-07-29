package com.dev.soarescrf.littlesounds.core.network

import com.google.gson.JsonObject
import retrofit2.http.GET

/**
 * Interface que define os endpoints da API para comunicação via Retrofit.
 */
interface ApiService {

    /**
     * Solicita dados dinâmicos do endpoint raiz "/".
     *
     * @return [JsonObject] contendo os dados recebidos da API.
     */
    @GET("/")
    suspend fun getDynamicData(): JsonObject
}

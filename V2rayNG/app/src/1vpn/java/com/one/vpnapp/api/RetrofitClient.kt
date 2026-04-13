package com.one.vpnapp.api

import androidx.compose.runtime.mutableStateOf
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    private const val PRIMARY = "1vpn.org"
    private val BACKUPS = listOf(
        "1vpn.co",
        "onevpn.com",
        "cloudlogcdn.com"
    )

    val usingBackupDomain = mutableStateOf<String?>(null)

    fun activeBaseUrl(): String = "https://${usingBackupDomain.value ?: PRIMARY}"

    private fun buildHttpClient() =
        OkHttpClient.Builder()
            .connectTimeout(5, TimeUnit.SECONDS)
            .readTimeout(5, TimeUnit.SECONDS)
            .writeTimeout(5, TimeUnit.SECONDS)
            .build()

    private fun buildRetrofit(domain: String): Retrofit =
        Retrofit.Builder()
            .baseUrl("https://$domain/api/")
            .client(buildHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    val retrofit: Retrofit = buildRetrofit(PRIMARY)
    val apiService: ApiService = retrofit.create(ApiService::class.java)

    private fun shouldUseResponse(code: Int) = code in 200..499

    suspend fun <T> callWithFallback(call: suspend (ApiService) -> Response<T>): Response<T> {
        // Try primary once
        try {
            val response = call(apiService)
            if (shouldUseResponse(response.code())) {
                usingBackupDomain.value = null
                return response
            }
        } catch (e: Exception) {
            // fall through to backups
        }

        // Try backups
        for (domain in BACKUPS) {
            try {
                val service = buildRetrofit(domain).create(ApiService::class.java)
                val response = call(service)
                if (shouldUseResponse(response.code())) {
                    usingBackupDomain.value = domain
                    return response
                }
            } catch (e: Exception) {
                // continue
            }
        }

        throw Exception("All API hosts failed")
    }
}

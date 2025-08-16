package com.one.vpnapp.api

import com.one.vpnapp.model.UserData
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.HeaderMap
import retrofit2.http.Headers
import retrofit2.http.POST

data class LoginRequest(
    val username: String,
    val password: String,
    val token: String? = null
)

data class SignUpRequest(
    val username: String,
    val password: String,
    val email: String,
    val revenuecat_id: String? = null
)

data class RefreshTokenResponse(
    val sessionAuthToken: String
)

interface ApiService {
    @Headers("Content-Type: application/json", "Client-Type: app")
    @POST("login/")
    suspend fun login(@Body request: LoginRequest): Response<UserData>

    @Headers("Content-Type: application/json", "Client-Type: app")
    @POST("signup/")
    suspend fun signUp(@Body request: SignUpRequest): Response<UserData>

    @Headers("Content-Type: application/json", "Client-Type: app")
    @POST("get_user_data/")
    suspend fun fetchUserData(
        @HeaderMap headers: Map<String, String>
    ): Response<UserData>

    @POST("refresh_token/")
    suspend fun refreshToken(
        @HeaderMap headers: Map<String, String>
    ): Response<RefreshTokenResponse>
}
package com.one.vpnapp.api

import com.one.vpnapp.model.UserData
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.HeaderMap
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

data class LoginRequest(
    val email: String,
    val password: String,
    val token: String? = null,
)

data class SignUpRequest(
    val email: String,
    val password: String,
)

data class RefreshTokenResponse(
    val sessionAuthToken: String
)

interface ApiService {
    @Headers("Content-Type: application/json", "Client-Type: app")
    @POST("login/")
    suspend fun login(
        @Body request: LoginRequest,
        @Query("type") type: String = "xray"
    ): Response<UserData>

    @Headers("Content-Type: application/json", "Client-Type: app")
    @POST("signup/")
    suspend fun signUp(
        @Body request: SignUpRequest,
        @Query("type") type: String = "xray"
    ): Response<UserData>

    @Headers("Content-Type: application/json", "Client-Type: app")
    @POST("get_user_data_api/")
    suspend fun fetchUserData(
        @HeaderMap headers: Map<String, String>,
        @Query("type") type: String = "xray"
    ): Response<UserData>

    @POST("refresh_token/")
    suspend fun refreshToken(
        @HeaderMap headers: Map<String, String>
    ): Response<RefreshTokenResponse>
}

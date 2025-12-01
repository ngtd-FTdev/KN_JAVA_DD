package com.example.frontend.api

import com.example.frontend.data.models.AuthResponse
import com.example.frontend.data.models.LoginRequest
import com.example.frontend.data.models.RegisterRequest
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {
    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): AuthResponse

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse

    @POST("auth/refresh")
    suspend fun refresh(@Body request: RefreshRequest): AuthResponse

    @POST("auth/logout")
    suspend fun logout(@Body request: LogoutRequest)
}

data class RefreshRequest(val refreshToken: String)
data class LogoutRequest(val refreshToken: String)

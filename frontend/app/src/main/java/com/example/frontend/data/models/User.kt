package com.example.frontend.data.models

data class User(
    val id: String,
    val username: String
)

data class AuthResponse(
    val token: String,
    val refreshToken: String,
    val user: User
)

data class LoginRequest(
    val username: String,
    val password: String
)

data class RegisterRequest(
    val username: String,
    val password: String
)

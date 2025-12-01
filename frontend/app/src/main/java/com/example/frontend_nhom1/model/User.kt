package com.example.frontend_nhom1.model

data class User(val id: String, val username: String)

data class LoginRequest(val username: String, val password: String)
data class LoginResponse(val token: String?, val user: User?)

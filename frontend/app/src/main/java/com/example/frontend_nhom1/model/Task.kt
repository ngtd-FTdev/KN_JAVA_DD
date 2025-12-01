package com.example.frontend_nhom1.model

data class Task(
    val _id: String,
    val title: String,
    val description: String? = null,
    val completed: Boolean = false,
    val owner: String? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null
)

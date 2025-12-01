package com.example.frontend.data.models

import com.squareup.moshi.Json
import java.util.Date

data class Task(
    @Json(name = "_id")
    val id: String,
    val title: String,
    val description: String = "",
    val completed: Boolean = false,
    @Json(name = "createdAt")
    val createdAt: String = "",
    @Json(name = "updatedAt")
    val updatedAt: String = ""
)

data class CreateTaskRequest(
    val title: String,
    val description: String = ""
)

data class UpdateTaskRequest(
    val title: String? = null,
    val description: String? = null,
    val completed: Boolean? = null
)

data class TaskListResponse(
    val data: List<Task>,
    val meta: Meta
)

data class Meta(
    val page: Int,
    val limit: Int,
    val total: Int
)

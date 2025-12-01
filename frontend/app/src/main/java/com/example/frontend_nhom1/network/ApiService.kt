package com.example.frontend_nhom1.network

import com.example.frontend_nhom1.model.LoginRequest
import com.example.frontend_nhom1.model.LoginResponse
import com.example.frontend_nhom1.model.Task
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @POST("/api/auth/login")
    suspend fun login(@Body body: LoginRequest): Response<LoginResponse>

    @GET("/api/tasks")
    suspend fun getTasks(@Query("page") page: Int = 1, @Query("limit") limit: Int = 20): Response<TaskListResponse>

    @POST("/api/tasks")
    suspend fun createTask(@Body task: TaskCreateRequest): Response<Task>

    @PUT("/api/tasks/{id}")
    suspend fun updateTask(@Path("id") id: String, @Body task: TaskUpdateRequest): Response<Task>

    @DELETE("/api/tasks/{id}")
    suspend fun deleteTask(@Path("id") id: String): Response<DeleteResponse>
}

data class TaskListResponse(
    val data: List<Task> = emptyList(),
    val meta: Meta? = null
)

data class Meta(val page: Int = 1, val limit: Int = 20, val total: Int = 0)

data class TaskCreateRequest(val title: String, val description: String? = null)
data class TaskUpdateRequest(val title: String? = null, val description: String? = null, val completed: Boolean? = null)
data class DeleteResponse(val msg: String)

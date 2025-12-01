package com.example.frontend.api

import com.example.frontend.data.models.CreateTaskRequest
import com.example.frontend.data.models.Task
import com.example.frontend.data.models.TaskListResponse
import com.example.frontend.data.models.UpdateTaskRequest
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface TaskService {
    @GET("tasks")
    suspend fun getTasks(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20,
        @Query("completed") completed: Boolean? = null,
        @Query("q") query: String? = null,
        @Query("sort") sort: String? = null
    ): TaskListResponse

    @GET("tasks/{id}")
    suspend fun getTask(@Path("id") id: String): Task

    @POST("tasks")
    suspend fun createTask(@Body request: CreateTaskRequest): Task

    @PUT("tasks/{id}")
    suspend fun updateTask(
        @Path("id") id: String,
        @Body request: UpdateTaskRequest
    ): Task

    @DELETE("tasks/{id}")
    suspend fun deleteTask(@Path("id") id: String)
}

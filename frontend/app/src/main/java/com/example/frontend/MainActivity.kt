package com.example.frontend

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.frontend.adapter.TaskAdapter
import com.example.frontend.api.ApiClient
import com.example.frontend.data.models.Task
import com.example.frontend.utils.AuthPreferences
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var fabAdd: FloatingActionButton
    private lateinit var logoutBtn: Button
    private lateinit var welcomeText: TextView
    private lateinit var taskAdapter: TaskAdapter
    private lateinit var authPreferences: AuthPreferences
    private val tasks = mutableListOf<Task>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        authPreferences = AuthPreferences(this)

        // Check if logged in
        if (!authPreferences.isLoggedIn()) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        recyclerView = findViewById(R.id.tasks_recycler)
        fabAdd = findViewById(R.id.fab_add)
        logoutBtn = findViewById(R.id.logout_btn)
        welcomeText = findViewById(R.id.welcome_text)

        welcomeText.text = "Welcome, ${authPreferences.getUsername()}"

        setupRecyclerView()
        setupListeners()
        loadTasks()
    }

    private fun setupRecyclerView() {
        taskAdapter = TaskAdapter(
            tasks,
            onEdit = { task -> editTask(task) },
            onDelete = { task -> deleteTask(task) },
            onToggleComplete = { task -> toggleComplete(task) }
        )
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = taskAdapter
    }

    private fun setupListeners() {
        fabAdd.setOnClickListener {
            startActivity(Intent(this, TaskFormActivity::class.java))
        }

        logoutBtn.setOnClickListener {
            authPreferences.clearAuth()
            ApiClient.setTokens(null, null)
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun loadTasks() {
        lifecycleScope.launch {
            try {
                val taskService = ApiClient.getTaskService()
                val response = taskService.getTasks()
                tasks.clear()
                tasks.addAll(response.data)
                taskAdapter.notifyDataSetChanged()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun editTask(task: Task) {
        val intent = Intent(this, TaskFormActivity::class.java)
        intent.putExtra("task_id", task.id)
        intent.putExtra("task_title", task.title)
        intent.putExtra("task_description", task.description)
        startActivity(intent)
    }

    private fun deleteTask(task: Task) {
        lifecycleScope.launch {
            try {
                val taskService = ApiClient.getTaskService()
                taskService.deleteTask(task.id)
                tasks.remove(task)
                taskAdapter.notifyDataSetChanged()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun toggleComplete(task: Task) {
        lifecycleScope.launch {
            try {
                val taskService = ApiClient.getTaskService()
                val updatedTask = taskService.updateTask(
                    task.id,
                    com.example.frontend.data.models.UpdateTaskRequest(
                        completed = !task.completed
                    )
                )
                val index = tasks.indexOfFirst { it.id == task.id }
                if (index >= 0) {
                    tasks[index] = updatedTask
                    taskAdapter.notifyItemChanged(index)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        loadTasks()
    }
}

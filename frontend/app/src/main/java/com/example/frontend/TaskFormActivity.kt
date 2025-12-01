package com.example.frontend

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.frontend.api.ApiClient
import com.example.frontend.data.models.CreateTaskRequest
import com.example.frontend.data.models.UpdateTaskRequest
import kotlinx.coroutines.launch

class TaskFormActivity : AppCompatActivity() {
    private lateinit var titleInput: EditText
    private lateinit var descriptionInput: EditText
    private lateinit var saveBtn: Button
    private lateinit var cancelBtn: Button

    private var taskId: String? = null
    private var isEditMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_form)

        titleInput = findViewById(R.id.title_input)
        descriptionInput = findViewById(R.id.description_input)
        saveBtn = findViewById(R.id.save_btn)
        cancelBtn = findViewById(R.id.cancel_btn)

        // Check if editing
        taskId = intent.getStringExtra("task_id")
        if (taskId != null) {
            isEditMode = true
            titleInput.setText(intent.getStringExtra("task_title"))
            descriptionInput.setText(intent.getStringExtra("task_description"))
            saveBtn.text = "Update"
        } else {
            saveBtn.text = "Create"
        }

        saveBtn.setOnClickListener { handleSave() }
        cancelBtn.setOnClickListener { finish() }
    }

    private fun handleSave() {
        val title = titleInput.text.toString().trim()
        val description = descriptionInput.text.toString().trim()

        if (title.isEmpty()) {
            Toast.makeText(this, "Title is required", Toast.LENGTH_SHORT).show()
            return
        }

        saveBtn.isEnabled = false

        lifecycleScope.launch {
            try {
                val taskService = ApiClient.getTaskService()

                if (isEditMode && taskId != null) {
                    // Update existing task
                    taskService.updateTask(
                        taskId!!,
                        UpdateTaskRequest(
                            title = title,
                            description = description
                        )
                    )
                    Toast.makeText(this@TaskFormActivity, "Task updated", Toast.LENGTH_SHORT).show()
                } else {
                    // Create new task
                    taskService.createTask(
                        CreateTaskRequest(
                            title = title,
                            description = description
                        )
                    )
                    Toast.makeText(this@TaskFormActivity, "Task created", Toast.LENGTH_SHORT).show()
                }

                // Return to task list
                startActivity(Intent(this@TaskFormActivity, TaskListActivity::class.java))
                finish()
            } catch (e: Exception) {
                Toast.makeText(
                    this@TaskFormActivity,
                    "Failed to save task: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
                saveBtn.isEnabled = true
            }
        }
    }
}

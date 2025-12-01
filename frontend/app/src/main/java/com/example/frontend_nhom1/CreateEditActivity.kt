package com.example.frontend_nhom1

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.frontend_nhom1.model.Task
import com.example.frontend_nhom1.network.ApiClient
import com.example.frontend_nhom1.network.TaskCreateRequest
import com.example.frontend_nhom1.network.TaskUpdateRequest
import com.example.frontend_nhom1.util.PreferencesHelper
import kotlinx.coroutines.launch

class CreateEditActivity : AppCompatActivity() {
    private lateinit var edtTitle: EditText
    private lateinit var edtDesc: EditText
    private lateinit var chkCompleted: CheckBox
    private lateinit var btnSave: Button
    private lateinit var progress: ProgressBar

    private var taskId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_edit)

        PreferencesHelper.init(this)

        edtTitle = findViewById(R.id.editTitle)
        edtDesc = findViewById(R.id.editDesc)
        chkCompleted = findViewById(R.id.chkCompleted)
        btnSave = findViewById(R.id.btnSave)
        progress = findViewById(R.id.saveProgress)

        taskId = intent.getStringExtra("task_id")
        if (taskId != null) {
            edtTitle.setText(intent.getStringExtra("task_title"))
            edtDesc.setText(intent.getStringExtra("task_desc"))
            chkCompleted.isChecked = intent.getBooleanExtra("task_completed", false)
        } else {
            chkCompleted.visibility = View.GONE
        }

        btnSave.setOnClickListener { save() }
    }

    private fun save() {
        val title = edtTitle.text.toString().trim()
        if (title.isEmpty()) {
            Toast.makeText(this, "Title is required", Toast.LENGTH_SHORT).show()
            return
        }
        progress.visibility = View.VISIBLE
        btnSave.isEnabled = false
        lifecycleScope.launch {
            try {
                val service = ApiClient.getService(this@CreateEditActivity)
                if (taskId == null) {
                    val req = TaskCreateRequest(title, edtDesc.text.toString())
                    val resp = service.createTask(req)
                    if (resp.isSuccessful) finish()
                    else Toast.makeText(this@CreateEditActivity, "Create failed", Toast.LENGTH_SHORT).show()
                } else {
                    val req = TaskUpdateRequest(title = title, description = edtDesc.text.toString(), completed = chkCompleted.isChecked)
                    val resp = service.updateTask(taskId!!, req)
                    if (resp.isSuccessful) finish()
                    else Toast.makeText(this@CreateEditActivity, "Update failed", Toast.LENGTH_SHORT).show()
                }
            } catch (ex: Exception) {
                Toast.makeText(this@CreateEditActivity, "Network error", Toast.LENGTH_SHORT).show()
            } finally {
                progress.visibility = View.GONE
                btnSave.isEnabled = true
            }
        }
    }
}

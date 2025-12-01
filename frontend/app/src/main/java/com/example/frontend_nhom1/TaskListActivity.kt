package com.example.frontend_nhom1

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.frontend_nhom1.adapter.TaskAdapter
import com.example.frontend_nhom1.model.Task
import com.example.frontend_nhom1.network.ApiClient
import com.example.frontend_nhom1.util.PreferencesHelper
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TaskListActivity : AppCompatActivity() {
    private lateinit var recycler: RecyclerView
    private lateinit var swipe: SwipeRefreshLayout
    private lateinit var fab: FloatingActionButton
    private lateinit var adapter: TaskAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_list)

        PreferencesHelper.init(this)

        recycler = findViewById(R.id.rvTasks)
        swipe = findViewById(R.id.swipe)
        fab = findViewById(R.id.fabAdd)

        adapter = TaskAdapter(onToggle = { t -> toggleTask(t) }, onClick = { t -> openEdit(t) }, onDelete = { t -> confirmDelete(t) })
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = adapter

        swipe.setOnRefreshListener { loadTasks() }
        fab.setOnClickListener { startActivity(Intent(this, CreateEditActivity::class.java)) }
    }

    override fun onResume() {
        super.onResume()
        loadTasks()
    }

    private fun loadTasks() {
        swipe.isRefreshing = true
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val resp = ApiClient.getService(this@TaskListActivity).getTasks()
                if (resp.isSuccessful) {
                    val list = resp.body()?.data ?: emptyList()
                    runOnUiThread { adapter.setData(list.toMutableList()) }
                } else {
                    runOnUiThread { Toast.makeText(this@TaskListActivity, "Error: ${resp.code()}", Toast.LENGTH_SHORT).show() }
                }
            } catch (ex: Exception) {
                runOnUiThread { Toast.makeText(this@TaskListActivity, "Network error", Toast.LENGTH_SHORT).show() }
            } finally {
                runOnUiThread { swipe.isRefreshing = false }
            }
        }
    }

    private fun toggleTask(task: Task) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val req = com.example.frontend_nhom1.network.TaskUpdateRequest(completed = !task.completed)
                val resp = ApiClient.getService(this@TaskListActivity).updateTask(task._id, req)
                if (resp.isSuccessful) loadTasks()
            } catch (_: Exception) {}
        }
    }

    private fun openEdit(task: Task) {
        val i = Intent(this, CreateEditActivity::class.java)
        i.putExtra("task_id", task._id)
        i.putExtra("task_title", task.title)
        i.putExtra("task_desc", task.description)
        i.putExtra("task_completed", task.completed)
        startActivity(i)
    }

    private fun confirmDelete(task: Task) {
        AlertDialog.Builder(this)
            .setTitle("Delete")
            .setMessage("Delete this task?")
            .setPositiveButton("Delete") { _, _ -> doDelete(task) }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun doDelete(task: Task) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val resp = ApiClient.getService(this@TaskListActivity).deleteTask(task._id)
                if (resp.isSuccessful) runOnUiThread { loadTasks() }
            } catch (_: Exception) {}
        }
    }
}

package com.example.frontend.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.frontend.R
import com.example.frontend.data.models.Task

class TaskAdapter(
    private val tasks: List<Task>,
    private val onEdit: (Task) -> Unit,
    private val onDelete: (Task) -> Unit,
    private val onToggleComplete: (Task) -> Unit
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleView: TextView = itemView.findViewById(R.id.task_title)
        private val descView: TextView = itemView.findViewById(R.id.task_description)
        private val completeCheckbox: CheckBox = itemView.findViewById(R.id.task_complete)
        private val editBtn: Button = itemView.findViewById(R.id.task_edit_btn)
        private val deleteBtn: Button = itemView.findViewById(R.id.task_delete_btn)

        fun bind(task: Task) {
            titleView.text = task.title
            descView.text = task.description.ifEmpty { "No description" }
            completeCheckbox.isChecked = task.completed

            if (task.completed) {
                titleView.alpha = 0.6f
                descView.alpha = 0.6f
            } else {
                titleView.alpha = 1f
                descView.alpha = 1f
            }

            completeCheckbox.setOnCheckedChangeListener { _, _ ->
                onToggleComplete(task)
            }

            editBtn.setOnClickListener {
                onEdit(task)
            }

            deleteBtn.setOnClickListener {
                onDelete(task)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.task_item, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(tasks[position])
    }

    override fun getItemCount(): Int = tasks.size
}

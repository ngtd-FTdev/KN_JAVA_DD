package com.example.frontend_nhom1.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.frontend_nhom1.R
import com.example.frontend_nhom1.model.Task

class TaskAdapter(
    private var items: MutableList<Task> = mutableListOf(),
    private val onToggle: (Task) -> Unit,
    private val onClick: (Task) -> Unit,
    private val onDelete: (Task) -> Unit
) : RecyclerView.Adapter<TaskAdapter.VH>() {

    fun setData(new: List<Task>) {
        items.clear()
        items.addAll(new)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val t = items[position]
        holder.title.text = t.title
        holder.desc.text = t.description ?: ""
        holder.checkbox.isChecked = t.completed
        holder.checkbox.setOnCheckedChangeListener { _, _ -> onToggle(t) }
        holder.itemView.setOnClickListener { onClick(t) }
        holder.itemView.setOnLongClickListener {
            onDelete(t)
            true
        }
    }

    override fun getItemCount(): Int = items.size

    class VH(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.taskTitle)
        val desc: TextView = view.findViewById(R.id.taskDesc)
        val checkbox: CheckBox = view.findViewById(R.id.taskCheckbox)
    }
}

package com.pam_228779.todoapppro.view.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.pam_228779.todoapppro.databinding.TaskItemBinding
import com.pam_228779.todoapppro.model.Task
import com.pam_228779.todoapppro.view.activity.TaskDetailActivity

class TaskListAdapter : ListAdapter<Task, TaskListAdapter.TaskViewHolder>(TasksComparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding = TaskItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = getItem(position)
        holder.bind(task)
    }

    inner class TaskViewHolder(private val binding: TaskItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private lateinit var task: Task

        init {
            binding.root.setOnClickListener {
                val context = binding.root.context
                val intent = Intent(context, TaskDetailActivity::class.java).apply {
                    putExtra("TASK_ID", task.id)
                }
                context.startActivity(intent)
            }
        }

        fun bind(task: Task) {
            this.task = task
            binding.taskItemTitle.text = task.title
            binding.taskItemCategory.text = task.category
            binding.taskItemDueTime.text = task.dueAt.toString()
            // Inicjalizacja innych pól
        }
    }


    class TasksComparator : DiffUtil.ItemCallback<Task>() {
        override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem == newItem
        }
    }
}
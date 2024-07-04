package com.pam_228779.todoapppro.view.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.pam_228779.todoapppro.R
import com.pam_228779.todoapppro.databinding.TaskItemBinding
import com.pam_228779.todoapppro.model.Task
import com.pam_228779.todoapppro.view.activity.TaskDetailActivity
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.Date
import java.util.Locale

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
            binding.taskItemDueTime.text = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(task.dueAt)
            if (task.hasAttachment) {
                binding.taskItemAttachmentIcon.visibility = View.VISIBLE
            } else {
                binding.taskItemAttachmentIcon.visibility = View.GONE
            }
            if (task.isCompleted) {
                binding.taskItemStatusIcon.setImageResource(R.drawable.round_done_24)
                binding.taskItemStatusIcon.visibility = View.VISIBLE
//                binding.taskItemTitle.setTextColor(Color.GREEN)
            } else if (task.dueAt.before(Date.from(Instant.now()))) {
                binding.taskItemStatusIcon.setImageResource(R.drawable.round_assignment_late_24)
                binding.taskItemStatusIcon.visibility = View.VISIBLE
//                binding.taskItemTitle.setTextColor(Color.RED)
            } else {
                binding.taskItemStatusIcon.visibility = View.GONE
            }
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
package com.pam_228779.todoapppro.view.activity

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.os.Bundle
import android.widget.SearchView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.pam_228779.todoapppro.databinding.ActivityMainBinding
import com.pam_228779.todoapppro.view.adapter.TaskListAdapter
import com.pam_228779.todoapppro.viewModel.TaskViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val taskViewModel: TaskViewModel by viewModels()
    private lateinit var taskListAdapter: TaskListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        taskListAdapter = TaskListAdapter()
        binding.tasksRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = taskListAdapter
        }

        taskViewModel.allTasks.observe(this) { tasks ->
            tasks?.let {
                taskListAdapter.submitList(it)
            }
        }

        binding.fab.setOnClickListener {
            val intent = Intent(this, AddTaskActivity::class.java)
            startActivity(intent)
        }

        createNotificationChannel()

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                filterTasks(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterTasks(newText)
                return true
            }
        })
    }

    private fun filterTasks(query: String?) {
        val filteredTasks = taskViewModel.allTasks.value?.filter {
            it.title.contains(query ?: "", ignoreCase = true)
        }
        taskListAdapter.submitList(filteredTasks)
    }

    private fun createNotificationChannel() {
        val name = "Task Reminder"
        val descriptionText = "Channel for task reminders"
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel("TASK_REMINDER_CHANNEL", name, importance).apply {
            description = descriptionText
        }
        val notificationManager: NotificationManager =
            getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
    }
}
package com.pam_228779.todoapppro.view.activity

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.SearchView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.pam_228779.todoapppro.R
import com.pam_228779.todoapppro.databinding.ActivityMainBinding
import com.pam_228779.todoapppro.view.adapter.TaskListAdapter
import com.pam_228779.todoapppro.viewModel.TaskViewModel

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val taskViewModel: TaskViewModel by viewModels()
    private lateinit var taskListAdapter: TaskListAdapter
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var preferenceChangeListener: SharedPreferences.OnSharedPreferenceChangeListener


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        PreferenceManager.setDefaultValues(this, R.xml.preferences, true)

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val categoriesToShow = sharedPreferences.getStringSet("categories_to_show", emptySet())
        Log.i(TAG, "Categories to show: $categoriesToShow")

        preferenceChangeListener = SharedPreferences.OnSharedPreferenceChangeListener { _, _ ->
            taskViewModel.updateFilteredTasks()
        }
        sharedPreferences.registerOnSharedPreferenceChangeListener(preferenceChangeListener)

        taskListAdapter = TaskListAdapter()
        binding.tasksRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = taskListAdapter
        }

        taskViewModel.filteredTasks.observe(this) { tasks ->
            tasks?.let {
                Log.i(TAG, "observe - tasks: $tasks")
                filterTasks(binding.searchView.query.toString())
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

        val filteredTasks = taskViewModel.filteredTasks.value?.filter {
            it.title.contains(query ?: "", ignoreCase = true)
        }
        Log.i(TAG, "filteredTask to taskListAdapter: $filteredTasks")
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
}
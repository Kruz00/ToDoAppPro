package com.pam_228779.todoapppro.viewModel

import android.app.Application
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import androidx.preference.PreferenceManager
import com.pam_228779.todoapppro.model.Task
import com.pam_228779.todoapppro.repository.AppDatabase
import com.pam_228779.todoapppro.repository.TaskRepository
import com.pam_228779.todoapppro.utils.cancelTaskReminder
import com.pam_228779.todoapppro.utils.scheduleTaskReminder
import kotlinx.coroutines.launch

class TaskViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: TaskRepository
    val allTasks: LiveData<List<Task>>
    val sharedPreferences: SharedPreferences

    init {
        val taskDao = AppDatabase.getDatabase(application).taskDao()
        repository = TaskRepository(taskDao)
        allTasks = repository.allTasks
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(application)
    }

    fun getTaskById(id: Int): LiveData<Task> {
        return repository.getTaskById(id)
    }

    fun getAllCategories(): LiveData<List<String>> {
        return repository.getAllCategories()
    }

    fun insert(task: Task) = viewModelScope.launch {
        repository.insert(task)
        if (task.isNotificationEnabled) {
            scheduleTaskReminder(task)
        }
    }

    fun update(task: Task) = viewModelScope.launch {
        repository.update(task)
        if (task.isNotificationEnabled) {
            scheduleTaskReminder(task)
        } else {
            cancelTaskReminder(task)
        }
    }

    fun delete(task: Task) = viewModelScope.launch {
        val taskCategory = task.category
        repository.delete(task)
        if(!repository.isCategoryExist(taskCategory)) {
            val categoriesToShow = mutableSetOf<String>().apply {
                addAll(sharedPreferences.getStringSet("categories_to_show", emptySet())!!)
            }
            categoriesToShow.remove(taskCategory)
            sharedPreferences.edit().putStringSet("categories_to_show", categoriesToShow).apply()
        }
        cancelTaskReminder(task)
    }

    private fun scheduleTaskReminder(task: Task) {
        val reminderOffsetMinutes = 0L
        Log.i("TaskViewModel", "Scheduling notification for $task")
//        scheduleTaskReminder(getApplication(), task, reminderOffsetMinutes)
    }

    private fun cancelTaskReminder(task: Task) {
        Log.i("TaskViewModel", "Cancel notification for $task")
//        cancelTaskReminder(getApplication(), task)
    }
}
package com.pam_228779.todoapppro.viewModel

import android.app.Application
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.viewModelScope
import androidx.preference.PreferenceManager
import com.pam_228779.todoapppro.model.Task
import com.pam_228779.todoapppro.repository.AppDatabase
import com.pam_228779.todoapppro.repository.TaskRepository
import com.pam_228779.todoapppro.utils.cancelTaskReminder
import com.pam_228779.todoapppro.utils.scheduleTaskReminder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val TAG = "TaskViewModel"

class TaskViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: TaskRepository
    private val sharedPreferences: SharedPreferences
    val allTasks: LiveData<List<Task>>
    private val _filteredTasks = MediatorLiveData<List<Task>>().apply { value = emptyList() }
    val filteredTasks: LiveData<List<Task>> get() = _filteredTasks

    init {
        val taskDao = AppDatabase.getDatabase(application).taskDao()
        repository = TaskRepository(taskDao)
        allTasks = repository.getTasks()
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(application)

        _filteredTasks.addSource(allTasks) {
            updateFilteredTasks()
        }
    }

    fun updateFilteredTasks() {
        viewModelScope.launch(Dispatchers.Main) {
            val hideCompleted = sharedPreferences.getBoolean("hide_completed_tasks", false)
            val categories = sharedPreferences.getStringSet("categories_to_show", emptySet()) ?: emptySet()

            val filteredList = allTasks.value?.filter { task ->
                (!hideCompleted || !task.isCompleted) && (categories.isEmpty() || categories.contains(task.category))
            } ?: emptyList()
            Log.i(TAG, "filteredList: ${filteredList}, allTasks: ${allTasks.value}")
            _filteredTasks.value = filteredList
        }

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

    // TODO reakcja na zmiane czasu powiadomien w shared preferences
    private fun scheduleTaskReminder(task: Task) {
        val reminderOffsetMinutes = 0L
        Log.i(TAG, "Scheduling notification for $task")
//        scheduleTaskReminder(getApplication(), task, reminderOffsetMinutes)
    }

    private fun cancelTaskReminder(task: Task) {
        Log.i(TAG, "Cancel notification for $task")
//        cancelTaskReminder(getApplication(), task)
    }
}
package com.pam_228779.todoapppro.repository

import androidx.lifecycle.LiveData
import com.pam_228779.todoapppro.model.Task

class TaskRepository(private val taskDao: TaskDao) {
    val allTasks: LiveData<List<Task>> = taskDao.getTasks()

    fun getTaskById(id: Int): LiveData<Task> {
        return taskDao.getTaskById(id)
    }

    fun getAllCategories(): LiveData<List<String>> {
        return taskDao.getAllCategories()
    }

    suspend fun isCategoryExist(category: String) : Boolean {
        return taskDao.isCategoryExist(category)
    }

    suspend fun insert(task: Task) {
        taskDao.insert(task)
    }

    suspend fun update(task: Task) {
        taskDao.update(task)
    }
    suspend fun delete(task: Task) {
        taskDao.delete(task)
    }
}
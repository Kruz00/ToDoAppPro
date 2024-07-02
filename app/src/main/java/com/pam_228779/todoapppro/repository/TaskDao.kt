package com.pam_228779.todoapppro.repository

import androidx.lifecycle.LiveData
import androidx.room.*
import com.pam_228779.todoapppro.model.Task

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks ORDER BY dueAt ASC")
    fun getTasks(): LiveData<List<Task>>

    @Query("SELECT * FROM tasks WHERE id = :id")
    fun getTaskById(id: Int): LiveData<Task>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: Task)

    @Update
    suspend fun update(task: Task)

    @Delete
    suspend fun delete(task: Task)
}
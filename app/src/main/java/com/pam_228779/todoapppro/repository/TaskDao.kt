package com.pam_228779.todoapppro.repository

import androidx.lifecycle.LiveData
import androidx.room.*
import com.pam_228779.todoapppro.model.Task

@Dao
interface TaskDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: Task)

    @Update
    suspend fun update(task: Task)

    @Delete
    suspend fun delete(task: Task)

    @Query("SELECT * FROM tasks WHERE isCompleted = :isCompleted ORDER BY dueAt ASC")
    fun getTasks(isCompleted: Boolean): LiveData<List<Task>>
}
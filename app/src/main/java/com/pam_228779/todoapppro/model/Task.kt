package com.pam_228779.todoapppro.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.File
import java.util.Date

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String,
    val createdAt: Date,
    val dueAt: Date,
    val isCompleted: Boolean = false,
    val isNotificationEnabled: Boolean = false,
    val category: String,
    val hasAttachment: Boolean = false,
    val attachmentUris: List<File> = emptyList(),
    val taskUniqueDir: String
)
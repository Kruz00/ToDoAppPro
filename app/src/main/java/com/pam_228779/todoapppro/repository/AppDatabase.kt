package com.pam_228779.todoapppro.repository

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.pam_228779.todoapppro.model.Task
import com.pam_228779.todoapppro.utils.DateConverter
import com.pam_228779.todoapppro.utils.UriListConverter

@Database(entities = [Task::class], version = 1)
@TypeConverters(DateConverter::class, UriListConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "task_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
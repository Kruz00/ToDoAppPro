package com.pam_228779.todoapppro.utils

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File

class FileListConverter {
    @TypeConverter
    fun fromFilesList(filesList: List<File>): String {
        val gson = Gson()
        return gson.toJson(
            filesList.map {
                it.absolutePath
            }
        )
    }

    @TypeConverter
    fun toFilesList(filesListString: String): List<File> {
        val gson = Gson()
        val type = object : TypeToken<List<String>>() {}.type
        val filesStringList: List<String> = gson.fromJson(filesListString, type)
        return filesStringList.map {
            File(it)
        }
    }
}
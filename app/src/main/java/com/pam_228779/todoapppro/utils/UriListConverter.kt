package com.pam_228779.todoapppro.utils

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class UriListConverter {
    @TypeConverter
    fun fromUriList(uriList: List<String>): String {
        val gson = Gson()
        return gson.toJson(uriList)
    }

    @TypeConverter
    fun toUriList(uriListString: String): List<String> {
        val gson = Gson()
        val type = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(uriListString, type)
    }
}
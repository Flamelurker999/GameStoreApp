package com.example.gamestoreapp.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * Конвертеры для преобразования сложных типов данных в простые типы,
 * которые могут быть сохранены в базе данных и обратно.
 */
class Converters {

    /**
     * Преобразует список строк (List<String>) в строку JSON.
     *
     * @param value Список строк, который необходимо сохранить в базе данных.
     * @return Строка JSON, представляющая список строк.
     */
    @TypeConverter
    fun fromGalleryList(value: List<String>): String {
        return Gson().toJson(value)
    }

    /**
     * Преобразует строку JSON обратно в список строк (List<String>).
     *
     * @param value Строка JSON, извлеченная из базы данных.
     * @return Список строк, восстановленный из JSON.
     */
    @TypeConverter
    fun toGalleryList(value: String): List<String> {
        val listType = object : TypeToken<List<String>>() {}.type
        return Gson().fromJson(value, listType)
    }
}

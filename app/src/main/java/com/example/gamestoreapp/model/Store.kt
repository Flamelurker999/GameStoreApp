package com.example.gamestoreapp.model

// Модель данных для представления магазина в приложении

data class Store(
    val id: Int, // Уникальный идентификатор магазина
    val name: String, // Название магазина
    val metro: String?,  // Название ближайшей станции метро (может быть null, если информация отсутствует)
    val address: String // Адрес магазина
)

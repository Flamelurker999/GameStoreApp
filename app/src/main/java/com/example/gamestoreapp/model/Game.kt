package com.example.gamestoreapp.model

import java.io.Serializable

// Модель данных для представления игры в приложении
// Объект Serializable для передачи между компонентами приложения

data class Game(
    val id: Int, // Уникальный идентификатор игры
    val name: String, // Название игры
    val description: String, // Описание игры
    val price: Double, // Текущая цена игры
    val oldPrice: Double?, // Перечеркнутая цена (может быть null, если скидки нет)
    val imageUrl: String, // Ссылка на изображение игры
    val releaseDate: String, // Дата выпуска игры в формате "YYYY-MM-DD"
    val platform: String, // Платформа: "PS5", "Xbox", "PC" и т.д.
    val genre: String, // Жанр игры (например, "RPG", "Action")
    val publisher: String, // Издатель игры
    val gallery: List<String>, // Список ссылок на изображения (галерея)
    val age: Int, // Возрастное ограничение (например, 18+)
    var inCart: Boolean = false // Флаг для отслеживания, добавлена ли игра в корзину
) : Serializable

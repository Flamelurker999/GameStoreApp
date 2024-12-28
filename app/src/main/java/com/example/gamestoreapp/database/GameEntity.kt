package com.example.gamestoreapp.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.gamestoreapp.model.Game

// Определение таблицы базы данных для хранения информации об играх
@Entity(tableName = "games")
data class GameEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0, // Уникальный идентификатор игры, генерируется автоматически
    val name: String, // Название игры
    val description: String, // Описание игры
    val price: Double, // Текущая цена игры
    val oldPrice: Double?, // Старая цена игры, если имеется (может быть null)
    val imageUrl: String, // URL изображения обложки игры
    val releaseDate: String, // Дата выпуска игры
    val platform: String, // Платформа, на которой доступна игра
    val genre: String, // Жанр игры
    val publisher: String, // Издатель игры
    val gallery: List<String>, // Список URL изображений, связанных с игрой (галерея)
    val age: Int = 0, // Возрастное ограничение игры (по умолчанию 0)
    var inCart: Boolean = false // Поле для отслеживания, добавлена ли игра в корзину
)

// Преобразование объекта GameEntity в объект Game
fun GameEntity.toGame(): Game {
    return Game(
        id = this.id, // Копируем ID из сущности базы данных
        name = this.name, // Копируем название игры
        description = this.description, // Копируем описание игры
        price = this.price, // Копируем текущую цену
        oldPrice = this.oldPrice, // Копируем старую цену, если имеется
        imageUrl = this.imageUrl, // Копируем URL изображения обложки
        releaseDate = this.releaseDate, // Копируем дату выпуска
        platform = this.platform, // Копируем платформу игры
        genre = this.genre, // Копируем жанр игры
        publisher = this.publisher, // Копируем информацию об издателе
        gallery = this.gallery, // Копируем список URL изображений (галерею)
        age = this.age, // Копируем возрастное ограничение
        inCart = this.inCart // Копируем статус добавления игры в корзину
    )
}


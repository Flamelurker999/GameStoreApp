package com.example.gamestoreapp.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.gamestoreapp.model.Store

// Определение таблицы базы данных для хранения информации о магазинах
@Entity(tableName = "stores")
data class StoreEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0, // Уникальный идентификатор магазина, генерируется автоматически
    val name: String, // Название магазина
    val metro: String?, // Название ближайшей станции метро (может быть null, если нет информации)
    val address: String // Адрес магазина
)

// Преобразование объекта StoreEntity в объект Store
fun StoreEntity.toStore(): Store {
    return Store(
        id = this.id, // Копируем ID из сущности базы данных
        name = this.name, // Копируем название магазина
        metro = this.metro, // Копируем название ближайшей станции метро, если имеется
        address = this.address // Копируем адрес магазина
    )
}

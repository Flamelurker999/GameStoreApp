package com.example.gamestoreapp.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

// Data Access Object (DAO) для работы с таблицей "stores" в базе данных
@Dao
interface StoreDao {

    // Вставка списка объектов StoreEntity в таблицу "stores"
    // При конфликте (например, по первичному ключу) старые записи заменяются новыми
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertStores(stores: List<StoreEntity>)

    // Получение всех записей из таблицы "stores"
    @Query("SELECT * FROM stores")
    fun getAllStores(): List<StoreEntity>
}

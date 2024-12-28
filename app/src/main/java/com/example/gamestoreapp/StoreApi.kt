package com.example.gamestoreapp

import com.example.gamestoreapp.database.StoreEntity
import retrofit2.Response
import retrofit2.http.GET

// Интерфейс API для взаимодействия с сервером для получения данных о магазинах
interface StoreApi {
    @GET("stores") // HTTP-запрос GET для получения списка магазинов
    suspend fun getStores(): Response<List<StoreEntity>> // Возвращает ответ с данными списка магазинов
}

package com.example.gamestoreapp

import com.example.gamestoreapp.database.GameEntity
import retrofit2.Response
import retrofit2.http.GET

// Интерфейс API для взаимодействия с сервером для получения данных об играх
interface GameApi {
    @GET("games") // HTTP-запрос GET для получения списка игр
    suspend fun getGames(): Response<List<GameEntity>> // Возвращает ответ с данными списка игр
}

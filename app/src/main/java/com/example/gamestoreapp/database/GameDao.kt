package com.example.gamestoreapp.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface GameDao {

    // Вставка одной игры. Если игра с таким ID уже существует, заменяет её
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertGame(game: GameEntity)

    // Обновление статуса "в корзине" для указанной игры
    @Query("UPDATE games SET inCart = :inCart WHERE id = :gameId")
    fun updateInCartStatus(gameId: Int, inCart: Boolean)

    // Вставка списка игр. Конфликт по ID решается заменой
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertGames(games: List<GameEntity>)

    // Получение списка игр, добавленных в корзину
    @Query("SELECT * FROM games WHERE inCart = 1")
    fun getGamesInCart(): List<GameEntity>

    // Получение всех игр с использованием LiveData (для автоматического обновления UI)
    @Query("SELECT * FROM games")
    fun getAllGamesLive(): LiveData<List<GameEntity>>

    // Синхронное получение всех игр (без LiveData)
    @Query("SELECT * FROM games")
    fun getAllGames(): List<GameEntity>

    // Получение игр со скидкой с использованием LiveData. Ограничено 5 записями
    @Query("SELECT * FROM games WHERE oldPrice IS NOT NULL LIMIT 5")
    fun getDiscountedGamesLive(): LiveData<List<GameEntity>>

    // Синхронное получение игр со скидкой. Ограничено 5 записями
    @Query("SELECT * FROM games WHERE oldPrice IS NOT NULL LIMIT 5")
    fun getDiscountedGames(): List<GameEntity>

    // Получение игры по её ID с использованием LiveData
    @Query("SELECT * FROM games WHERE id = :gameId")
    fun getGameByIdLive(gameId: Int): LiveData<GameEntity>

    // Синхронное получение игры по её ID
    @Query("SELECT * FROM games WHERE id = :gameId")
    fun getGameById(gameId: Int): GameEntity?

    // Удаление указанной игры из базы данных
    @Delete
    fun deleteGame(game: GameEntity)

    // Очистка всей таблицы игр. Возвращает количество удалённых записей
    @Query("DELETE FROM games")
    fun clearGames(): Int

    // Получение игр, доступных для указанной платформы
    @Query("SELECT * FROM games WHERE platform = :platform")
    fun getGamesByPlatform(platform: String): LiveData<List<GameEntity>>

    // Получение игр, выпущенных за последние три месяца, с использованием LiveData
    @Query("SELECT * FROM games WHERE releaseDate >= DATE('now', '-3 months')")
    fun getRecentGamesLive(): LiveData<List<GameEntity>>

    // Получение игр, соответствующих указанной платформе и жанру
    @Query("SELECT * FROM games WHERE platform = :platform AND genre = :genre")
    fun getGamesByPlatformAndGenre(platform: String, genre: String): List<GameEntity>
}

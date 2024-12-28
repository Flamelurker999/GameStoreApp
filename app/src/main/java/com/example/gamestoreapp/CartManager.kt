package com.example.gamestoreapp

import android.content.Context
import com.example.gamestoreapp.database.GameDatabase
import com.example.gamestoreapp.database.toGame
import com.example.gamestoreapp.model.Game
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// Объект для управления товарами в корзине
object CartManager {
    private val cartItems: MutableList<Game> = mutableListOf() // Локальный список товаров в корзине

    // Добавление игры в корзину
    fun addToCart(context: Context, game: Game) {
        if (!cartItems.contains(game)) { // Проверяем, если игры еще нет в корзине
            cartItems.add(game) // Добавляем игру в локальный список
            game.inCart = true // Устанавливаем флаг inCart
            // Обновляем состояние inCart в базе данных
            CoroutineScope(Dispatchers.IO).launch {
                val database = GameDatabase.getInstance(context)
                database.gameDao().updateInCartStatus(game.id, true)
            }
        }
    }

    // Проверка, находится ли игра в корзине
    fun isInCart(gameId: Int): Boolean {
        return cartItems.any { it.id == gameId } // Проверяем наличие ID игры в локальном списке
    }

    // Подсчет общей стоимости товаров в корзине
    fun getTotalPrice(): Double {
        return cartItems.sumOf { it.price } // Суммируем цену всех товаров в корзине
    }

    // Получение списка товаров в корзине
    fun getCartItems(): List<Game> {
        return cartItems // Возвращаем копию локального списка
    }

    // Очистка корзины
    fun clearCart(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            // Сохраняем копию текущих товаров в корзине
            val itemsToClear = cartItems.toList()

            // Очищаем локальный список
            cartItems.clear()

            /// Обновляем состояние inCart для всех товаров в базе данных
            val database = GameDatabase.getInstance(context)
            itemsToClear.forEach { game ->
                database.gameDao().updateInCartStatus(game.id, false)
            }
        }
    }

    // Удаление игры из корзины по её ID
    fun removeFromCart(context: Context, gameId: Int) {
        val game = cartItems.find { it.id == gameId } // Ищем игру в локальном списке
        game?.let {
            cartItems.remove(it) // Удаляем игру из локального списка
            it.inCart = false // Сбрасываем флаг inCart
            // Обновляем состояние inCart в базе данных
            CoroutineScope(Dispatchers.IO).launch {
                val database = GameDatabase.getInstance(context)
                database.gameDao().updateInCartStatus(gameId, false)
            }
        }
    }

    // Загрузка товаров в корзину из базы данных
    fun loadCartItems(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            val database = GameDatabase.getInstance(context)
            val cachedItems = database.gameDao().getGamesInCart() // Получаем товары из базы данных
            cartItems.clear() // Очищаем локальный список
            cartItems.addAll(cachedItems.map { it.toGame() }) // Преобразуем и добавляем в локальный список
        }
    }

}

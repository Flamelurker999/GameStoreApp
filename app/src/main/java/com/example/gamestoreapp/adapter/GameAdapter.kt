package com.example.gamestoreapp.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.gamestoreapp.CartActivity
import com.example.gamestoreapp.CartManager
import com.example.gamestoreapp.PriceUtils
import com.example.gamestoreapp.R
import com.example.gamestoreapp.model.Game

/**
 * Адаптер для отображения списка игр
 * Используется для отображения информации о каждой игре и взаимодействия с пользователем (например, добавление в корзину).
 */
class GameAdapter(
    private val games: List<Game>, // Список игр для отображения
    private val onCardClick: (Game) -> Unit // Обработчик клика на карточку игры
) : RecyclerView.Adapter<GameAdapter.GameViewHolder>() {

    /**
     * ViewHolder отвечает за отображение одного элемента списка
     * Содержит ссылки на элементы макета для изображения, названия, цен и кнопки "В корзину".
     */
    class GameViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val gameImage: ImageView = itemView.findViewById(R.id.gameImage) // Изображение игры
        val gameName: TextView = itemView.findViewById(R.id.gameName) // Название игры
        val gameOldPrice: TextView =
            itemView.findViewById(R.id.gameOldPrice) // Старая цена (если есть)
        val gamePrice: TextView = itemView.findViewById(R.id.gamePrice) // Актуальная цена
        val addToCartButton: Button =
            itemView.findViewById(R.id.addToCartButton) // Кнопка "Добавить в корзину"
    }

    /**
     * Создание нового ViewHolder
     * Вызывается, когда RecyclerView нуждается в создании нового элемента.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_home_game, parent, false)
        return GameViewHolder(view)
    }

    /**
     * Привязка данных к ViewHolder
     * Вызывается при прокрутке списка для отображения информации о конкретной игре.
     */
    override fun onBindViewHolder(holder: GameViewHolder, position: Int) {
        val game = games[position] // Получаем текущую игру
        holder.gameName.text = game.name // Устанавливаем название игры

        // Установка цен с обработкой старой цены (если она доступна)
        PriceUtils.setPrice(
            context = holder.itemView.context,
            priceTextView = holder.gamePrice,
            oldPriceTextView = holder.gameOldPrice,
            price = game.price,
            oldPrice = game.oldPrice
        )


        // Загрузка изображения игры
        val context = holder.itemView.context
        val resourceId =
            context.resources.getIdentifier(game.imageUrl, "drawable", context.packageName)
        if (resourceId != 0) {
            // Если изображение найдено в ресурсах
            holder.gameImage.setImageResource(resourceId)
        } else {
            // Если изображение отсутствует, используем Glide для загрузки из сети
            Glide.with(context)
                .load(game.imageUrl)
                .placeholder(R.drawable.placeholder_image) // Загрузчик по умолчанию
                .error(R.drawable.error_image) // Изображение при ошибке загрузки
                .into(holder.gameImage)
        }

        // Проверяем, находится ли игра в корзине, чтобы обновить состояние кнопки
        if (CartManager.isInCart(game.id)) {
            holder.addToCartButton.text = "В корзине"
            val colorStateList = ContextCompat.getColorStateList(context, R.color.button_in_cart)
            holder.addToCartButton.backgroundTintList = colorStateList
        } else {
            holder.addToCartButton.text = "В корзину"
            val colorStateList = ContextCompat.getColorStateList(context, R.color.primary_color)
            holder.addToCartButton.backgroundTintList = colorStateList
        }

        // Обработка клика на кнопку "Добавить в корзину" или "В корзине"
        holder.addToCartButton.setOnClickListener {
            if (!CartManager.isInCart(game.id)) {
                // Если игра не в корзине, добавляем её
                CartManager.addToCart(holder.itemView.context, game)
                game.inCart = true

                // Обновляем текст и цвет кнопки
                holder.addToCartButton.text = "В корзине"
                val colorStateList =
                    ContextCompat.getColorStateList(holder.itemView.context, R.color.button_in_cart)
                holder.addToCartButton.backgroundTintList = colorStateList
            } else {
                // Если игра уже в корзине, открываем экран корзины
                val intent = Intent(holder.itemView.context, CartActivity::class.java)
                holder.itemView.context.startActivity(intent)

                // Применяем анимацию перехода
                (holder.itemView.context as? Activity)?.overridePendingTransition(
                    R.anim.slide_in_up,
                    R.anim.slide_out_down
                )
            }
        }

        // Обработка клика на карточку игры
        holder.itemView.setOnClickListener {
            onCardClick(game) // Вызов обработчика, переданного через конструктор адаптера
        }
    }

    /**
     * Возвращает общее количество элементов в списке
     */
    override fun getItemCount(): Int = games.size
}

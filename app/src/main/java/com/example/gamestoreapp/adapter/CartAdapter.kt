package com.example.gamestoreapp.adapter

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.gamestoreapp.PriceUtils
import com.example.gamestoreapp.R
import com.example.gamestoreapp.model.Game

class CartAdapter(
    private val items: List<Game>, // Список товаров в корзине
    private val onRemoveItem: (Game) -> Unit, // Callback для удаления товара
    private val onItemClick: (Game) -> Unit // Callback для обработки клика по товару
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    // ViewHolder, отвечает за отображение одного элемента в RecyclerView
    class CartViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val gameImage: ImageView = view.findViewById(R.id.cartItemImage) // Изображение товара
        val gameName: TextView = view.findViewById(R.id.cartItemName) // Название товара
        val gamePlatform: TextView = view.findViewById(R.id.cartItemPlatform) // Платформа товара
        val gamePrice: TextView = view.findViewById(R.id.cartItemPrice) // Цена товара
        val gameOldPrice: TextView =
            view.findViewById(R.id.cartItemOldPrice) // Старая цена (если есть скидка)
        val removeIcon: ImageView =
            view.findViewById(R.id.cartItemRemoveIcon) // Иконка для удаления товара

    }

    // Создание ViewHolder для нового элемента
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cart_game, parent, false) // Используем макет item_cart_game
        return CartViewHolder(view)
    }

    // Привязка данных к ViewHolder
    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val game = items[position] // Получаем текущий товар

        holder.gameName.text = game.name // Устанавливаем название товара
        holder.gamePlatform.text = game.platform // Устанавливаем платформу

        // Обработка клика по элементу (например, открытие карточки товара)
        holder.itemView.setOnClickListener {
            onItemClick(game)
        }

        // Устанавливаем цену и старую цену (если есть)
        PriceUtils.setPrice(
            context = holder.itemView.context,
            priceTextView = holder.gamePrice,
            oldPriceTextView = holder.gameOldPrice,
            price = game.price,
            oldPrice = game.oldPrice
        )

        // Отображение старой цены, если она есть
        holder.gameOldPrice.apply {
            if (game.oldPrice != null && game.oldPrice > 0) {
                visibility = View.VISIBLE
                text = "${game.oldPrice.toInt()} ₽" // Форматируем цену в рублях
                paintFlags = paintFlags or Paint.STRIKE_THRU_TEXT_FLAG // Перечеркиваем старую цену
                setTextColor(
                    ContextCompat.getColor(
                        holder.itemView.context, android.R.color.darker_gray
                    )
                ) // Устанавливаем цвет текста
            } else {
                visibility = View.GONE // Прячем старую цену, если её нет
            }
        }


        // Загрузка изображения товара
        val context = holder.itemView.context
        val resourceId =
            context.resources.getIdentifier(game.imageUrl, "drawable", context.packageName)
        if (resourceId != 0) {
            holder.gameImage.setImageResource(resourceId) // Используем локальный ресурс
        } else {
            // Загружаем изображение через Glide (для удалённых URL)
            Glide.with(context).load(game.imageUrl)
                .placeholder(R.drawable.placeholder_image) // Заглушка при загрузке
                .error(R.drawable.error_image) // Изображение при ошибке
                .into(holder.gameImage)
        }

        // Удаление товара из корзины по нажатию на иконку
        holder.removeIcon.setOnClickListener {
            onRemoveItem(game) // Вызываем callback для удаления товара
        }
    }

    // Возвращает количество элементов в списке
    override fun getItemCount(): Int = items.size
}


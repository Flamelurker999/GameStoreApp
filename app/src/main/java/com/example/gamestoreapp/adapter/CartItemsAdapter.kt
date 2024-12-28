package com.example.gamestoreapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gamestoreapp.R
import com.example.gamestoreapp.model.Game

/**
 * Адаптер для отображения списка товаров в корзине
 * Используется для отображения имени и цены каждого товара.
 */
class CartItemsAdapter(private val items: List<Game>) :
    RecyclerView.Adapter<CartItemsAdapter.CartItemViewHolder>() {

    /**
     * ViewHolder отвечает за отображение одного элемента списка
     * Содержит ссылки на TextView для имени товара и его цены.
     */
    class CartItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView =
            itemView.findViewById(R.id.itemTextView) // Поле для отображения названия товара
        val priceTextView: TextView =
            itemView.findViewById(R.id.itemPriceTextView) // Поле для отображения цены
    }

    /**
     * Создание нового ViewHolder при прокрутке списка
     * Используется макет item_cart для отображения каждого элемента.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_cart, parent, false)
        return CartItemViewHolder(view)
    }

    /**
     * Привязка данных к ViewHolder
     * Устанавливает название и цену для текущего элемента списка.
     */
    override fun onBindViewHolder(holder: CartItemViewHolder, position: Int) {
        val item = items[position] // Получаем текущий товар из списка
        holder.nameTextView.text = item.name // Устанавливаем название игры
        holder.priceTextView.text = "${(item.price).toInt()}₽" // Устанавливаем цену (целое число)
    }

    /**
     * Возвращает общее количество элементов в списке
     * Используется для определения длины списка RecyclerView.
     */
    override fun getItemCount(): Int {
        return items.size
    }
}

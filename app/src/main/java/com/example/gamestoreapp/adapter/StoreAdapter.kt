package com.example.gamestoreapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gamestoreapp.R
import com.example.gamestoreapp.model.Store

/**
 * Адаптер для отображения списка магазинов.
 *
 * @param stores Список объектов `Store`, представляющих магазины.
 * @param onStoreClick Лямбда-функция, вызываемая при клике на магазин.
 */
class StoreAdapter(private val stores: List<Store>, private val onStoreClick: (Store) -> Unit) :
    RecyclerView.Adapter<StoreAdapter.StoreViewHolder>() {

    /**
     * Создаёт новый ViewHolder для элемента списка.
     * Вызывается, когда RecyclerView нуждается в новом элементе для отображения.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoreViewHolder {
        // Создание макета элемента из XML-файла `item_store`
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_store, parent, false)
        return StoreViewHolder(view)
    }

    /**
     * Привязывает данные к ViewHolder.
     * Устанавливает информацию о магазине в соответствующие виджеты.
     */
    override fun onBindViewHolder(holder: StoreViewHolder, position: Int) {
        val store = stores[position]

        // Установка данных магазина
        holder.nameTextView.text = store.name // Название магазина
        holder.metroTextView.text = store.metro ?: "Метро не указано" // Ближайшее метро (если есть)
        holder.addressTextView.text = store.address // Адрес магазина

        // Обработка клика на элемент списка
        holder.itemView.setOnClickListener { onStoreClick(store) }
    }

    /**
     * Возвращает общее количество элементов в списке.
     */
    override fun getItemCount(): Int = stores.size

    /**
     * ViewHolder для хранения ссылок на виджеты элемента списка.
     * Оптимизирует доступ к элементам макета.
     */
    class StoreViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView =
            itemView.findViewById(R.id.storeNameTextView) // Название магазина
        val metroTextView: TextView = itemView.findViewById(R.id.storeMetroTextView) // Метро
        val addressTextView: TextView = itemView.findViewById(R.id.storeAddressTextView) // Адрес
    }
}

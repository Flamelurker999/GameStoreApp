package com.example.gamestoreapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gamestoreapp.R

/**
 * Адаптер для отображения списка игровых платформ.
 *
 * @param platforms Список платформ для отображения.
 * @param onPlatformClick Обработчик клика по элементу платформы.
 */
class PlatformAdapter(
    private val platforms: List<Platform>,
    private val onPlatformClick: (Platform) -> Unit
) : RecyclerView.Adapter<PlatformAdapter.PlatformViewHolder>() {

    /**
     * Data class для хранения информации о платформе.
     *
     * @param name Название платформы.
     * @param imageResId Идентификатор ресурса изображения платформы.
     */
    data class Platform(val name: String, val imageResId: Int)

    /**
     * Создаёт новый ViewHolder для элемента платформы.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlatformViewHolder {
        // Инфлейтим макет для элемента из XML-файла item_platform
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_platform, parent, false)
        return PlatformViewHolder(view)
    }

    /**
     * Привязывает данные платформы к ViewHolder.
     *
     * @param holder ViewHolder для текущего элемента.
     * @param position Позиция элемента в списке.
     */
    override fun onBindViewHolder(holder: PlatformViewHolder, position: Int) {
        val platform = platforms[position] // Получаем платформу по текущей позиции
        holder.bind(platform) // Привязываем данные платформы к ViewHolder
    }

    /**
     * Возвращает общее количество элементов в списке.
     */
    override fun getItemCount(): Int = platforms.size

    /**
     * ViewHolder для отображения одного элемента платформы.
     * Содержит ссылку на `ImageView` и `TextView`.
     */
    inner class PlatformViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val platformImageView: ImageView =
            itemView.findViewById(R.id.platformImageView) // Изображение платформы
        private val platformTextView: TextView =
            itemView.findViewById(R.id.platformTextView) // Название платформы

        /**
         * Привязывает данные платформы к ViewHolder.
         *
         * @param platform Объект платформы для отображения.
         */
        fun bind(platform: Platform) {
            // Устанавливаем название платформы
            platformTextView.text = platform.name

            // Устанавливаем изображение платформы
            platformImageView.setImageResource(platform.imageResId)

            // Обработка клика по элементу платформы
            itemView.setOnClickListener {
                onPlatformClick(platform)
            }
        }
    }
}

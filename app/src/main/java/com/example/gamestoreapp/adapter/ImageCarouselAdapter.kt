package com.example.gamestoreapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.gamestoreapp.R

/**
 * Адаптер для отображения изображений в карусели.
 *
 * @param context Контекст для доступа к ресурсам и загрузки изображений.
 * @param images Список URL-адресов или имён ресурсов изображений.
 */
class ImageCarouselAdapter(
    private val context: Context,
    private val images: List<String>
) : RecyclerView.Adapter<ImageCarouselAdapter.ImageViewHolder>() {

    /**
     * ViewHolder для отображения одного изображения.
     * Содержит ссылку на `ImageView` для отображения картинки.
     */
    inner class ImageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView =
            view.findViewById(R.id.carouselImageView) // ImageView для отображения изображения
    }

    /**
     * Создаёт новый ViewHolder.
     * Вызывается, когда RecyclerView нуждается в новом элементе.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        // Создаём макет для элемента карусели из XML-файла carousel_item
        val view = LayoutInflater.from(context).inflate(R.layout.carousel_item, parent, false)
        return ImageViewHolder(view)
    }

    /**
     * Привязывает данные к ViewHolder.
     * Устанавливает изображение для текущей позиции.
     */
    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val imageResource = images[position] // Текущий URL или имя ресурса изображения

        // Проверяем, является ли ресурс локальным (в папке drawable)
        val resourceId =
            context.resources.getIdentifier(imageResource, "drawable", context.packageName)
        if (resourceId != 0) {
            // Если ресурс найден, устанавливаем его в ImageView
            holder.imageView.setImageResource(resourceId)
        } else {
            // Если ресурс отсутствует, загружаем изображение с помощью Glide
            Glide.with(context)
                .load(imageResource) // Загрузка изображения из URL
                .placeholder(R.drawable.placeholder_image) // Плейсхолдер для загрузки
                .error(R.drawable.error_image) // Изображение для ошибки загрузки
                .into(holder.imageView)
        }
    }

    /**
     * Возвращает общее количество изображений в карусели.
     */
    override fun getItemCount(): Int = images.size
}

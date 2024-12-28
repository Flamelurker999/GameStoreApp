package com.example.gamestoreapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gamestoreapp.R

/**
 * Адаптер для отображения списка жанров
 * Позволяет отображать жанры в виде карточек и обрабатывать клики по ним.
 *
 * @param genres Список жанров, которые нужно отобразить.
 * @param onGenreClick Лямбда-функция для обработки нажатий на жанр.
 */
class GenreAdapter(
    private val genres: List<String>, // Список жанров
    private val onGenreClick: (String) -> Unit // Обработчик кликов на жанры
) : RecyclerView.Adapter<GenreAdapter.GenreViewHolder>() {

    /**
     * ViewHolder отвечает за отображение одного элемента списка
     * Содержит ссылку на карточку жанра и текст внутри неё.
     */
    inner class GenreViewHolder(val cardView: View) : RecyclerView.ViewHolder(cardView) {
        val genreText: TextView = cardView.findViewById(R.id.genreText) // Текстовое поле для жанра
    }

    /**
     * Создаёт новый ViewHolder
     * Вызывается, когда RecyclerView нуждается в новом элементе.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenreViewHolder {
        // Создаём макет для элемента списка из XML-файла item_genre
        val cardView = LayoutInflater.from(parent.context).inflate(
            R.layout.item_genre, parent, false
        )
        return GenreViewHolder(cardView)
    }

    /**
     * Привязывает данные к ViewHolder
     * Устанавливает текст жанра и обрабатывает клики на карточку.
     */
    override fun onBindViewHolder(holder: GenreViewHolder, position: Int) {
        val genre = genres[position] // Текущий жанр
        holder.genreText.text = genre // Устанавливаем название жанра в текстовое поле

        // Обрабатываем клик на карточке
        holder.cardView.setOnClickListener {
            onGenreClick(genre) // Вызываем переданную функцию с текущим жанром
        }
    }

    /**
     * Возвращает количество элементов в списке
     * Определяет размер списка жанров, который нужно отобразить.
     */
    override fun getItemCount() = genres.size
}


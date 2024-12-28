package com.example.gamestoreapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gamestoreapp.adapter.GenreAdapter

// Экран выбора жанра игры
class GenreActivity : AppCompatActivity() {

    private lateinit var platform: String // Переменная для хранения выбранной платформы

    override fun onCreate(savedInstanceState: Bundle?) {
        // Устанавливаем светлую тему
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_genre)

        // Обработка нажатия кнопки "Назад"
        onBackPressedDispatcher.addCallback(this) {
            finish()
            overridePendingTransition(
                R.anim.slide_in_left,
                R.anim.slide_out_right
            ) // Анимация перехода
        }

        // Получаем переданную платформу из Intent
        platform = intent.getStringExtra("platform") ?: ""

        // Настраиваем Toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // Включаем кнопку "Назад"
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_large) // Устанавливаем иконку "Назад"
        toolbar.setNavigationOnClickListener {
            finish()
            overridePendingTransition(
                R.anim.slide_in_left,
                R.anim.slide_out_right
            ) // Анимация перехода
        }

        // Устанавливаем заголовок Toolbar с указанием выбранной платформы
        supportActionBar?.title = "Выберите жанр ($platform)"
        toolbar.setBackgroundColor(
            ContextCompat.getColor(
                this,
                android.R.color.white
            )
        ) // Устанавливаем белый фон

        // Список жанров
        val genres = listOf(
            "RPG",
            "Спорт",
            "Симуляторы",
            "Ужасы",
            "Экшен",
            "Файтинг",
            "Приключения",
            "Гонки"
        )
        val genreRecyclerView = findViewById<RecyclerView>(R.id.genreRecyclerView)
        genreRecyclerView.layoutManager = LinearLayoutManager(this) // Линейный менеджер компоновки
        genreRecyclerView.adapter = GenreAdapter(genres) { genre ->
            openListingActivity(genre) // Переход к экрану списка игр
        }
    }

    // Переход к экрану списка игр выбранного жанра
    private fun openListingActivity(genre: String) {
        val intent = Intent(this, ListingActivity::class.java).apply {
            putExtra("platform", platform) // Передаем платформу
            putExtra("genre", genre) // Передаем выбранный жанр
        }
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left) // Анимация перехода
    }

    override fun onBackPressed() {
        super.onBackPressed()
        // Добавляем анимацию перехода при возврате назад
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }
}


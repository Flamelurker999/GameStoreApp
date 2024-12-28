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
import com.example.gamestoreapp.adapter.PlatformAdapter

// Экран каталога платформ
class CatalogActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        // Устанавливаем светлую тему
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_catalog)

        // Обработка нажатия кнопки "Назад"
        onBackPressedDispatcher.addCallback(this) {
            // Выполняем действия при нажатии "Назад"
            finish()
            overridePendingTransition(
                R.anim.slide_in_up,
                R.anim.slide_out_down
            ) // Анимация перехода
        }

        // Инициализация Toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Настраиваем кнопку "Назад"
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // Включаем кнопку "Назад"
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_large) // Устанавливаем иконку "Назад"

        // Обработка нажатия на кнопку "Назад"
        toolbar.setNavigationOnClickListener {
            finish()
            overridePendingTransition(
                R.anim.slide_in_up,
                R.anim.slide_out_down
            ) // Анимация перехода
        }

        // Устанавливаем заголовок и фон Toolbar
        supportActionBar?.title = "Выберите платформу" // Устанавливаем заголовок
        toolbar.setBackgroundColor(
            ContextCompat.getColor(
                this,
                android.R.color.white
            )
        ) // Устанавливаем белый фон

        // Настройка RecyclerView для отображения списка платформ
        val platformRecyclerView = findViewById<RecyclerView>(R.id.platformRecyclerView)
        platformRecyclerView.layoutManager =
            LinearLayoutManager(this) // Линейный менеджер компоновки

        // Данные для списка платформ
        val platforms = listOf(
            PlatformAdapter.Platform("PS5", R.drawable.ic_ps5), // Платформа PS5
            PlatformAdapter.Platform("Xbox", R.drawable.ic_xbox), // Платформа Xbox
            PlatformAdapter.Platform("PC", R.drawable.ic_pc) // Платформа PC
        )

        // Инициализация адаптера для отображения платформ
        val adapter = PlatformAdapter(platforms) { platform ->
            openGenreActivity(platform.name) // Переход к экрану жанров
        }
        platformRecyclerView.adapter = adapter
    }

    // Переход к экрану жанров
    private fun openGenreActivity(platform: String) {
        val intent = Intent(this, GenreActivity::class.java).apply {
            putExtra("platform", platform) // Передаем выбранную платформу
        }
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left) // Анимация перехода
    }

    override fun onBackPressed() {
        super.onBackPressed()
        // Добавляем анимацию перехода при возврате назад
        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_down)
    }

}

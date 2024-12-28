package com.example.gamestoreapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gamestoreapp.adapter.GameAdapter
import com.example.gamestoreapp.database.GameDatabase
import com.example.gamestoreapp.database.toGame
import com.example.gamestoreapp.model.Game
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// Экран списка игр для выбранной платформы и жанра
class ListingActivity : AppCompatActivity() {

    private lateinit var platform: String // Выбранная платформа
    private lateinit var genre: String // Выбранный жанр

    override fun onCreate(savedInstanceState: Bundle?) {
        // Устанавливаем светлую тему
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_listing)

        // Обработка нажатия кнопки "Назад"
        onBackPressedDispatcher.addCallback(this) {
            finish()
            overridePendingTransition(
                R.anim.slide_in_left,
                R.anim.slide_out_right
            ) // Анимация перехода
        }

        // Получаем данные из Intent
        platform = intent.getStringExtra("platform") ?: ""
        genre = intent.getStringExtra("genre") ?: ""

        // Настраиваем Toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // Включаем кнопку "Назад"
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_large) // Устанавливаем иконку "Назад"
        supportActionBar?.title =
            "$platform - $genre" // Устанавливаем заголовок с платформой и жанром
        toolbar.setBackgroundColor(
            ContextCompat.getColor(
                this,
                android.R.color.white
            )
        ) // Белый фон тулбара

        toolbar.setNavigationOnClickListener {
            finish()
            overridePendingTransition(
                R.anim.slide_in_left,
                R.anim.slide_out_right
            ) // Анимация перехода
        }

        // Настройка RecyclerView
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager =
            GridLayoutManager(this, 2) // Отображение в виде сетки с 2 столбцами

        // Загрузка данных для RecyclerView
        loadGames(recyclerView)
    }

    // Загрузка списка игр для выбранной платформы и жанра
    private fun loadGames(recyclerView: RecyclerView) {
        CoroutineScope(Dispatchers.IO).launch {
            // Получаем игры из базы данных
            val games = GameDatabase.getInstance(this@ListingActivity).gameDao()
                .getGamesByPlatformAndGenre(platform, genre)
                .map { it.toGame() } // Преобразуем сущности базы данных в объекты Game

            CoroutineScope(Dispatchers.Main).launch {
                if (games.isNotEmpty()) {
                    // Если игры найдены, устанавливаем адаптер
                    recyclerView.adapter = GameAdapter(
                        games = games,
                        onCardClick = { game ->
                            openGameDetails(game) // Переход на экран деталей игры
                        }
                    )
                } else {
                    // Если игры не найдены, показываем сообщение
                    Toast.makeText(
                        this@ListingActivity,
                        "Игры для $platform и жанра $genre не найдены.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    // Переход на экран карточки товара
    private fun openGameDetails(game: Game) {
        val intent = Intent(this, ProductDetailsActivity::class.java).apply {
            putExtra("game", game) // Передаем объект Game в Intent
        }
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left) // Анимация перехода
    }

    override fun onBackPressed() {
        super.onBackPressed()
        // Анимация перехода при возврате назад
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }
}

package com.example.gamestoreapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.GridLayoutManager
import com.example.gamestoreapp.adapter.GameAdapter
import com.example.gamestoreapp.database.GameDatabase
import com.example.gamestoreapp.database.toGame
import com.example.gamestoreapp.databinding.ActivitySearchResultsBinding
import com.example.gamestoreapp.model.Game
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// Экран отображения результатов поиска
class SearchResultsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchResultsBinding // ViewBinding для работы с элементами интерфейса
    private val queryHistory = ArrayDeque<String>() // Стек для хранения истории запросов
    private lateinit var currentQuery: String // Текущий поисковый запрос

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO) // Устанавливаем светлую тему
        super.onCreate(savedInstanceState)

        // Инициализация View Binding
        binding = ActivitySearchResultsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Настройка OnBackPressedDispatcher для обработки жеста "Назад"
        onBackPressedDispatcher.addCallback(this) {
            finish()
            overridePendingTransition(
                R.anim.slide_in_up,
                R.anim.slide_out_down
            ) // Анимация перехода
        }

        // Обработка кнопки "Назад" для возврата к предыдущему запросу
        binding.backButton.setOnClickListener {
            queryHistory.removeLast() // Удаляем последний запрос из истории

            val previousQuery = queryHistory.lastOrNull() // Получаем предыдущий запрос
            if (previousQuery != null) {
                binding.searchQueryInput.setText(previousQuery) // Устанавливаем предыдущий запрос в поле ввода
                performSearch(previousQuery) // Выполняем поиск
            } else {
                finish() // Завершаем активность, если запросов больше нет
                overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_down)
            }
        }

        // Скрытие клавиатуры при нажатии вне поля ввода
        binding.rootLayout.setOnTouchListener { _, _ ->
            hideKeyboard()
            binding.rootLayout.requestFocus() // Снимаем фокус с EditText
            true
        }

        // Получаем запрос из Intent
        val query = intent.getStringExtra("query") ?: ""
        currentQuery = query
        binding.searchQueryInput.setText(query) // Устанавливаем запрос в поле ввода


        // Изменение цвета выделения текста
        val highlightColor = ContextCompat.getColor(this, R.color.primary_color)
        binding.searchQueryInput.highlightColor = highlightColor

        // Показ кнопки очистки, если поле ввода не пустое
        updateClearButtonVisibility()

        // Обработка кнопки "Очистить"
        binding.clearSearchButton.setOnClickListener {
            binding.searchQueryInput.text.clear() // Очищаем поле ввода
            binding.clearSearchButton.visibility = View.GONE // Скрываем кнопку очистки
        }

        /// Выполняем поиск при наличии запроса
        if (query.isNotEmpty()) {
            performSearch(query)
        }

        // Устанавливаем обработчик для клавиши Enter
        binding.searchQueryInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE) {
                val searchQuery = binding.searchQueryInput.text.toString().trim()
                currentQuery = searchQuery
                performSearch(searchQuery) // Выполняем поиск
                hideKeyboard() // Скрываем клавиатуру
                true
            } else {
                false
            }
        }

        // Обновляем видимость кнопки очистки при вводе текста
        binding.searchQueryInput.addTextChangedListener {
            updateClearButtonVisibility()
        }

        // Настройка нижней панели навигации
        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    finish() // Возврат на главный экран
                    true
                }

                R.id.nav_catalog -> {
                    startActivity(Intent(this, CatalogActivity::class.java))
                    overridePendingTransition(
                        R.anim.slide_in_up,
                        R.anim.slide_out_down
                    ) // Анимация перехода
                    finish()
                    true
                }

                R.id.nav_cart -> {
                    startActivity(Intent(this, CartActivity::class.java))
                    overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_down)
                    finish()
                    true
                }

                else -> false
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Выполняем повторный поиск при возвращении на экран
        if (::currentQuery.isInitialized && currentQuery.isNotEmpty()) {
            performSearch(currentQuery)
        }
    }

    // Обновление видимости кнопки "Очистить"
    private fun updateClearButtonVisibility() {
        binding.clearSearchButton.visibility =
            if (binding.searchQueryInput.text.isNullOrEmpty()) View.GONE else View.VISIBLE
    }

    // Выполнение поиска по запросу
    private fun performSearch(query: String) {
        // Проверяем, чтобы запрос не был пустым
        if (query.isEmpty()) {
            Toast.makeText(this, "Введите текст для поиска", Toast.LENGTH_SHORT).show()
            return
        }

        // Добавляем запрос в историю, если он отличается от предыдущего
        if (queryHistory.isEmpty() || queryHistory.lastOrNull() != query) {
            queryHistory.addLast(query)
        }

        CoroutineScope(Dispatchers.IO).launch {
            val games = GameDatabase.getInstance(this@SearchResultsActivity).gameDao().getAllGames()
                .filter { it.name.contains(query, ignoreCase = true) } // Фильтрация игр по запросу
                .map { it.toGame() } // Преобразование сущностей базы данных в объекты Game

            CoroutineScope(Dispatchers.Main).launch {
                if (games.isNotEmpty()) {
                    val declension = getDeclension(games.size) // Склонение слова "товар"
                    binding.searchResultText.text =
                        "По запросу \"$query\" найдено ${games.size} $declension"
                    binding.searchResultsRecyclerView.adapter = GameAdapter(
                        games = games,
                        onCardClick = { game ->
                            openGameDetails(game) // Переход на карточку товара
                        }
                    )
                    binding.searchResultsRecyclerView.layoutManager =
                        GridLayoutManager(this@SearchResultsActivity, 2) // Отображение в виде сетки
                } else {
                    binding.searchResultText.text = "По запросу \"$query\" ничего не найдено :("
                    binding.searchResultsRecyclerView.adapter = null
                }
            }
        }
    }

    // Открытие экрана карточки товара
    private fun openGameDetails(game: Game) {
        val intent = Intent(this, ProductDetailsActivity::class.java).apply {
            putExtra("game", game) // Передаем объект Game
        }
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }

    // Скрытие клавиатуры
    private fun hideKeyboard() {
        val inputMethodManager =
            getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        currentFocus?.let {
            inputMethodManager.hideSoftInputFromWindow(it.windowToken, 0)
        }
    }

    // Склонение слова "товар" в зависимости от количества
    private fun getDeclension(count: Int): String {
        return when {
            count % 10 == 1 && count % 100 != 11 -> "товар"
            count % 10 in 2..4 && (count % 100 !in 12..14) -> "товара"
            else -> "товаров"
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        queryHistory.clear() // Очищаем историю запросов при уничтожении активности
    }

    override fun onBackPressed() {
        super.onBackPressed()
        // Анимация перехода при возврате назад
        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_down)
    }
}



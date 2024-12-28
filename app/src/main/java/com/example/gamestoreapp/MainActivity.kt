package com.example.gamestoreapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.bottomnavigation.BottomNavigationView

// Главный экран приложения, управляющий навигацией и стартовыми действиями
class MainActivity : AppCompatActivity() {


    private lateinit var bottomNavigationView: BottomNavigationView // Нижняя панель навигации

    override fun onCreate(savedInstanceState: Bundle?) {
        // Устанавливаем светлую тему
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Загружаем данные корзины при старте приложения
        CartManager.loadCartItems(this)


        // Инициализация BottomNavigationView
        bottomNavigationView = findViewById(R.id.bottomNavigationView)

        // Устанавливаем стартовый фрагмент (главная страница)
        if (savedInstanceState == null) {
            setFragment(HomeFragment()) // Отображаем HomeFragment при первом запуске
            bottomNavigationView.selectedItemId = R.id.nav_home // Устанавливаем активный элемент
        }

        // Обработка нажатий на элементы нижней панели навигации
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> true // Главная страница уже активна
                R.id.nav_catalog -> {
                    // Переход к каталогу
                    startActivity(Intent(this, CatalogActivity::class.java))
                    overridePendingTransition(
                        R.anim.slide_in_up,
                        R.anim.slide_out_down
                    ) // Анимация перехода
                    true
                }

                R.id.nav_cart -> {
                    // Переход к корзине
                    val intent = Intent(this, CartActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(
                        R.anim.slide_in_up,
                        R.anim.slide_out_down
                    ) // Анимация перехода
                    true
                }

                else -> false
            }
        }
    }

    override fun onResume() {
        super.onResume()

        // Устанавливаем активный элемент навигации в зависимости от переданного значения
        val activeTabId = intent.getIntExtra("active_tab", R.id.nav_home)
        bottomNavigationView.selectedItemId = activeTabId
    }


    /**
     * Утилитный метод для замены текущего фрагмента
     * @param fragment Новый фрагмент для отображения
     */
    private fun setFragment(fragment: androidx.fragment.app.Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.nav_host_fragment, fragment) // Замена фрагмента
            .commit()
    }
}

package com.example.gamestoreapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gamestoreapp.adapter.CartAdapter
import com.example.gamestoreapp.model.Game
import com.google.android.material.bottomnavigation.BottomNavigationView

// Экран корзины
class CartActivity : AppCompatActivity() {

    private lateinit var cartAdapter: CartAdapter // Адаптер для списка товаров в корзине
    private lateinit var recyclerView: RecyclerView // RecyclerView для отображения списка игр
    private lateinit var totalPriceTextView: TextView // Текстовое поле для отображения общей стоимости
    private lateinit var checkoutButton: Button // Кнопка оформления заказа


    override fun onCreate(savedInstanceState: Bundle?) {
        // Устанавливаем светлую тему для активности
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        // Обработка нажатия кнопки "Назад"
        onBackPressedDispatcher.addCallback(this) {
            finish() // Завершаем активность
            overridePendingTransition(
                R.anim.slide_in_up,
                R.anim.slide_out_down
            ) // Анимация перехода
        }

        // Настройка Toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Корзина" // Устанавливаем заголовок для Toolbar

        // Настройка RecyclerView
        recyclerView = findViewById(R.id.recyclerViewCart)
        recyclerView.layoutManager =
            LinearLayoutManager(this) // Устанавливаем линейный менеджер компоновки

        // Инициализация адаптера для отображения списка товаров
        cartAdapter = CartAdapter(
            CartManager.getCartItems(), // Получаем список товаров из корзины
            onItemClick = { game -> openProductDetails(game) }, // Обработка клика по товару
            onRemoveItem = { game -> removeGameFromCart(game) } // Обработка удаления товара
        )
        recyclerView.adapter = cartAdapter


        // Настройка отображения общей стоимости
        totalPriceTextView = findViewById(R.id.textViewTotalPrice)
        updateTotalPrice() // Обновляем текст с общей стоимостью

        // Настройка кнопки оформления заказа
        checkoutButton = findViewById(R.id.buttonCheckout)
        checkoutButton.setOnClickListener {
            val cartItems = CartManager.getCartItems()

            val intent = Intent(this, RecipientActivity::class.java).apply {
                putExtra(
                    "cart_items",
                    ArrayList(cartItems)
                ) // Передаем список товаров в следующую активность
            }
            startActivity(intent)
            overridePendingTransition(
                R.anim.slide_in_right,
                R.anim.slide_out_left
            ) // Анимация перехода
        }

        // Настройка Bottom Navigation View
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.selectedItemId =
            R.id.nav_cart // Устанавливаем активный элемент навигации

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    val intent = Intent(this, MainActivity::class.java)
                    intent.putExtra("active_tab", R.id.nav_home)
                    startActivity(intent)
                    overridePendingTransition(
                        R.anim.slide_in_up,
                        R.anim.slide_out_down
                    ) // Анимация перехода
                    true
                }

                R.id.nav_catalog -> {
                    val intent = Intent(this, CatalogActivity::class.java)
                    intent.putExtra("active_tab", R.id.nav_catalog)
                    startActivity(intent)
                    overridePendingTransition(
                        R.anim.slide_in_up,
                        R.anim.slide_out_down
                    ) // Анимация перехода
                    true
                }

                R.id.nav_cart -> true // Текущая активность
                else -> false
            }
        }

        // Проверяем, есть ли товары в корзине, чтобы активировать/деактивировать кнопку оформления заказа
        updateCheckoutButtonState()
    }

    // Удаление игры из корзины
    private fun removeGameFromCart(game: Game) {
        CartManager.removeFromCart(this, game.id) // Удаляем товар из корзины по ID
        cartAdapter.notifyDataSetChanged()       // Уведомляем адаптер об изменениях
        updateTotalPrice()                       // Обновляем общую стоимость
        updateCheckoutButtonState()             // Обновляем состояние кнопки оформления заказа
    }

    // Обновление текста общей стоимости
    private fun updateTotalPrice() {
        val totalPriceTextView = findViewById<TextView>(R.id.textViewTotalPrice)
        totalPriceTextView.text =
            "Общая стоимость: ${(CartManager.getTotalPrice()).toInt()} ₽" // Обновляем текст
    }

    // Обновление состояния кнопки оформления заказа
    private fun updateCheckoutButtonState() {
        val hasItemsInCart = CartManager.getCartItems().isNotEmpty()
        checkoutButton.isEnabled = hasItemsInCart // Активируем кнопку, если корзина не пуста
    }

    override fun onResume() {
        super.onResume()

        // Обновляем список товаров в адаптере
        cartAdapter.updateItems(CartManager.getCartItems())

        // Уведомляем адаптер об изменениях данных
        cartAdapter.notifyDataSetChanged()

        // Обновляем текст общей стоимости
        updateTotalPrice()

        // Обновляем состояние кнопки при возврате на экран
        updateCheckoutButtonState()


        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        val activeTabId = intent.getIntExtra("active_tab", R.id.nav_cart)
        bottomNavigationView.selectedItemId = activeTabId // Устанавливаем выбранную вкладку
    }

    // Переход к экрану деталей товара
    private fun openProductDetails(game: Game) {
        val intent = Intent(this, ProductDetailsActivity::class.java).apply {
            putExtra("game", game) // Передаем объект Game в следующую активность
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

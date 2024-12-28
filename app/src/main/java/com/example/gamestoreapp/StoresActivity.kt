package com.example.gamestoreapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.addCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gamestoreapp.adapter.StoreAdapter
import com.example.gamestoreapp.database.GameDatabase
import com.example.gamestoreapp.database.toStore
import com.example.gamestoreapp.databinding.ActivityStoresBinding
import com.example.gamestoreapp.model.Game
import com.example.gamestoreapp.model.Store
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// Экран выбора магазина
class StoresActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStoresBinding // ViewBinding для упрощенной работы с элементами интерфейса

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO) // Устанавливаем светлую тему
        binding = ActivityStoresBinding.inflate(layoutInflater)
        setContentView(binding.root)

        onBackPressedDispatcher.addCallback(this) {
            // Обработка кнопки "Назад"
            finish()
            overridePendingTransition(
                R.anim.slide_in_left,
                R.anim.slide_out_right
            ) // Анимация перехода
        }

        // Получение данных о получателе из Intent
        val recipientName = intent.getStringExtra("recipient_name") ?: "Не указано"
        val recipientPhone = intent.getStringExtra("recipient_phone") ?: "Не указано"


        // Настройка Toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // Включаем кнопку "Назад"
        binding.toolbar.setNavigationIcon(R.drawable.ic_arrow_back_large) // Устанавливаем иконку "Назад"
        binding.toolbar.setNavigationOnClickListener {
            finish()
            overridePendingTransition(
                R.anim.slide_in_left,
                R.anim.slide_out_right
            ) // Анимация перехода
        }
        binding.toolbar.setBackgroundColor(
            ContextCompat.getColor(
                this,
                android.R.color.white
            )
        ) // Белый фон тулбара

        // Загрузка списка магазинов
        loadStores()
    }

    // Метод для загрузки магазинов из базы данных
    private fun loadStores() {
        CoroutineScope(Dispatchers.IO).launch {
            val stores = GameDatabase.getInstance(this@StoresActivity).storeDao().getAllStores()
                .map { it.toStore() } // Преобразуем сущности базы данных в объекты Store

            withContext(Dispatchers.Main) {
                // Настройка RecyclerView для отображения списка магазинов
                binding.storesRecyclerView.layoutManager = LinearLayoutManager(this@StoresActivity)
                binding.storesRecyclerView.adapter = StoreAdapter(stores) { store ->
                    showStoreDialog(store) // Показать диалог выбора магазина
                }
            }
        }
    }

    // Показ диалогового окна для подтверждения выбора магазина
    private fun showStoreDialog(store: Store) {
        val dialog = AlertDialog.Builder(this)
            .setTitle("Выбрать магазин")
            .setMessage("Выбрать магазин \"${store.name}\"?")
            .setPositiveButton("Выбрать") { _, _ ->
                openNextScreen(store) // Переход к следующему экрану
            }
            .setNegativeButton("Отменить", null) // Кнопка отмены
            .create()

        dialog.show() // Отображение диалога
    }

    // Переход к экрану оформления заказа (CheckoutActivity)
    private fun openNextScreen(store: Store) {
        // Чтение данных о получателе из SharedPreferences
        val cartItems =
            intent.getSerializableExtra("cart_items") as? ArrayList<Game> // Список товаров из корзины
        val sharedPreferences = getSharedPreferences("RecipientPrefs", Context.MODE_PRIVATE)
        val recipientName = sharedPreferences.getString("name", "") ?: "Не указано"
        val recipientPhone = sharedPreferences.getString("phone", "") ?: "Не указано"

        // Передача данных в CheckoutActivity
        val intent = Intent(this, CheckoutActivity::class.java).apply {
            putExtra("cart_items", cartItems)
            putExtra("store_name", store.name)
            putExtra("store_address", store.address)
            putExtra("recipient_name", recipientName)
            putExtra("recipient_phone", recipientPhone)
        }
        startActivity(intent) // Запуск CheckoutActivity
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left) // Анимация перехода
    }

    override fun onBackPressed() {
        super.onBackPressed()
        // Анимация перехода при возврате назад
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }
}

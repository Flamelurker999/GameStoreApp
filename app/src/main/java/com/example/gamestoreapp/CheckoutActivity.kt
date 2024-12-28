package com.example.gamestoreapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gamestoreapp.adapter.CartItemsAdapter
import com.example.gamestoreapp.databinding.ActivityCheckoutBinding
import com.example.gamestoreapp.model.Game
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

// Экран чекаута
class CheckoutActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCheckoutBinding // Используем ViewBinding для удобного доступа к UI элементам

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO) // Устанавливаем светлую тему
        binding = ActivityCheckoutBinding.inflate(layoutInflater)
        setContentView(binding.root) // Устанавливаем макет активности

        // Обработка нажатия кнопки "Назад"
        onBackPressedDispatcher.addCallback(this) {
            // Выполняем действия при нажатии "Назад"
            finish()
            overridePendingTransition(
                R.anim.slide_in_left,
                R.anim.slide_out_right
            ) // Анимация перехода
        }

        // Получение данных из Intent
        val storeName = intent.getStringExtra("store_name") ?: "Не выбран магазин"
        val storeAddress = intent.getStringExtra("store_address") ?: "Адрес не указан"
        val recipientName = intent.getStringExtra("recipient_name") ?: "Имя не указано"
        val recipientPhone = intent.getStringExtra("recipient_phone") ?: "Телефон не указан"
        val totalPrice = CartManager.getTotalPrice().toInt()

        // Получение списка товаров из корзины
        val cartItems = CartManager.getCartItems()


        // Настройка Toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // Включаем кнопку "Назад"
        binding.toolbar.setNavigationIcon(R.drawable.ic_arrow_back_large) // Иконка "Назад"
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
        ) // Устанавливаем белый фон

        // Заполнение данных в интерфейсе
        binding.storeInfo.text = "$storeName\n$storeAddress" // Информация о магазине
        binding.recipientInfo.text = "$recipientName\n$recipientPhone" // Информация о получателе

        // Настройка RecyclerView для отображения товаров в корзине
        binding.cartItemsRecyclerView.layoutManager =
            LinearLayoutManager(this) // Линейный менеджер компоновки
        binding.cartItemsRecyclerView.adapter =
            CartItemsAdapter(cartItems) // Адаптер для списка товаров

        // Установка итоговой суммы
        binding.totalPriceValue.text = "$totalPrice ₽"

        // Обработка нажатия кнопки "Заказать"
        binding.orderButton.setOnClickListener {
            val orderDetails = createOrderJson(
                storeName, storeAddress, recipientName, recipientPhone, totalPrice, cartItems
            )

            sendOrderRequest(orderDetails) // Отправляем запрос на сервер
        }
    }

    // Формирование JSON-объекта для отправки заказа
    private fun createOrderJson(
        storeName: String,
        storeAddress: String,
        recipientName: String,
        recipientPhone: String,
        totalPrice: Int,
        cartItems: List<Game>
    ): String {
        val json = JSONObject()
        json.put("store_name", storeName)
        json.put("store_address", storeAddress)
        json.put("recipient_name", recipientName)
        json.put("recipient_phone", recipientPhone)
        json.put("total_price", totalPrice)

        val itemsArray = JSONArray()
        cartItems.forEach { game ->
            val itemJson = JSONObject()
            itemJson.put("name", game.name)
            itemJson.put("price", game.price)
            itemsArray.put(itemJson)
        }
        json.put("items", itemsArray)

        return json.toString()
    }

    // Отправка POST-запроса для оформления заказа
    private fun sendOrderRequest(orderJson: String) {
        val client = OkHttpClient()

        val requestBody = orderJson.toRequestBody("application/json".toMediaTypeOrNull())
        val request = Request.Builder()
            .url("https://example.com/api/submit_order") // URL сервера
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // Обработка ошибки запроса
                runOnUiThread {
                    Toast.makeText(
                        this@CheckoutActivity,
                        "Ошибка при оформлении заказа",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                // Обработка ответа от сервера
                if (response.isSuccessful || response.code == 405) {
                    runOnUiThread {
                        Toast.makeText(
                            this@CheckoutActivity,
                            "Заказ успешно оформлен!",
                            Toast.LENGTH_LONG
                        ).show()
                        // Очищаем корзину
                        CartManager.clearCart(this@CheckoutActivity)

                        // Переход на экран корзины
                        val intent = Intent(this@CheckoutActivity, CartActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    // Логика после успешного ответа
                } else {
                    runOnUiThread {
                        Toast.makeText(
                            this@CheckoutActivity,
                            "Ошибка: ${response.code}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        })
    }

    override fun onBackPressed() {
        super.onBackPressed()
        // Добавляем анимацию перехода при возврате назад
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }
}

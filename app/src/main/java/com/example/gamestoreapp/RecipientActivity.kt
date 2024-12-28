package com.example.gamestoreapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import com.example.gamestoreapp.model.Game

// Экран ввода данных получателя
class RecipientActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO) // Устанавливаем светлую тему
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipient)

        onBackPressedDispatcher.addCallback(this) {
            // Обработка нажатия кнопки "Назад"
            finish()
            overridePendingTransition(
                R.anim.slide_in_left,
                R.anim.slide_out_right
            ) // Анимация перехода
        }

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
        toolbar.setBackgroundColor(
            ContextCompat.getColor(
                this,
                android.R.color.white
            )
        ) // Устанавливаем белый фон


        // Элементы интерфейса
        val nameInput = findViewById<EditText>(R.id.nameInput) // Поле ввода имени
        val emailInput = findViewById<EditText>(R.id.emailInput) // Поле ввода электронной почты
        val continueButton = findViewById<Button>(R.id.continueButton) // Кнопка "Продолжить"
        val phoneInput = findViewById<EditText>(R.id.phoneInput) // Поле ввода номера телефона

        // Изменение цвета выделения текста
        val highlightColor = ContextCompat.getColor(this, R.color.primary_color)
        phoneInput.highlightColor = highlightColor
        nameInput.highlightColor = highlightColor
        emailInput.highlightColor = highlightColor

        // Устанавливаем TextWatcher для автоматического форматирования номера телефона
        phoneInput.addTextChangedListener(PhoneNumberTextWatcher())


        // Загружаем сохраненные данные, если они существуют
        loadFromCache(nameInput, phoneInput, emailInput)

        // Обработка нажатия кнопки "Продолжить"
        continueButton.setOnClickListener {
            val name = nameInput.text.toString().trim() // Читаем введенное имя
            val phone = phoneInput.text.toString().trim() // Читаем введенный номер телефона
            val email = emailInput.text.toString().trim() // Читаем введенный email

            if (validateInputs(name, phone, email)) {
                saveToCache(name, phone, email) // Сохраняем данные в кэш
                openNextScreen() // Переходим на следующий экран
            }
        }
    }

    // Проверка введенных данных
    private fun validateInputs(name: String, phone: String, email: String): Boolean {
        if (name.isEmpty()) {
            showToast("Введите имя") // Показываем сообщение об ошибке
            return false
        }
        if (phone.length != 18) { // Проверяем длину номера телефона
            showToast("Введите корректный номер телефона")
            return false
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) { // Проверяем формат email
            showToast("Введите корректный адрес электронной почты")
            return false
        }
        return true
    }

    // Сохранение данных в SharedPreferences
    private fun saveToCache(name: String, phone: String, email: String) {
        val sharedPreferences = getSharedPreferences("RecipientPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("name", name)
        editor.putString("phone", phone)
        editor.putString("email", email)
        editor.apply() // Применяем изменения
    }

    // Загрузка данных из SharedPreferences
    private fun loadFromCache(nameInput: EditText, phoneInput: EditText, emailInput: EditText) {
        val sharedPreferences = getSharedPreferences("RecipientPrefs", Context.MODE_PRIVATE)
        val name = sharedPreferences.getString("name", "") // Читаем сохраненное имя
        val phone = sharedPreferences.getString("phone", "") // Читаем сохраненный номер телефона
        val email = sharedPreferences.getString("email", "") // Читаем сохраненный email

        nameInput.setText(name) // Устанавливаем имя в поле ввода
        phoneInput.setText(phone) // Устанавливаем номер телефона в поле ввода
        emailInput.setText(email) // Устанавливаем email в поле ввода
    }

    // Переход на следующий экран
    private fun openNextScreen() {
        // Чтение данных из SharedPreferences
        val cartItems =
            intent.getSerializableExtra("cart_items") as? ArrayList<Game> // Получаем список товаров из корзины
        val sharedPreferences = getSharedPreferences("RecipientPrefs", Context.MODE_PRIVATE)
        val name = sharedPreferences.getString("name", "") ?: "" // Читаем имя
        val phone = sharedPreferences.getString("phone", "") ?: "" // Читаем номер телефона

        // Передача данных в StoresActivity
        val intent = Intent(this, StoresActivity::class.java).apply {
            putExtra("cart_items", cartItems)
            putExtra("recipient_name", name)
            putExtra("recipient_phone", phone)
        }
        startActivity(intent) // Открытие экрана StoresActivity
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left) // Анимация перехода
    }

    // Показ сообщения пользователю
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        // Анимация перехода при возврате назад
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }
}

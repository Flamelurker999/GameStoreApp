package com.example.gamestoreapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.example.gamestoreapp.adapter.ImageCarouselAdapter
import com.example.gamestoreapp.databinding.ActivityProductDetailsBinding
import com.example.gamestoreapp.model.Game
import java.text.SimpleDateFormat
import java.util.Locale

// Экран отображения карточки товара
class ProductDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProductDetailsBinding // ViewBinding для упрощения работы с UI элементами
    private var isInCart = false // Флаг для проверки, добавлен ли товар в корзину
    private var game: Game? = null // Объект Game для отображения деталей

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO) // Устанавливаем светлую тему
        super.onCreate(savedInstanceState)

        onBackPressedDispatcher.addCallback(this) {
            // Обработка нажатия кнопки "Назад"
            finish()
            overridePendingTransition(
                R.anim.slide_in_left,
                R.anim.slide_out_right
            ) // Анимация перехода
        }

        binding = ActivityProductDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root) // Устанавливаем макет активности

        // Получение объекта Game из Intent
        game = intent.getSerializableExtra("game") as? Game

        if (game == null) {
            // Если данные игры отсутствуют, завершаем активность
            Toast.makeText(this, "Ошибка загрузки данных", Toast.LENGTH_SHORT).show()
            finish()
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
            return
        }

        // Проверяем, добавлен ли товар в корзину
        isInCart = CartManager.isInCart(game!!.id)

        // Настройка кнопки "В корзину"
        val addToCartButton = binding.addToCartButton
        if (isInCart) {
            addToCartButton.text = "В корзине"
            val colorStateList = ContextCompat.getColorStateList(this, R.color.button_in_cart)
            addToCartButton.backgroundTintList = colorStateList
        }


        // Устанавливаем данные в виджеты
        binding.nameTextView.text = game?.name
        binding.descriptionTextView.text = game?.description
        binding.releaseDateTextView.text =
            if (!game?.releaseDate.isNullOrEmpty()) formatReleaseDate(
                game?.releaseDate ?: ""
            ) else "Неизвестно"
        binding.platformTextView.text = game?.platform ?: "Неизвестно"
        binding.genreTextView.text = game?.genre ?: "Неизвестно"
        binding.publisherTextView.text = game?.publisher ?: "Неизвестно"
        binding.ageTextView.text = if (game?.age == 0) "Без ограничений" else "С ${game?.age} лет"

        // Установка цены
        PriceUtils.setPrice(
            this,
            binding.priceTextView,
            binding.oldPriceTextView,
            game?.price ?: 0.0,
            game?.oldPrice
        )

        // Настройка карусели изображений
        setupImageCarousel(game?.imageUrl ?: "", game?.gallery ?: listOf())

        // Слушатель скролла для управления видимостью тулбара
        binding.scrollView.viewTreeObserver.addOnScrollChangedListener {
            val scrollY = binding.scrollView.scrollY
            val threshold =
                binding.imageCarouselViewPager.height / 5 // Высота в px, после которой показываем тулбар

            if (scrollY > threshold) {
                if (binding.toolbar.visibility != View.VISIBLE) {
                    showToolbarWithAnimation()
                    binding.toolbar.title =
                        binding.nameTextView.text.toString() // Устанавливаем заголовок тулбара
                }
            } else {
                if (binding.toolbar.visibility != View.GONE) {
                    hideToolbarWithAnimation()
                }
            }
        }


        // Инициализация кнопки "В корзину"
        setupAddToCartButton()


        // Обработка кнопки "Назад"
        val backButton = findViewById<ImageButton>(R.id.backButton)
        backButton.setOnClickListener {
            finish() // Закрываем текущую Activity
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }

        // Настройка тулбара
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            title = game?.name // Устанавливаем название игры в тулбар
            setDisplayHomeAsUpEnabled(true) // Включаем кнопку "назад"
        }



        binding.toolbar.setNavigationOnClickListener {
            finish()
        }

        // Устанавливаем белый фон тулбара
        binding.toolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.white))

        // Настройка цвета стрелки навигации
        val backButtonDrawable = ContextCompat.getDrawable(this, R.drawable.ic_arrow_back_large)
        binding.toolbar.navigationIcon = backButtonDrawable


        // Изначально скрываем тулбар
        binding.toolbar.visibility = View.GONE


        // Обработчик навигации в таббаре
        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    val intent = Intent(this, MainActivity::class.java)
                    intent.putExtra("active_tab", R.id.nav_home)
                    startActivity(intent)
                    overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_down)
                    true
                }

                R.id.nav_catalog -> {
                    startActivity(Intent(this, CatalogActivity::class.java))
                    finish()
                    overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_down)
                    true
                }

                R.id.nav_cart -> {
                    startActivity(Intent(this, CartActivity::class.java))
                    finish()
                    overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_down)
                    true
                }

                else -> false
            }
        }
    }

    private fun showToolbarWithAnimation() {
        binding.toolbar.visibility = View.VISIBLE
        val fadeIn = AlphaAnimation(0f, 1f)
        fadeIn.duration = 300 // Длительность анимации в миллисекундах
        binding.toolbar.startAnimation(fadeIn)
    }

    private fun hideToolbarWithAnimation() {
        val fadeOut = AlphaAnimation(1f, 0f).apply {
            duration = 300 // Длительность анимации в миллисекундах
            setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {}

                override fun onAnimationRepeat(animation: Animation?) {}

                override fun onAnimationEnd(animation: Animation?) {
                    binding.toolbar.visibility = View.GONE
                }
            })
        }
        binding.toolbar.startAnimation(fadeOut)
    }


    // Форматирование даты релиза
    private fun formatReleaseDate(date: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd MMMM yyyy года", Locale("ru"))
            val parsedDate = inputFormat.parse(date)
            parsedDate?.let { outputFormat.format(it) } ?: date
        } catch (e: Exception) {
            date // Возвращаем оригинальную строку при ошибке
        }
    }

    private fun setupImageCarousel(mainImage: String, gallery: List<String>) {
        val imageList = mutableListOf<String>()
        imageList.add(mainImage) // Добавляем основное изображение
        imageList.addAll(gallery) // Добавляем изображения из галереи

        val carouselAdapter = ImageCarouselAdapter(this, imageList)
        binding.imageCarouselViewPager.adapter = carouselAdapter

        // Устанавливаем кастомные индикаторы
        setupCustomIndicator(imageList.size)
    }

    private fun setupCustomIndicator(count: Int) {
        val indicatorLayout = binding.indicatorLinearLayout
        indicatorLayout.removeAllViews() // Убираем старые индикаторы

        for (i in 0 until count) {
            val dot = View(this)
            val params = LinearLayout.LayoutParams(
                18, // ширина точки
                18  // высота точки
            )
            params.marginEnd = 12 // Отступ между точками
            dot.layoutParams = params
            dot.setBackgroundResource(R.drawable.dot_selector) // Устанавливаем селектор для изменения состояния точки
            indicatorLayout.addView(dot)
        }

        // Первую точку сделать активной
        if (indicatorLayout.childCount > 0) {
            indicatorLayout.getChildAt(0).isSelected = true
        }

        // Обработка изменения текущей страницы в ViewPager2
        binding.imageCarouselViewPager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                updateIndicator(position)
            }
        })
    }

    private fun updateIndicator(position: Int) {
        val indicatorLayout = binding.indicatorLinearLayout
        for (i in 0 until indicatorLayout.childCount) {
            indicatorLayout.getChildAt(i).isSelected = i == position
        }
    }


    private fun setupAddToCartButton() {
        val addToCartButton = binding.addToCartButton
        game?.let {
            if (it.inCart) {
                isInCart = true
                addToCartButton.text = "В корзине"
                val colorStateList = ContextCompat.getColorStateList(this, R.color.button_in_cart)
                addToCartButton.backgroundTintList = colorStateList
            }
        }
        addToCartButton.setOnClickListener {
            game?.let {
                if (!isInCart) {
                    // Если товар не в корзине, добавляем его
                    isInCart = true
                    it.inCart = true
                    CartManager.addToCart(this, it)  // Передаем контекст и игру
                    addToCartButton.text = "В корзине"
                    val colorStateList =
                        ContextCompat.getColorStateList(this, R.color.button_in_cart)
                    addToCartButton.backgroundTintList = colorStateList
                } else {
                    // Если товар уже в корзине, открываем корзину
                    val intent = Intent(this, CartActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_down)
                }
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        // Анимация перехода при возврате назад
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }
}



package com.example.gamestoreapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gamestoreapp.adapter.GameAdapter
import com.example.gamestoreapp.database.GameDatabase
import com.example.gamestoreapp.database.toGame
import com.example.gamestoreapp.model.Game

// Главный экран приложения, отображающий акции, новинки и поиск
class HomeFragment : Fragment() {

    private lateinit var promotionsRecyclerView: RecyclerView // RecyclerView для списка акций
    private lateinit var newReleasesRecyclerView: RecyclerView // RecyclerView для списка новинок
    private var promotionsAdapter: GameAdapter? = null // Адаптер для акций
    private var newReleasesAdapter: GameAdapter? = null // Адаптер для новинок

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Устанавливаем светлую тему
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // Настройка поля поиска
        val searchInput = view.findViewById<EditText>(R.id.searchInput)
        val clearButton = view.findViewById<ImageView>(R.id.clearSearchButton)

        // Изменение цвета выделения текста
        val highlightColor = ContextCompat.getColor(requireContext(), R.color.primary_color)
        searchInput.highlightColor = highlightColor

        // Логика отображения кнопки очистки
        searchInput.addTextChangedListener {
            clearButton.visibility =
                if (it.isNullOrEmpty()) View.GONE else View.VISIBLE // Показываем или скрываем кнопку
        }

        // Обработка клика на кнопку очистки
        clearButton.setOnClickListener {
            searchInput.text.clear() // Очищаем текстовое поле
            clearButton.visibility = View.GONE // Скрываем кнопку
        }

        // Закрытие клавиатуры при тапе по области вне инпута
        view.findViewById<View>(R.id.rootLayout).setOnTouchListener { _, _ ->
            hideKeyboard()
            view.findViewById<View>(R.id.rootLayout).requestFocus() // Снимаем фокус с поля ввода
            true
        }

        // Обработка нажатия клавиши Enter на клавиатуре
        searchInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                actionId == EditorInfo.IME_ACTION_DONE
            ) {
                val searchQuery = searchInput.text.toString().trim()

                if (searchQuery.isNotEmpty()) {
                    // Переход на экран результатов поиска
                    val intent = Intent(requireContext(), SearchResultsActivity::class.java).apply {
                        putExtra("query", searchQuery)
                    }
                    startActivity(intent)
                    requireActivity().overridePendingTransition(
                        R.anim.slide_in_up,
                        R.anim.slide_out_down
                    )
                    hideKeyboard()
                    true
                } else {
                    hideKeyboard()
                    view.findViewById<View>(R.id.rootLayout)
                        .requestFocus() // Снимаем фокус с поля ввода
                    Toast.makeText(requireContext(), "Введите текст для поиска", Toast.LENGTH_SHORT)
                        .show()
                    false
                }
            } else {
                false
            }
        }

        // Настройка RecyclerView для акций и новинок
        promotionsRecyclerView = view.findViewById(R.id.promotionsRecyclerView)
        newReleasesRecyclerView = view.findViewById(R.id.newReleasesRecyclerView)

        promotionsRecyclerView.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.HORIZONTAL,
            false
        )
        newReleasesRecyclerView.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.HORIZONTAL,
            false
        )

        // Загрузка данных для RecyclerView
        loadGames()

        return view
    }

    override fun onResume() {
        super.onResume()
        // Обновляем адаптеры при возврате на фрагмент
        promotionsAdapter?.notifyDataSetChanged()
        newReleasesAdapter?.notifyDataSetChanged()
    }

    // Загрузка данных из базы данных
    private fun loadGames() {
        val database = GameDatabase.getInstance(requireContext())

        // Загрузка акций из базы данных
        database.gameDao().getDiscountedGamesLive().observe(viewLifecycleOwner) { games ->
            val savedPosition =
                (promotionsRecyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
            promotionsAdapter = GameAdapter(
                games = games.map { it.toGame() },
                onCardClick = { game ->
                    openGameDetails(game) // Переход на экран карточки товара
                }
            )
            promotionsRecyclerView.adapter = promotionsAdapter

            // Восстанавливаем позицию скроллинга
            promotionsRecyclerView.scrollToPosition(savedPosition)
        }

        // Загрузка новинок из базы данных
        database.gameDao().getRecentGamesLive().observe(viewLifecycleOwner) { games ->
            val savedPosition =
                (newReleasesRecyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
            newReleasesAdapter = GameAdapter(
                games = games.map { it.toGame() },
                onCardClick = { game ->
                    openGameDetails(game) // Переход на экран карточки товара
                }
            )
            newReleasesRecyclerView.adapter = newReleasesAdapter

            // Восстанавливаем позицию скроллинга
            newReleasesRecyclerView.scrollToPosition(savedPosition)
        }
    }

    // Переход на экран карточки товара
    private fun openGameDetails(game: Game) {
        val intent = Intent(context, ProductDetailsActivity::class.java).apply {
            putExtra("game", game) // Передаем объект Game в Intent
        }
        context?.startActivity(intent)
        requireActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }

    // Скрытие клавиатуры
    private fun hideKeyboard() {
        val inputMethodManager =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val view = requireActivity().currentFocus
        if (view != null) {
            inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}

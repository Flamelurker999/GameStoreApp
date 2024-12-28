package com.example.gamestoreapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.gamestoreapp.R
import com.example.gamestoreapp.GameApi
import com.example.gamestoreapp.StoreApi
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.InputStreamReader

@Database(entities = [GameEntity::class, StoreEntity::class], version = 4, exportSchema = false)
@TypeConverters(Converters::class) // Используем конвертеры для преобразования сложных типов
abstract class GameDatabase : RoomDatabase() {

    // Предоставляем доступ к DAO для взаимодействия с таблицами
    abstract fun gameDao(): GameDao
    abstract fun storeDao(): StoreDao

    companion object {
        @Volatile
        private var INSTANCE: GameDatabase? = null

        // Миграция с версии 1 на версию 2: добавляем новые колонки в таблицу `games`
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE games ADD COLUMN genre TEXT NOT NULL DEFAULT ''")
                database.execSQL("ALTER TABLE games ADD COLUMN publisher TEXT NOT NULL DEFAULT ''")
                database.execSQL("ALTER TABLE games ADD COLUMN gallery TEXT NOT NULL DEFAULT '[]'")
                database.execSQL("ALTER TABLE games ADD COLUMN age INTEGER NOT NULL DEFAULT 0")
            }
        }

        // Миграция с версии 2 на версию 3: добавляем колонку `inCart` для статуса корзины
        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE games ADD COLUMN inCart INTEGER NOT NULL DEFAULT 0")
            }
        }

        // Миграция с версии 3 на версию 4: добавляем таблицу `stores`
        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    """
            CREATE TABLE IF NOT EXISTS `stores` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                `name` TEXT NOT NULL,
                `metro` TEXT,
                `address` TEXT NOT NULL
            )
        """
                )
            }
        }

        // Получение или создание экземпляра базы данных
        fun getInstance(context: Context): GameDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    GameDatabase::class.java,
                    "game_database"
                )
                    .addMigrations(
                        MIGRATION_1_2,
                        MIGRATION_2_3,
                        MIGRATION_3_4
                    ) // Добавляем миграции
                    .addCallback(DatabaseCallback(context)) // Добавляем предзагрузку данных
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    // Callback для предзагрузки данных при создании базы данных
    private class DatabaseCallback(private val context: Context) : Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            CoroutineScope(Dispatchers.IO).launch {
                // Предзагрузка игр
                val games = fetchGamesOrFallback(context)
                getInstance(context).gameDao().insertGames(games)

                // Предзагрузка магазинов
                val stores = loadStoresFromJson(context)
                getInstance(context).storeDao().insertStores(stores)
            }
        }
    }
}

// Попытка загрузки списка игр с сервера или из локального JSON
suspend fun fetchGamesOrFallback(context: Context): List<GameEntity> {
    return try {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://example.com/") // URL API
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(GameApi::class.java)
        val response = api.getGames() // Запрос на сервер

        if (response.isSuccessful && response.body() != null) {
            response.body()!! // Возвращаем данные из API
        } else {
            loadGamesFromJson(context) // Возвращаем данные из JSON
        }
    } catch (e: Exception) {
        e.printStackTrace()
        loadGamesFromJson(context) // При ошибке возвращаем данные из JSON
    }
}

// Попытка загрузки списка магазинов с сервера или из локального JSON
suspend fun fetchStoresOrFallback(context: Context): List<StoreEntity> {
    return try {
        // Попытка загрузки данных с сервера
        val retrofit = Retrofit.Builder()
            .baseUrl("https://example.com/") // URL API
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(StoreApi::class.java)
        val response = api.getStores() // Запрос на сервер

        if (response.isSuccessful && response.body() != null) {
            response.body()!! // Возвращаем данные из API
        } else {
            loadStoresFromJson(context) // Возвращаем данные из JSON
        }
    } catch (e: Exception) {
        e.printStackTrace()
        loadStoresFromJson(context) // При ошибке возвращаем данные из JSON
    }
}

// Загрузка списка игр из локального JSON
fun loadGamesFromJson(context: Context): List<GameEntity> {
    return try {
        val inputStream = context.resources.openRawResource(R.raw.games)
        val reader = InputStreamReader(inputStream)
        val type = object : TypeToken<List<GameEntity>>() {}.type
        Gson().fromJson(reader, type)
    } catch (e: Exception) {
        e.printStackTrace()
        emptyList() // Возвращаем пустой список при ошибке
    }
}

// Загрузка списка магазинов из локального JSON
fun loadStoresFromJson(context: Context): List<StoreEntity> {
    return try {
        val inputStream = context.resources.openRawResource(R.raw.stores) // stores.json
        val reader = InputStreamReader(inputStream)
        val type = object : TypeToken<List<StoreEntity>>() {}.type
        Gson().fromJson(reader, type)
    } catch (e: Exception) {
        e.printStackTrace()
        emptyList() // Возвращаем пустой список при ошибке
    }
}
package com.money.xpensesbookproject.data.database

import android.R.attr.data
import android.content.Context
import android.graphics.Color
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.money.xpensesbookproject.data.dao.CategoryDao
import com.money.xpensesbookproject.data.dao.TransactionDao
import com.money.xpensesbookproject.data.model.Category
import com.money.xpensesbookproject.data.model.Transaction
import com.money.xpensesbookproject.data.model.TransactionType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@Database(
    entities = [Transaction::class, Category::class],
    version = 1
)

@TypeConverters(TransactionTypeConvert::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun transactionDao(): TransactionDao
    abstract fun categoryDao(): CategoryDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "xpensebook_database"
                ).apply {
                    addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            // Add default categories
                            INSTANCE?.let { database ->
                                CoroutineScope(Dispatchers.IO).launch {
                                    val categoryDao = database.categoryDao()
                                    // Default expense categories
                                    categoryDao.insertCategory(
                                        Category(
                                            "Food & Dining",
                                            TransactionType.EXPENSE,
                                            Color.parseColor("#FF5252")
                                        )
                                    )
                                    categoryDao.insertCategory(
                                        Category(
                                            "Transportation",
                                            TransactionType.EXPENSE,
                                            Color.parseColor("#448AFF")
                                        )
                                    )
                                    categoryDao.insertCategory(
                                        Category(
                                            "Shopping",
                                            TransactionType.EXPENSE,
                                            Color.parseColor("#4CAF50")
                                        )
                                    )
                                    categoryDao.insertCategory(
                                        Category(
                                            "Bills & Utilities",
                                            TransactionType.EXPENSE,
                                            Color.parseColor("#FFC107")
                                        )
                                    )
                                    categoryDao.insertCategory(
                                        Category(
                                            "Healthcare",
                                            TransactionType.EXPENSE,
                                            Color.parseColor("#E040FB")
                                        )
                                    )

                                    // Default income categories
                                    categoryDao.insertCategory(
                                        Category(
                                            "Salary",
                                            TransactionType.INCOME,
                                            Color.parseColor("#00BCD4")
                                        )
                                    )
                                    categoryDao.insertCategory(
                                        Category(
                                            "Freelance",
                                            TransactionType.INCOME,
                                            Color.parseColor("#9C27B0")
                                        )
                                    )
                                    categoryDao.insertCategory(
                                        Category(
                                            "Investments",
                                            TransactionType.INCOME,
                                            Color.parseColor("#4CAF50")
                                        )
                                    )
                                }
                            }
                        }
                    })
                }.build()
                INSTANCE = instance
                instance
            }
        }
    }
}
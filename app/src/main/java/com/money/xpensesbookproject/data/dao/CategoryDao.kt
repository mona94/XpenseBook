package com.money.xpensesbookproject.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.money.xpensesbookproject.data.model.Category
import com.money.xpensesbookproject.data.model.CategoryTotal
import com.money.xpensesbookproject.data.model.TransactionType
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {

    @Query("SELECT * FROM categories")
    fun getAllCategories(): Flow<List<Category>>

    @Query("SELECT * FROM categories WHERE type = :type")
    fun getCategoriesByType(type: TransactionType): Flow<List<Category>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: Category)

    @Update
    fun updateCategory(category: Category)

    @Query("SELECT c.name as categoryName ,COALESCE(SUM(t.amount),0.00) as totalAmount FROM categories c LEFT JOIN transactions t  ON c.name = t.category WHERE c.type = :type  AND (t.date >= :startDate OR t.date IS NULL) GROUP BY c.name")
    fun getCategoryTotal(
        type: TransactionType,
        startDate: Long = 0
    ): Flow<List<CategoryTotal>>

}
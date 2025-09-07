package com.money.xpensesbookproject.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.money.xpensesbookproject.data.model.CategoryTotal
import com.money.xpensesbookproject.data.model.Transaction
import com.money.xpensesbookproject.data.model.TransactionType
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {

    @Query("SELECT * FROM transactions ORDER BY date DESC")
    fun getAllTransactions(): Flow<List<Transaction>>

    @Query("SELECT * FROM transactions WHERE type = :type ORDER BY date DESC")
    fun getTransactionsByType(type: TransactionType): Flow<List<Transaction>>

    @Query("SELECT SUM(amount) FROM transactions WHERE type = :type")
    suspend fun getTotalByType(type: TransactionType): Double?

    @Query("SELECT category as categoryName,SUM(amount) as totalAmount FROM transactions WHERE type = :type  AND date >= :startDate GROUP BY category")
    fun getCategoryTotal(
        type: TransactionType,
        startDate: Long = 0
    ): Flow<List<CategoryTotal>>

    @Insert
    suspend fun insertTransaction(transaction: Transaction)

    @Delete
    suspend fun deleteTransaction(transaction: Transaction)
}
package com.money.xpensesbookproject.data.repository

import com.money.xpensebook.utils.DateFormatter
import com.money.xpensesbookproject.data.dao.CategoryDao
import com.money.xpensesbookproject.data.dao.TransactionDao
import com.money.xpensesbookproject.data.model.Category
import com.money.xpensesbookproject.data.model.CategoryTotal
import com.money.xpensesbookproject.data.model.Transaction
import com.money.xpensesbookproject.data.model.TransactionType
import kotlinx.coroutines.flow.Flow
import java.util.Calendar

class FinanceRepository(
    private val transactionDao: TransactionDao,
    private val categoryDao: CategoryDao
) {

    val allTransactions = transactionDao.getAllTransactions()
    val allCategories = categoryDao.getAllCategories()

    val monthlyTransactions = transactionDao.getTransactionsForMonth(getStartMonth(), getMonthEnd())

    fun getStartMonth(): Long {
        val calendar = Calendar.getInstance()

        // Start of month
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startOfMonth = calendar.timeInMillis

        return startOfMonth
    }

    fun getMonthEnd(): Long {
        val calendar = Calendar.getInstance()
        // End of month
        calendar.add(Calendar.MONTH, 1)
        calendar.add(Calendar.DAY_OF_MONTH, -1)
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        val endOfMonth = calendar.timeInMillis

        return endOfMonth
    }


    fun getTransactionsByType(type: TransactionType) =
        transactionDao.getTransactionsByType(type,getStartMonth(),getMonthEnd())

    fun getCategoriesByType(type: TransactionType) =
        categoryDao.getCategoriesByType(type)


    fun getCategoryTotals(
        type: TransactionType,
        startDate: Long = 0
    ): Flow<List<CategoryTotal>> =
        transactionDao.getCategoryTotal(type, startDate)

    suspend fun getTotalByType(type: TransactionType) =
        transactionDao.getTotalByType(type) ?: 0.0

    suspend fun addTransaction(transaction: Transaction) {
        transactionDao.insertTransaction(transaction)
    }

    suspend fun deleteTransaction(transaction: Transaction) {
        transactionDao.deleteTransaction(transaction)
    }

    suspend fun deleteCategory(category: Category){
        categoryDao.deleteCategory(category)
    }
    suspend fun addCategory(category: Category) {
        categoryDao.insertCategory(category)
    }

    fun getMonthRange(): Pair<Long, Long> {
        val calendar = Calendar.getInstance()

        // Start of month
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startOfMonth = calendar.timeInMillis

        // End of month
        calendar.add(Calendar.MONTH, 1)
        calendar.add(Calendar.DAY_OF_MONTH, -1)
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        val endOfMonth = calendar.timeInMillis

        return Pair(startOfMonth, endOfMonth)
    }

    fun getTransactionsByDateRange(start: Long, end: Long): Flow<List<Transaction>> {
        return transactionDao.getTransactionsForMonth(start, end)
    }

    fun getTransactionsByTypeForMonth(
        type: TransactionType,
        month: Int,
        year: Int
    ): Flow<List<Transaction>> {
        val (start, end) = getMonthTimestamps(month, year)
        return transactionDao.getTransactionsByType(type, start, end)
    }

    private fun getMonthTimestamps(month: Int, year: Int): Pair<Long, Long> {
        val calendar = java.util.Calendar.getInstance()

        // Start of month
        calendar.set(year, month - 1, 1, 0, 0, 0) // month is 1-based
        calendar.set(java.util.Calendar.MILLISECOND, 0)
        val start = calendar.timeInMillis

        // End of month
        calendar.set(java.util.Calendar.DAY_OF_MONTH, calendar.getActualMaximum(java.util.Calendar.DAY_OF_MONTH))
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 23)
        calendar.set(java.util.Calendar.MINUTE, 59)
        calendar.set(java.util.Calendar.SECOND, 59)
        calendar.set(java.util.Calendar.MILLISECOND, 999)
        val end = calendar.timeInMillis

        return Pair(start, end)
    }

}
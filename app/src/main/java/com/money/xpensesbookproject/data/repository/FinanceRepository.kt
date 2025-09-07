package com.money.xpensesbookproject.data.repository

import com.money.xpensesbookproject.data.dao.CategoryDao
import com.money.xpensesbookproject.data.dao.TransactionDao
import com.money.xpensesbookproject.data.model.Category
import com.money.xpensesbookproject.data.model.CategoryTotal
import com.money.xpensesbookproject.data.model.Transaction
import com.money.xpensesbookproject.data.model.TransactionType
import kotlinx.coroutines.flow.Flow

class FinanceRepository(
    private val transactionDao: TransactionDao,
    private val categoryDao: CategoryDao
) {

    val allTransactions = transactionDao.getAllTransactions()
    val allCategories = categoryDao.getAllCategories()


    fun getTransactionsByType(type: TransactionType) =
        transactionDao.getTransactionsByType(type)

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

    suspend fun addCategory(category: Category) {
        categoryDao.insertCategory(category)
    }
}
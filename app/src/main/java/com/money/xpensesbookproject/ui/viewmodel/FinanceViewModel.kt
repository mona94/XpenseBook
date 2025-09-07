package com.money.xpensesbookproject.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.money.xpensesbookproject.data.database.AppDatabase
import com.money.xpensesbookproject.data.model.Category
import com.money.xpensesbookproject.data.model.CategoryTotal
import com.money.xpensesbookproject.data.model.Transaction
import com.money.xpensesbookproject.data.model.TransactionType
import com.money.xpensesbookproject.data.repository.FinanceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class FinanceViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: FinanceRepository
    val allTransactions: Flow<List<Transaction>>
    val allCategories: Flow<List<Category>>

    init {
        val database = AppDatabase.getDatabase(application)
        repository = FinanceRepository(database.transactionDao(), database.categoryDao())
        allTransactions = repository.allTransactions
        allCategories = repository.allCategories
    }

    fun getTransactionsByType(type: TransactionType): Flow<List<Transaction>> {
        return repository.getTransactionsByType(type)
    }

    fun getCategoriesByType(type: TransactionType): Flow<List<Category>> {
        return repository.getCategoriesByType(type)
    }

    fun getCategoryTotals(type: TransactionType, startDate: Long = 0): Flow<List<CategoryTotal>> {
        return repository.getCategoryTotals(type, startDate)
    }

    fun addTransaction(transaction: Transaction) = viewModelScope.launch {
        repository.addTransaction(transaction)
    }

    fun deleteTransaction(transaction: Transaction) = viewModelScope.launch {
        repository.deleteTransaction(transaction)
    }

    fun addCategory(category: Category) = viewModelScope.launch {
        repository.addCategory(category)
    }

    suspend fun getTotalByType(type: TransactionType): Double {
        return repository.getTotalByType(type)
    }
}

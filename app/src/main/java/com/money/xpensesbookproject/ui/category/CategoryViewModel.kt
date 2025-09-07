package com.money.xpensesbookproject.ui.category

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.money.xpensesbookproject.data.database.AppDatabase
import com.money.xpensesbookproject.data.model.Category
import com.money.xpensesbookproject.data.model.TransactionType
import com.money.xpensesbookproject.data.repository.FinanceRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CategoryViewModel(
    application: Application
) : AndroidViewModel(application) {

    // Initialize repository first
    private val repository: FinanceRepository = FinanceRepository(
        AppDatabase.getDatabase(application).transactionDao(),
        AppDatabase.getDatabase(application).categoryDao()
    )

    private val _typeFilter = MutableStateFlow(TransactionType.INCOME)

    // Now we can safely use repository since it's already initialized

    @OptIn(ExperimentalCoroutinesApi::class)
    val category = _typeFilter
        .flatMapLatest { filter ->
            when (filter) {
                TransactionType.INCOME -> repository.getCategoriesByType(TransactionType.INCOME)
                TransactionType.EXPENSE -> repository.getCategoriesByType(TransactionType.EXPENSE)
            }
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    fun setCategoryFilter(filter: TransactionType) {
        _typeFilter.value = filter
    }

    fun deleteCategory(category: Category) {
        viewModelScope.launch {
            repository.deleteCategory(category)
        }
    }


}
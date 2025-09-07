package com.money.xpensesbookproject.ui.dashboard

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.money.xpensesbookproject.data.database.AppDatabase
import com.money.xpensesbookproject.data.model.CategoryTotal
import com.money.xpensesbookproject.data.model.DashboardState
import com.money.xpensesbookproject.data.model.ExpenseCategoryData
import com.money.xpensesbookproject.data.model.TransactionType
import com.money.xpensesbookproject.data.repository.FinanceRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class DashboardViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: FinanceRepository = FinanceRepository(
        AppDatabase.getDatabase(application).transactionDao(),
        AppDatabase.getDatabase(application).categoryDao()
    )

    private val incomeTotalFlow = repository.allTransactions
        .map { transactions ->
            transactions.filter { it.type == TransactionType.INCOME }
                .sumOf { it.amount }
        }

    private val expensesTotalFlow = repository.allTransactions
        .map { transactions ->
            transactions.filter { it.type == TransactionType.EXPENSE }
                .sumOf { it.amount }
        }

    val dashboardState: StateFlow<DashboardState> = combine(
        incomeTotalFlow,
        expensesTotalFlow,
        repository.getCategoryTotals(TransactionType.EXPENSE)
    ) { income: Double, expenses: Double, categoryTotals: List<CategoryTotal> ->
        DashboardState(
            totalIncome = income,
            totalExpenses = expenses,
            balance = income - expenses,
            expensesCategory = categoryTotals.map { categoryTotal ->
                ExpenseCategoryData(
                    category = categoryTotal.categoryName,
                    amount = categoryTotal.totalAmount
                )
            }

        )

    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DashboardState()
    )

}
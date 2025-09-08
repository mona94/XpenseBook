package com.money.xpensesbookproject.ui.report

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.money.xpensesbookproject.data.database.AppDatabase
import com.money.xpensesbookproject.data.model.CategoryTotal
import com.money.xpensesbookproject.data.model.DashboardState
import com.money.xpensesbookproject.data.model.ExpenseCategoryData
import com.money.xpensesbookproject.data.model.Transaction
import com.money.xpensesbookproject.data.model.TransactionType
import com.money.xpensesbookproject.data.repository.FinanceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class ReportViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: FinanceRepository = FinanceRepository(
        AppDatabase.getDatabase(application).transactionDao(),
        AppDatabase.getDatabase(application).categoryDao()
    )

    private val _selectedMonthYearList = MutableStateFlow<Pair<Int, Int>?>(null) // month, year
    val selectedMonthYearList: StateFlow<Pair<Int, Int>?> = _selectedMonthYearList

    // Transactions flow that updates when monthYear changes
    val transactionsList: StateFlow<List<Transaction>> = _selectedMonthYearList
        .filterNotNull()
        .flatMapLatest { (month, year) ->
            val (start, end) = getMonthTimestamps(month, year)
            repository.getTransactionsByDateRange(start, end)
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // Selected month/year (default: current month)
    private val selectedMonthYear = MutableStateFlow(
        Pair(
            java.util.Calendar.getInstance().get(java.util.Calendar.MONTH) + 1, // 1-based
            java.util.Calendar.getInstance().get(java.util.Calendar.YEAR)
        )
    )

    val dashboardState: StateFlow<DashboardState> =
        selectedMonthYear.flatMapLatest { (month, year) ->
            val incomeFlow =
                repository.getTransactionsByTypeForMonth(TransactionType.INCOME, month, year)
                    .map { it.sumOf { txn -> txn.amount } }

            val expensesFlow =
                repository.getTransactionsByTypeForMonth(TransactionType.EXPENSE, month, year)
                    .map { it.sumOf { txn -> txn.amount } }

            combine(incomeFlow, expensesFlow) { income, expenses ->
                DashboardState(
                    totalIncome = income,
                    totalExpenses = expenses,
                    balance = income - expenses,
                )
            }
//            val categoryTotalsFlow = repository.getCategoryTotals(TransactionType.EXPENSE, getMonthTimestamps(month, year).first)


//            combine(incomeFlow, expensesFlow, categoryTotalsFlow) { income, expenses, categoryTotals ->
//                DashboardState(
//                    totalIncome = income,
//                    totalExpenses = expenses,
//                    balance = income - expenses,
//                    expensesCategory = categoryTotals.map { categoryTotal ->
//                        ExpenseCategoryData(
//                            category = categoryTotal.categoryName,
//                            amount = categoryTotal.totalAmount
//                        )
//                    }
//                )
//            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = DashboardState()
        )

    fun setMonthYear(month: Int, year: Int) {
        selectedMonthYear.value = month to year
        _selectedMonthYearList.value = month to year
    }

    private fun getMonthTimestamps(month: Int, year: Int): Pair<Long, Long> {
        val calendar = java.util.Calendar.getInstance()

        // Start of month
        calendar.set(year, month - 1, 1, 0, 0, 0) // month is 1-based
        calendar.set(java.util.Calendar.MILLISECOND, 0)
        val start = calendar.timeInMillis

        // End of month
        calendar.set(
            java.util.Calendar.DAY_OF_MONTH,
            calendar.getActualMaximum(java.util.Calendar.DAY_OF_MONTH)
        )
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 23)
        calendar.set(java.util.Calendar.MINUTE, 59)
        calendar.set(java.util.Calendar.SECOND, 59)
        calendar.set(java.util.Calendar.MILLISECOND, 999)
        val end = calendar.timeInMillis

        return Pair(start, end)
    }


}

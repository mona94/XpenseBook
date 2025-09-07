package com.money.xpensesbookproject.data.model

data class DashboardState(
    val totalIncome: Double = 0.0,
    val totalExpenses: Double = 0.0,
    val balance: Double = 0.0,
    val expensesCategory: List<ExpenseCategoryData> = emptyList()
)

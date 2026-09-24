package com.stonks.app.ui.viewmodel

import com.stonks.app.data.TransactionEntity
import com.stonks.app.model.TimeFilter
import com.stonks.app.ui.theme.AppThemeMode

enum class MemeType {
    STONKS,
    NOT_STONKS
}

data class MemeCelebration(
    val type: MemeType,
    val title: String,
    val subtitle: String
)

data class StonksUiState(
    val transactions: List<TransactionEntity> = emptyList(),
    val filteredTransactions: List<TransactionEntity> = emptyList(),
    val totalIncome: Double = 0.0,
    val totalExpense: Double = 0.0,
    val netBalance: Double = 0.0,
    val monthlyBudget: Double = 800.0,
    val selectedPeriod: TimeFilter = TimeFilter.MONTH,
    val searchQuery: String = "",
    val themeMode: AppThemeMode = AppThemeMode.DARK,
    val memeCelebration: MemeCelebration? = null,
    val categoryBreakdown: Map<String, Double> = emptyMap(),
    val currentMonth: String = ""
)

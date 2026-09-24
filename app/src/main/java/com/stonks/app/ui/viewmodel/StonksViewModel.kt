package com.stonks.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.stonks.app.data.TransactionEntity
import com.stonks.app.data.TransactionRepository
import com.stonks.app.model.TimeFilter
import com.stonks.app.model.TransactionType
import com.stonks.app.ui.theme.AppThemeMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class StonksViewModel(
    private val repository: TransactionRepository
) : ViewModel() {

    private val monthFormat = SimpleDateFormat("yyyy-MM", Locale.getDefault())
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val currentMonthStr = monthFormat.format(Date())

    private val _selectedPeriod = MutableStateFlow(TimeFilter.MONTH)
    private val _searchQuery = MutableStateFlow("")
    private val _monthlyBudget = MutableStateFlow(800.0)
    private val _themeMode = MutableStateFlow(AppThemeMode.DARK)
    private val _memeCelebration = MutableStateFlow<MemeCelebration?>(null)

    val uiState: StateFlow<StonksUiState> = combine(
        repository.allTransactions,
        _selectedPeriod,
        _searchQuery,
        _monthlyBudget,
        _themeMode,
        _memeCelebration
    ) { allTx, period, query, budget, theme, meme ->
        val currentYear = currentMonthStr.substring(0, 4)

        val periodFiltered = when (period) {
            TimeFilter.MONTH -> allTx.filter { it.month == currentMonthStr || it.date.startsWith(currentMonthStr) }
            TimeFilter.YEAR -> allTx.filter { it.date.startsWith(currentYear) }
            TimeFilter.ALL -> allTx
        }

        val searchFiltered = if (query.isBlank()) {
            periodFiltered
        } else {
            val q = query.trim().lowercase(Locale.ROOT)
            periodFiltered.filter { 
                it.title.lowercase(Locale.ROOT).contains(q) ||
                it.category.lowercase(Locale.ROOT).contains(q) ||
                it.note.lowercase(Locale.ROOT).contains(q)
            }
        }

        var incomeSum = 0.0
        var expenseSum = 0.0
        val catMap = mutableMapOf<String, Double>()

        for (tx in periodFiltered) {
            if (tx.type == TransactionType.INCOME.name) {
                incomeSum += tx.amount
            } else {
                expenseSum += tx.amount
                val currentCat = catMap.getOrDefault(tx.category, 0.0)
                catMap[tx.category] = currentCat + tx.amount
            }
        }

        val net = incomeSum - expenseSum

        StonksUiState(
            transactions = allTx,
            filteredTransactions = searchFiltered,
            totalIncome = incomeSum,
            totalExpense = expenseSum,
            netBalance = net,
            monthlyBudget = budget,
            selectedPeriod = period,
            searchQuery = query,
            themeMode = theme,
            memeCelebration = meme,
            categoryBreakdown = catMap,
            currentMonth = currentMonthStr
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = StonksUiState(currentMonth = currentMonthStr)
    )

    fun addTransaction(
        title: String,
        amount: Double,
        type: TransactionType,
        category: String,
        date: String,
        note: String
    ) {
        viewModelScope.launch {
            val txDate = if (date.isBlank()) dateFormat.format(Date()) else date
            val month = if (txDate.length >= 7) txDate.substring(0, 7) else currentMonthStr
            val entity = TransactionEntity(
                id = UUID.randomUUID().toString(),
                title = title.ifBlank { if (type == TransactionType.INCOME) "Entrata" else "Spesa" },
                amount = amount,
                type = type.name,
                category = category,
                date = txDate,
                month = month,
                note = note,
                timestamp = System.currentTimeMillis()
            )
            repository.insert(entity)

            // Trigger Stonks or Not Stonks meme effect
            if (type == TransactionType.INCOME) {
                _memeCelebration.value = MemeCelebration(
                    type = MemeType.STONKS,
                    title = "STONKS! ↗",
                    subtitle = "+${String.format(Locale.ITALY, "%.2f €", amount)} registrati con successo!"
                )
            } else if (amount > 150.0 || (uiState.value.totalExpense + amount > uiState.value.monthlyBudget)) {
                _memeCelebration.value = MemeCelebration(
                    type = MemeType.NOT_STONKS,
                    title = "NOT STONKS! ↘",
                    subtitle = "Spesa di ${String.format(Locale.ITALY, "%.2f €", amount)} registrata. Monitora il budget!"
                )
            }
        }
    }

    fun updateTransaction(
        id: String,
        title: String,
        amount: Double,
        type: TransactionType,
        category: String,
        date: String,
        note: String
    ) {
        viewModelScope.launch {
            val txDate = if (date.isBlank()) dateFormat.format(Date()) else date
            val month = if (txDate.length >= 7) txDate.substring(0, 7) else currentMonthStr
            val entity = TransactionEntity(
                id = id,
                title = title,
                amount = amount,
                type = type.name,
                category = category,
                date = txDate,
                month = month,
                note = note,
                timestamp = System.currentTimeMillis()
            )
            repository.update(entity)
        }
    }

    fun deleteTransaction(id: String) {
        viewModelScope.launch {
            repository.deleteById(id)
        }
    }

    fun setPeriod(period: TimeFilter) {
        _selectedPeriod.value = period
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setMonthlyBudget(budget: Double) {
        _monthlyBudget.value = budget
    }

    fun setThemeMode(mode: AppThemeMode) {
        _themeMode.value = mode
    }

    fun dismissMeme() {
        _memeCelebration.value = null
    }

    fun clearAllData() {
        viewModelScope.launch {
            repository.clearAll()
        }
    }

    fun exportToCsv(): String {
        val list = uiState.value.transactions
        val sb = StringBuilder()
        sb.append("ID,Data,Titolo,Importo,Tipo,Categoria,Note\n")
        for (tx in list) {
            sb.append("${tx.id},${tx.date},\"${tx.title}\",${tx.amount},${tx.type},\"${tx.category}\",\"${tx.note}\"\n")
        }
        return sb.toString()
    }

    fun importFromCsv(csvText: String): Int {
        var count = 0
        viewModelScope.launch {
            val lines = csvText.lines()
            val newEntities = mutableListOf<TransactionEntity>()
            for ((index, line) in lines.withIndex()) {
                if (index == 0 || line.isBlank()) continue
                val parts = line.split(",")
                if (parts.size >= 6) {
                    try {
                        val d = parts[1].trim()
                        val title = parts[2].trim().replace("\"", "")
                        val amt = parts[3].trim().toDoubleOrNull() ?: 0.0
                        val type = if (parts[4].trim().equals("INCOME", ignoreCase = true)) "INCOME" else "EXPENSE"
                        val cat = parts[5].trim().replace("\"", "")
                        val note = if (parts.size > 6) parts.subList(6, parts.size).joinToString(",").replace("\"", "") else ""
                        val m = if (d.length >= 7) d.substring(0, 7) else currentMonthStr
                        newEntities.add(
                            TransactionEntity(
                                id = UUID.randomUUID().toString(),
                                title = title,
                                amount = amt,
                                type = type,
                                category = cat,
                                date = d,
                                month = m,
                                note = note
                            )
                        )
                        count++
                    } catch (_: Exception) {}
                }
            }
            if (newEntities.isNotEmpty()) {
                repository.insertAll(newEntities)
            }
        }
        return count
    }

    class Factory(private val repository: TransactionRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(StonksViewModel::class.java)) {
                return StonksViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}

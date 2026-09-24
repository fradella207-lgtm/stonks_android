package com.stonks.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.stonks.app.data.StonksDatabase
import com.stonks.app.data.TransactionEntity
import com.stonks.app.data.TransactionRepository
import com.stonks.app.model.TransactionCategory
import com.stonks.app.model.TransactionType
import com.stonks.app.ui.screens.MainScreen
import com.stonks.app.ui.theme.StonksTheme
import com.stonks.app.ui.viewmodel.StonksViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = StonksDatabase.getDatabase(applicationContext)
        val repository = TransactionRepository(database.transactionDao())

        val viewModel: StonksViewModel by viewModels {
            StonksViewModel.Factory(repository)
        }

        // Seed initial data if database is empty for immediate rich user experience
        lifecycleScope.launch {
            val existing = repository.allTransactions.first()
            if (existing.isEmpty()) {
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val today = dateFormat.format(Date())
                val month = today.substring(0, 7)

                val initialData = listOf(
                    TransactionEntity(
                        id = UUID.randomUUID().toString(),
                        title = "Stipendio Mensile",
                        amount = 2150.00,
                        type = TransactionType.INCOME.name,
                        category = TransactionCategory.SALARY.displayName,
                        date = today,
                        month = month,
                        note = "Bonifico stipendio"
                    ),
                    TransactionEntity(
                        id = UUID.randomUUID().toString(),
                        title = "Spesa Supermercato",
                        amount = 84.50,
                        type = TransactionType.EXPENSE.name,
                        category = TransactionCategory.FOOD.displayName,
                        date = today,
                        month = month,
                        note = "Spesa settimanale"
                    ),
                    TransactionEntity(
                        id = UUID.randomUUID().toString(),
                        title = "Abbonamento Mezzi",
                        amount = 35.00,
                        type = TransactionType.EXPENSE.name,
                        category = TransactionCategory.TRANSPORT.displayName,
                        date = today,
                        month = month,
                        note = "Tessera trasporti urbani"
                    ),
                    TransactionEntity(
                        id = UUID.randomUUID().toString(),
                        title = "Dividendi ETF",
                        amount = 45.20,
                        type = TransactionType.INCOME.name,
                        category = TransactionCategory.INVESTMENTS.displayName,
                        date = today,
                        month = month,
                        note = "Distribuzione trimestrale"
                    ),
                    TransactionEntity(
                        id = UUID.randomUUID().toString(),
                        title = "Cena Sushi con amici",
                        amount = 38.00,
                        type = TransactionType.EXPENSE.name,
                        category = TransactionCategory.FOOD.displayName,
                        date = today,
                        month = month,
                        note = "Venerdì sera"
                    )
                )
                repository.insertAll(initialData)
            }
        }

        setContent {
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            StonksTheme(themeMode = uiState.themeMode) {
                MainScreen(viewModel = viewModel)
            }
        }
    }
}

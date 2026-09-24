package com.stonks.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.stonks.app.data.TransactionEntity
import com.stonks.app.ui.components.AddEditTransactionSheet
import com.stonks.app.ui.components.Header
import com.stonks.app.ui.components.MenuBottomSheet
import com.stonks.app.ui.components.StonksMemeOverlay
import com.stonks.app.ui.theme.StonksGreen
import com.stonks.app.ui.viewmodel.StonksViewModel

enum class MainTab {
    HISTORY,
    REPORTS
}

@Composable
fun MainScreen(
    viewModel: StonksViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var currentTab by remember { mutableStateOf(MainTab.HISTORY) }

    var isAddSheetOpen by remember { mutableStateOf(false) }
    var transactionToEdit by remember { mutableStateOf<TransactionEntity?>(null) }
    var isMenuOpen by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding(),
        topBar = {
            Header(
                onOpenMenu = { isMenuOpen = true }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    transactionToEdit = null
                    isAddSheetOpen = true
                },
                containerColor = StonksGreen,
                contentColor = MaterialTheme.colorScheme.background,
                shape = CircleShape,
                modifier = Modifier
                    .size(56.dp)
                    .testTag("add_transaction_fab")
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Aggiungi nuova spesa o entrata",
                    modifier = Modifier.size(28.dp)
                )
            }
        },
        bottomBar = {
            // Nothing OS style bottom tab bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = 24.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .clip(RoundedCornerShape(28.dp))
                        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.95f))
                        .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(28.dp))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // History Tab Button
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .clip(RoundedCornerShape(24.dp))
                            .background(
                                if (currentTab == MainTab.HISTORY) MaterialTheme.colorScheme.surfaceVariant
                                else Color.Transparent
                            )
                            .clickable { currentTab = MainTab.HISTORY }
                            .testTag("tab_history_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.List,
                                contentDescription = "Movimenti",
                                tint = if (currentTab == MainTab.HISTORY) StonksGreen else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "MOVIMENTI",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                fontWeight = if (currentTab == MainTab.HISTORY) FontWeight.Bold else FontWeight.Medium,
                                color = if (currentTab == MainTab.HISTORY) StonksGreen else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Reports Tab Button
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .clip(RoundedCornerShape(24.dp))
                            .background(
                                if (currentTab == MainTab.REPORTS) MaterialTheme.colorScheme.surfaceVariant
                                else Color.Transparent
                            )
                            .clickable { currentTab = MainTab.REPORTS }
                            .testTag("tab_reports_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Analytics,
                                contentDescription = "Report",
                                tint = if (currentTab == MainTab.REPORTS) StonksGreen else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "REPORT",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                fontWeight = if (currentTab == MainTab.REPORTS) FontWeight.Bold else FontWeight.Medium,
                                color = if (currentTab == MainTab.REPORTS) StonksGreen else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                MainTab.HISTORY -> {
                    HistoryScreen(
                        transactions = uiState.filteredTransactions,
                        totalIncome = uiState.totalIncome,
                        totalExpense = uiState.totalExpense,
                        netBalance = uiState.netBalance,
                        monthlyBudget = uiState.monthlyBudget,
                        selectedPeriod = uiState.selectedPeriod,
                        searchQuery = uiState.searchQuery,
                        onPeriodSelect = { viewModel.setPeriod(it) },
                        onSearchChange = { viewModel.setSearchQuery(it) },
                        onEditTransaction = {
                            transactionToEdit = it
                            isAddSheetOpen = true
                        },
                        onDeleteTransaction = { viewModel.deleteTransaction(it) }
                    )
                }

                MainTab.REPORTS -> {
                    ReportsScreen(
                        totalIncome = uiState.totalIncome,
                        totalExpense = uiState.totalExpense,
                        netBalance = uiState.netBalance,
                        categoryBreakdown = uiState.categoryBreakdown,
                        selectedPeriod = uiState.selectedPeriod,
                        onPeriodSelect = { viewModel.setPeriod(it) }
                    )
                }
            }

            // Meme Celebration Overlay
            StonksMemeOverlay(
                celebration = uiState.memeCelebration,
                onDismiss = { viewModel.dismissMeme() }
            )
        }
    }

    // Add or Edit Transaction Bottom Sheet
    if (isAddSheetOpen) {
        AddEditTransactionSheet(
            initialTransaction = transactionToEdit,
            onDismiss = {
                isAddSheetOpen = false
                transactionToEdit = null
            },
            onSave = { title, amount, type, category, date, note ->
                if (transactionToEdit == null) {
                    viewModel.addTransaction(title, amount, type, category, date, note)
                } else {
                    viewModel.updateTransaction(
                        transactionToEdit!!.id,
                        title,
                        amount,
                        type,
                        category,
                        date,
                        note
                    )
                }
                isAddSheetOpen = false
                transactionToEdit = null
            }
        )
    }

    // Menu and Settings Bottom Sheet
    if (isMenuOpen) {
        MenuBottomSheet(
            currentTheme = uiState.themeMode,
            monthlyBudget = uiState.monthlyBudget,
            onThemeChange = { viewModel.setThemeMode(it) },
            onBudgetChange = { viewModel.setMonthlyBudget(it) },
            onClearAll = { viewModel.clearAllData() },
            onDismiss = { isMenuOpen = false }
        )
    }
}

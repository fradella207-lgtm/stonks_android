package com.stonks.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.stonks.app.data.TransactionEntity
import com.stonks.app.model.TransactionCategory
import com.stonks.app.model.TransactionType
import com.stonks.app.ui.theme.NotStonksRed
import com.stonks.app.ui.theme.StonksGreen
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddEditTransactionSheet(
    initialTransaction: TransactionEntity? = null,
    onDismiss: () -> Unit,
    onSave: (title: String, amount: Double, type: TransactionType, category: String, date: String, note: String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val dateFormat = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }

    var selectedType by remember {
        mutableStateOf(
            if (initialTransaction?.type == TransactionType.INCOME.name) TransactionType.INCOME
            else TransactionType.EXPENSE
        )
    }
    var amountText by remember {
        mutableStateOf(initialTransaction?.let { String.format(Locale.US, "%.2f", it.amount) } ?: "")
    }
    var title by remember { mutableStateOf(initialTransaction?.title ?: "") }
    var selectedCategory by remember {
        mutableStateOf(
            initialTransaction?.category ?: if (selectedType == TransactionType.INCOME) "Stipendio" else "Cibo & Spesa"
        )
    }
    var dateText by remember {
        mutableStateOf(initialTransaction?.date ?: dateFormat.format(Date()))
    }
    var note by remember { mutableStateOf(initialTransaction?.note ?: "") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val categories = remember(selectedType) {
        if (selectedType == TransactionType.INCOME) {
            listOf(
                TransactionCategory.SALARY,
                TransactionCategory.FREELANCE,
                TransactionCategory.INVESTMENTS,
                TransactionCategory.OTHER
            )
        } else {
            listOf(
                TransactionCategory.FOOD,
                TransactionCategory.TRANSPORT,
                TransactionCategory.HOUSING,
                TransactionCategory.ENTERTAINMENT,
                TransactionCategory.SHOPPING,
                TransactionCategory.HEALTH,
                TransactionCategory.OTHER
            )
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.background,
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (initialTransaction == null) "NUOVA OPERAZIONE" else "MODIFICA OPERAZIONE",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Chiudi pannello",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Segmented Type Selector: Spesa vs Entrata
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            if (selectedType == TransactionType.EXPENSE) NotStonksRed
                            else MaterialTheme.colorScheme.surfaceVariant
                        )
                        .clickable {
                            selectedType = TransactionType.EXPENSE
                            if (selectedCategory in listOf("Stipendio", "Freelance", "Investimenti")) {
                                selectedCategory = "Cibo & Spesa"
                            }
                        }
                        .testTag("type_expense_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "SPESA ↘",
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = if (selectedType == TransactionType.EXPENSE) MaterialTheme.colorScheme.background
                        else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            if (selectedType == TransactionType.INCOME) StonksGreen
                            else MaterialTheme.colorScheme.surfaceVariant
                        )
                        .clickable {
                            selectedType = TransactionType.INCOME
                            if (selectedCategory in listOf("Cibo & Spesa", "Trasporti", "Shopping")) {
                                selectedCategory = "Stipendio"
                            }
                        }
                        .testTag("type_income_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "ENTRATA ↗",
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = if (selectedType == TransactionType.INCOME) MaterialTheme.colorScheme.background
                        else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Amount Input
            OutlinedTextField(
                value = amountText,
                onValueChange = {
                    amountText = it
                    errorMessage = null
                },
                label = { Text("Importo (€)") },
                placeholder = { Text("0.00") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = if (selectedType == TransactionType.INCOME) StonksGreen else NotStonksRed,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("amount_input")
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Title / Description
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Descrizione") },
                placeholder = { Text(if (selectedType == TransactionType.INCOME) "es. Stipendio mensile" else "es. Spesa supermercato") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("title_input")
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Category Chips
            Text(
                text = "CATEGORIA",
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.forEach { cat ->
                    val isSelected = selectedCategory == cat.displayName
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (isSelected) {
                                    if (selectedType == TransactionType.INCOME) StonksGreen.copy(alpha = 0.2f)
                                    else NotStonksRed.copy(alpha = 0.2f)
                                } else MaterialTheme.colorScheme.surfaceVariant
                            )
                            .border(
                                1.dp,
                                if (isSelected) {
                                    if (selectedType == TransactionType.INCOME) StonksGreen else NotStonksRed
                                } else MaterialTheme.colorScheme.outlineVariant,
                                RoundedCornerShape(12.dp)
                            )
                            .clickable { selectedCategory = cat.displayName }
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = cat.displayName,
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) {
                                if (selectedType == TransactionType.INCOME) StonksGreen else NotStonksRed
                            } else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Date Input
            OutlinedTextField(
                value = dateText,
                onValueChange = { dateText = it },
                label = { Text("Data (YYYY-MM-DD)") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("date_input")
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Note Input
            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text("Note aggiuntive (opzionale)") },
                maxLines = 3,
                modifier = Modifier.fillMaxWidth()
            )

            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = errorMessage ?: "",
                    color = NotStonksRed,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Save Button
            Button(
                onClick = {
                    val amount = amountText.replace(",", ".").toDoubleOrNull()
                    if (amount == null || amount <= 0.0) {
                        errorMessage = "Inserisci un importo valido maggiore di 0"
                        return@Button
                    }
                    onSave(
                        title.ifBlank { if (selectedType == TransactionType.INCOME) "Entrata" else "Spesa" },
                        amount,
                        selectedType,
                        selectedCategory,
                        dateText,
                        note
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("save_transaction_button"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedType == TransactionType.INCOME) StonksGreen else NotStonksRed
                )
            ) {
                Text(
                    text = if (initialTransaction == null) "SALVA OPERAZIONE" else "AGGIORNA OPERAZIONE",
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Black,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.background
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

package com.stonks.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val amount: Double,
    val type: String, // "EXPENSE" or "INCOME"
    val category: String,
    val date: String, // YYYY-MM-DD
    val month: String, // YYYY-MM
    val note: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

package com.stonks.app.data

import kotlinx.coroutines.flow.Flow

class TransactionRepository(private val transactionDao: TransactionDao) {
    val allTransactions: Flow<List<TransactionEntity>> = transactionDao.getAllTransactions()

    fun getTransactionsByMonth(month: String): Flow<List<TransactionEntity>> =
        transactionDao.getTransactionsByMonth(month)

    fun getTransactionsByYear(year: String): Flow<List<TransactionEntity>> =
        transactionDao.getTransactionsByYear(year)

    suspend fun insert(transaction: TransactionEntity) =
        transactionDao.insertTransaction(transaction)

    suspend fun insertAll(transactions: List<TransactionEntity>) =
        transactionDao.insertAll(transactions)

    suspend fun update(transaction: TransactionEntity) =
        transactionDao.updateTransaction(transaction)

    suspend fun delete(transaction: TransactionEntity) =
        transactionDao.deleteTransaction(transaction)

    suspend fun deleteById(id: String) =
        transactionDao.deleteById(id)

    suspend fun clearAll() =
        transactionDao.clearAll()
}

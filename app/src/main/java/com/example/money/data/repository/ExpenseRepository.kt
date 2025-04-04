package com.example.money.data.repository

import com.example.money.data.models.Expense

interface ExpenseRepository {
    suspend fun readSmsExpenses(): List<Expense>
    suspend fun readDatabaseExpenses(): List<Expense>
    suspend fun saveExpenses(expenses: List<Expense>)
}

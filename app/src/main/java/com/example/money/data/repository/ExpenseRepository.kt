package com.example.money.data.repository

import com.example.money.data.models.Expense

interface ExpenseRepository {
    suspend fun readSmsExpenses(): List<Expense>
}

package com.example.money.utils

import com.example.money.data.models.Expense
import com.example.money.data.models.Recurrence
import com.example.money.data.models.TransactionFilterOption
import java.time.LocalDate

fun filterByTransactionType(
    expenses: List<Expense>,
    filterOption: TransactionFilterOption
): List<Expense> {
    return when (filterOption) {
        TransactionFilterOption.ALL -> expenses
        TransactionFilterOption.DEBITS -> expenses.filter { it.amount < 0 }
        TransactionFilterOption.CREDITS -> expenses.filter { it.amount > 0 }
    }
}

fun filterByRecurrence(
    expenses: List<Expense>,
    recurrence: Recurrence
): List<Expense> {
    val today = LocalDate.now()
    return when (recurrence) {
        Recurrence.Daily -> expenses.filter { it.date.toLocalDate() == today }
        Recurrence.Weekly -> {
            val startOfWeek = today.minusDays(today.dayOfWeek.value.toLong() - 1)
            expenses.filter { it.date.toLocalDate() >= startOfWeek }
        }
        Recurrence.Monthly -> expenses.filter {
            val date = it.date.toLocalDate()
            date.month == today.month && date.year == today.year
        }
        Recurrence.Yearly -> expenses.filter {
            it.date.toLocalDate().year == today.year
        }
        else -> expenses
    }
}

fun applyAllFilters(
    expenses: List<Expense>,
    filterOption: TransactionFilterOption,
    recurrence: Recurrence
): Pair<List<Expense>, Double> {
    val byType = filterByTransactionType(expenses, filterOption)
    val byRecurrence = filterByRecurrence(byType, recurrence)
    val sum = byRecurrence.sumOf { it.amount }
    return byRecurrence to sum
}


fun sumByRecurrenceOnly(expenses: List<Expense>, recurrence: Recurrence): Double {
    return filterByRecurrence(expenses, recurrence).sumOf { it.amount }
}




package com.example.money.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.money.data.models.Expense
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import kotlin.math.absoluteValue

data class DashboardUiState(
    val totalIncome: Double = 0.0,
    val totalExpense: Double = 0.0,
    val incomeChangePercent: Double = 0.0,
    val expenseChangePercent: Double = 0.0,
    val incomeMonthly: List<Float> = emptyList(),
    val expenseMonthly: List<Float> = emptyList(),
    val monthLabels: List<String> = emptyList(),
    val recentExpenses: List<Expense> = emptyList()
)
class DashboardViewModel(
    private val expensesViewModel: ExpensesViewModel
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            expensesViewModel.uiState.collect { expenseState ->
                val all = expenseState.allExpenses

                val now = LocalDate.now()
                val last30Days = now.minusDays(30)
                val prev30Days = last30Days.minusDays(30)

                val currentPeriod = all.filter { it.date.toLocalDate() in last30Days..now }
                val previousPeriod = all.filter { it.date.toLocalDate() in prev30Days..last30Days.minusDays(1) }


                val incomeNow = currentPeriod.filter { it.amount > 0 }.sumOf { it.amount }
                val incomeBefore = previousPeriod.filter { it.amount > 0 }.sumOf { it.amount }
                val expenseNow = currentPeriod.filter { it.amount < 0 }.sumOf { it.amount.absoluteValue }
                val expenseBefore = previousPeriod.filter { it.amount < 0 }.sumOf { it.amount.absoluteValue }

                val monthsBack = 3
                val monthlyLabels = (0..monthsBack).map { now.minusMonths(it.toLong()).month.name.substring(0, 3) }.reversed()

                val incomeMonthly = (0..monthsBack).map {
                    val month = now.minusMonths(it.toLong()).monthValue
                    all.filter { exp -> exp.amount > 0 && exp.date.monthValue == month }.sumOf { it.amount }.toFloat()
                }.reversed()

                val expenseMonthly = (0..monthsBack).map {
                    val month = now.minusMonths(it.toLong()).monthValue
                    all.filter { exp -> exp.amount < 0 && exp.date.monthValue == month }.sumOf { it.amount.absoluteValue }.toFloat()
                }.reversed()

                _uiState.update {
                    it.copy(
                        totalIncome = incomeNow,
                        totalExpense = expenseNow,
                        incomeChangePercent = calculateChange(incomeBefore, incomeNow),
                        expenseChangePercent = calculateChange(expenseBefore, expenseNow),
                        incomeMonthly = incomeMonthly,
                        expenseMonthly = expenseMonthly,
                        monthLabels = monthlyLabels,
                        recentExpenses = all.sortedByDescending { it.date }.take(5)
                    )
                }
            }
        }
    }

    private fun calculateChange(old: Double, new: Double): Double {
        return if (old == 0.0) 100.0 else ((new - old) / old) * 100
    }
}

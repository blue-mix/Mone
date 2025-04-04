
package com.example.money.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.money.data.models.CategorySpending
import com.example.money.data.models.Expense
import com.example.money.data.models.toCategorySpendingList
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate

enum class TransactionFilterType { CREDIT, DEBIT }

data class SpendingUiState(
    val allExpenses: List<Expense> = emptyList(),
    val filteredExpenses: List<Expense> = emptyList(),
    val categories: List<CategorySpending> = emptyList(),
    val totalAmount: Double = 0.0,
    val fromDate: LocalDate = LocalDate.now().minusDays(30), // Default to last 30 days
    val toDate: LocalDate = LocalDate.now(),
    val transactionFilter: TransactionFilterType = TransactionFilterType.DEBIT
)

class SpendingViewModel(
    private val expensesViewModel: ExpensesViewModel
) : ViewModel() {

    private val _uiState = MutableStateFlow(SpendingUiState())
    val uiState: StateFlow<SpendingUiState> = _uiState.asStateFlow()

    init {
        // Set default date range and apply the filter

        val start = LocalDate.now().minusDays(30) // Default to 30 days ago
        val end = LocalDate.now() // Default to today
        setDateRange(start, end) // Trigger the filter for the default range
        observeExpenses()
    }

    private fun observeExpenses() {
        viewModelScope.launch {
            expensesViewModel.uiState.collect { expenseState ->
                val all = expenseState.allExpenses
                _uiState.update {
                    it.copy(allExpenses = all)
                }
                // Filter and update categories and total when new expenses are observed
                applyDateFilter(_uiState.value.fromDate, _uiState.value.toDate)
            }
        }
    }

    private fun applyDateFilter(from: LocalDate, to: LocalDate) {
        val fromDateTime = from.atStartOfDay()
        val toDateTime = to.plusDays(1).atStartOfDay() // Include the full day for 'to' date
        val currentFilter = _uiState.value.transactionFilter

        val filtered = _uiState.value.allExpenses.filter { expense ->
            expense.date >= fromDateTime && expense.date < toDateTime
        } .filter {
            when (currentFilter) {
                TransactionFilterType.DEBIT -> it.amount < 0
                TransactionFilterType.CREDIT -> it.amount > 0
            }
        }

        val categorySpending = filtered.toCategorySpendingList()
        val totalAmount = categorySpending.sumOf { it.amount }

        _uiState.update {
            it.copy(
                fromDate = from,
                toDate = to,
                filteredExpenses = filtered,
                categories = categorySpending,
                totalAmount = totalAmount,

            )
        }
    }

    // Function to update the date range and trigger filtering
    fun setDateRange(from: LocalDate, to: LocalDate) {
        applyDateFilter(from, to)
    }
    fun setTransactionTypeFilter(filter: TransactionFilterType) {
        _uiState.update { it.copy(transactionFilter = filter) }
        applyDateFilter(_uiState.value.fromDate, _uiState.value.toDate)
    }

}

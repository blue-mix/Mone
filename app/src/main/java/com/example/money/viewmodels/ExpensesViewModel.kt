package com.example.money.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.money.data.models.Expense
import com.example.money.data.models.Recurrence
import com.example.money.data.models.TransactionFilterOption
import com.example.money.data.repository.ExpenseRepository
import com.example.money.utils.applyAllFilters
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ExpensesState(
    val recurrence: Recurrence = Recurrence.Daily,
    val allExpenses: List<Expense> = emptyList(),
    val filteredExpenses: List<Expense> = emptyList(),
    val transactionFilter: TransactionFilterOption = TransactionFilterOption.ALL,
    val sumTotal: Double = 0.0
)

class ExpensesViewModel(private val repo: ExpenseRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(ExpensesState())
    val uiState: StateFlow<ExpensesState> = _uiState.asStateFlow()

    init {
        loadExpenses()
    }

    private fun loadExpenses() {
        viewModelScope.launch {
            val smsExpenses = repo.readSmsExpenses()
            _uiState.update {
                it.copy(allExpenses = smsExpenses)
            }
            applyAllFilters()
        }
    }

    fun refresh() {
        loadExpenses()
    }

    fun setTransactionFilter(filter: TransactionFilterOption) {
        _uiState.update { it.copy(transactionFilter = filter) }
        applyAllFilters()
    }

    fun setRecurrence(recurrence: Recurrence) {
        _uiState.update { it.copy(recurrence = recurrence) }
        applyAllFilters()
    }

    private fun applyAllFilters() {
        val all = _uiState.value.allExpenses
        val (filtered, sum) = applyAllFilters(all, _uiState.value.transactionFilter, _uiState.value.recurrence)

        _uiState.update {
            it.copy(
                filteredExpenses = filtered,
                sumTotal = sum
            )
        }
    }

}

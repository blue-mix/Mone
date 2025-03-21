package com.example.money.viewmodels

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.money.models.Expense
import com.example.money.models.Recurrence
import com.example.money.pages.filterExpensesByRecurrence

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class AnalyticsState(
    val recurrence: Recurrence = Recurrence.Weekly,
    val recurrenceMenuOpened: Boolean = false
)

class AnalyticsViewModel: ViewModel() {
    private val _uiState = MutableStateFlow(AnalyticsState())
    val uiState: StateFlow<AnalyticsState> = _uiState.asStateFlow()

    private val _allExpenses = mutableStateListOf<Expense>()
    val allExpenses: List<Expense> = _allExpenses

    fun setAllExpenses(expenses: List<Expense>) {
        _allExpenses.clear()
        _allExpenses.addAll(expenses)
    }
    fun setRecurrence(recurrence: Recurrence) {
        _uiState.update { currentState ->
            currentState.copy(
                recurrence = recurrence
            )
        }
    }



}
package com.example.money.viewmodels

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.money.data.models.Expense
import com.example.money.data.models.Recurrence

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
//
//data class AnalyticsState(
//    val recurrence: Recurrence = Recurrence.Weekly,
//    val recurrenceMenuOpened: Boolean = false
//)
//
//class AnalyticsViewModel: ViewModel() {
//    private val _uiState = MutableStateFlow(AnalyticsState())
//    val uiState: StateFlow<AnalyticsState> = _uiState.asStateFlow()
//
//    private val _allExpenses = mutableStateListOf<Expense>()
//    val allExpenses: List<Expense> = _allExpenses
//
//    fun setAllExpenses(expenses: List<Expense>) {
//        _allExpenses.clear()
//        _allExpenses.addAll(expenses)
//    }
//    fun setRecurrence(recurrence: Recurrence) {
//        _uiState.update { currentState ->
//            currentState.copy(
//                recurrence = recurrence
//            )
//        }
//    }
//
//}


data class AnalyticsState(
    val recurrence: Recurrence = Recurrence.Monthly,
    val pageCount: Int = 12
)

class AnalyticsViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(AnalyticsState())
    val uiState: StateFlow<AnalyticsState> = _uiState.asStateFlow()

    fun setRecurrence(recurrence: Recurrence) {
        val pageCount = when (recurrence) {
            Recurrence.Weekly -> 53
            Recurrence.Monthly -> 12
            Recurrence.Yearly -> 1
            else -> 12
        }

        _uiState.update {
            it.copy(recurrence = recurrence, pageCount = pageCount)
        }
    }
    fun getTabLabels(recurrence: Recurrence): List<String> {
        return when (recurrence) {
            Recurrence.Monthly -> listOf(
                "Jan", "Feb", "Mar", "Apr", "May", "Jun",
                "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
            )
            Recurrence.Yearly -> (2020..2025).map { it.toString() }
            Recurrence.Weekly -> (1..52).map { "Week $it" }
            else -> emptyList()
        }
    }

}

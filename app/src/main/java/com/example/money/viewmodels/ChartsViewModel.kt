package com.example.money.viewmodels


import androidx.lifecycle.ViewModel
import com.example.money.models.Expense
import com.example.money.models.Recurrence
import com.example.money.utils.calculateDateRange
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.LocalDateTime
import java.time.LocalTime

data class ChartsState(
    val expenses: List<Expense> = listOf(),
    val dateStart: LocalDateTime = LocalDateTime.now(),
    val dateEnd: LocalDateTime = LocalDateTime.now(),
    val totalInRange: Double = 0.0
)

class ChartsViewModel(
    private val page: Int,
    val recurrence: Recurrence,
    allExpenses: List<Expense>
) : ViewModel() {
    private val _uiState = MutableStateFlow(ChartsState())
    val uiState: StateFlow<ChartsState> = _uiState.asStateFlow()

    //    init {
//        viewModelScope.launch(Dispatchers.IO) {
//            val (start, end) = calculateDateRange(recurrence, page)
//            val filteredExpenses = db.query<Expense>().find().filter {
//                (it.date.toLocalDate().isAfter(start) && it.date.toLocalDate()
//                    .isBefore(end)) || it.date.toLocalDate()
//                    .isEqual(start) || it.date.toLocalDate().isEqual(end)
//            }
//            val totalExpensesAmount = filteredExpenses.sumOf { it.amount }
//            viewModelScope.launch(Dispatchers.IO) {
//                _uiState.update {
//                    it.copy(
//                        dateStart = LocalDateTime.of(start, LocalTime.MIN),
//                        dateEnd = LocalDateTime.of(end, LocalTime.MAX),
//                        expenses = filteredExpenses,
//                        totalInRange = totalExpensesAmount,
//                    )
//                }
//            }
//        }
//    }
    init {
        val (start, end) = calculateDateRange(recurrence, page)

        val filteredExpenses = allExpenses.filter {
            val date = it.date.toLocalDate()
            (date.isAfter(start) && date.isBefore(end)) ||
                    date.isEqual(start) || date.isEqual(end)
        }

        val totalExpensesAmount = filteredExpenses.sumOf { it.amount }

        _uiState.update {
            it.copy(
                dateStart = start.atStartOfDay(),
                dateEnd = end.atTime(LocalTime.MAX),
                expenses = filteredExpenses,
                totalInRange = totalExpensesAmount,
            )
        }
    }

}
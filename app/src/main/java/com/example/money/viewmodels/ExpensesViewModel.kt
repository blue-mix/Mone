package com.example.money.viewmodels

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.money.data.Database.db
import com.example.money.data.models.CategorySpending
import com.example.money.data.models.Expense
import com.example.money.data.models.Recurrence
import com.example.money.data.models.TransactionFilterOption
import com.example.money.data.models.toCategorySpendingList
import com.example.money.data.repository.ExpenseRepository
import com.example.money.utils.SmsParser
import com.example.money.utils.applyAllFilters
import io.realm.kotlin.ext.query
import kotlinx.coroutines.Dispatchers
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
    val sumTotal: Double = 0.0,
    val categorySpending: List<CategorySpending> = emptyList()

)

class ExpensesViewModel(private val repo: ExpenseRepository, private val appContext: Context) :
    ViewModel() {

    private val _uiState = MutableStateFlow(ExpensesState())
    val uiState: StateFlow<ExpensesState> = _uiState.asStateFlow()

    private var hasSmsPermission: Boolean = false

    fun setPermissionGranted(granted: Boolean) {
        hasSmsPermission = granted

    }

    init {
        viewModelScope.launch(Dispatchers.IO) {
            // Load manual expenses (already saved in Realm)
            val manualExpenses = repo.readDatabaseExpenses()

            // Try reading SMS expenses (if permission granted)
            val smsExpenses = try {
                repo.readSmsExpenses()

            } catch (e: SecurityException) {
                emptyList() // If permission is missing, fallback safely
            }
            val combined = manualExpenses + smsExpenses

            _uiState.update {
                it.copy(allExpenses = combined)
            }

            // Collect updates from Realm for live manual expense tracking
            db.query<Expense>().asFlow().collect { result ->
                val latestManualExpenses = result.list
                val latestCombined = latestManualExpenses + smsExpenses

                _uiState.update { state ->
                    state.copy(
                        allExpenses = latestCombined,

                        sumTotal = latestCombined.sumOf { it.amount }
                    )
                }

                applyAllFilters()
            }
        }
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
        val (filtered, sum) = applyAllFilters(
            all,
            _uiState.value.transactionFilter,
            _uiState.value.recurrence
        )


        _uiState.update {
            it.copy(
                filteredExpenses = filtered,
                sumTotal = sum,
            )
        }
    }

}
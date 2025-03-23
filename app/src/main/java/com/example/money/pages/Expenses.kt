package com.example.money.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.money.components.custom.PickerTrigger
import com.example.money.components.expenses.ExpensesList
import com.example.money.data.models.Expense
import com.example.money.data.models.Recurrence
import com.example.money.data.models.TransactionFilterOption
import com.example.money.ui.theme.LabelSecondary
import com.example.money.ui.theme.TopAppBarBackground
import com.example.money.ui.theme.Typography
import com.example.money.viewmodels.CurrencyViewModel
import com.example.money.viewmodels.ExpensesViewModel
import java.text.DecimalFormat
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Expenses(
    navController: NavController,
    expensesList: List<Expense>,
    vm: ExpensesViewModel = viewModel(),
    currencyViewModel: CurrencyViewModel
) {
    val selectedCurrency by currencyViewModel.selectedCurrency.collectAsState()
    val recurrences = listOf(
        Recurrence.Daily,
        Recurrence.Weekly,
        Recurrence.Monthly,
        Recurrence.Yearly
    )
    val state by vm.uiState.collectAsState()
    var recurrenceMenuOpened by remember {
        mutableStateOf(false)
    }

    var selectedFilter by remember { mutableStateOf(TransactionFilterOption.ALL) }

    val allExpenses = (state.expenses + expensesList).distinctBy { it.note + it.date +it.amount}

// 👇 Filter based on transaction type
    val filteredExpenses = when (selectedFilter) {
        TransactionFilterOption.ALL -> allExpenses
        TransactionFilterOption.DEBITS -> allExpenses.filter { it.amount < 0 }
        TransactionFilterOption.CREDITS -> allExpenses.filter { it.amount > 0 }
    }

// 👇 Then apply recurrence filtering
    val filteredExpensesForSum = remember(state.recurrence, filteredExpenses) {
        filterExpensesByRecurrence(filteredExpenses, state.recurrence)
    }

    val totalSum = filteredExpensesForSum.sumOf { it.amount }

    Scaffold(
topBar = {
        MediumTopAppBar(
            title = { Text("Expenses", style = MaterialTheme.typography.titleLarge) },
            colors = TopAppBarDefaults.mediumTopAppBarColors(
                containerColor = TopAppBarBackground
            )
        )
    }, floatingActionButton = {
            ExtendedFloatingActionButton(
                text = { Text("Add Expense") },
                icon = { Icon(Icons.Filled.Add, contentDescription = "Add") },
                onClick = { navController.navigate("add") },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary

            )
        },
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Recurrence Picker Row
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Total for:",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    PickerTrigger(
                        state.recurrence.target ?: Recurrence.None.target,
                        onClick = { recurrenceMenuOpened = !recurrenceMenuOpened }
                    )

                    DropdownMenu(
                        expanded = recurrenceMenuOpened,
                        onDismissRequest = { recurrenceMenuOpened = false }
                    ) {
                        recurrences.forEach { recurrence ->
                            DropdownMenuItem(
                                text = { Text(recurrence.target) },
                                leadingIcon = { Icon(Icons.Default.CalendarToday, contentDescription = null) },
                                onClick = {
                                    vm.setRecurrence(recurrence)
                                    recurrenceMenuOpened = false
                                }
                            )
                        }
                    }
                }

                Surface(
                    tonalElevation = 2.dp,
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Total:", style = Typography.bodyLarge)
                        Row {
                            Text("₹", style = Typography.titleMedium, color = LabelSecondary)
                            Text(
                                text = DecimalFormat("##,##,###.##").format(totalSum),
                                style = Typography.titleMedium,
                                color = if (totalSum < 0)  Color(0xFFD32F2F) else Color(0xFF2E7D32) // rich green
                            )
                        }
                    }
                }


                TransactionFilterSegmentedButton(
                    selectedOption = selectedFilter,
                    onOptionSelected = { selectedFilter = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )


                ExpensesList(
                    expenses = filteredExpenses,
                    modifier = Modifier
                        .weight(1f)
                    .verticalScroll(
                            rememberScrollState()
                        ), currencyViewModel
                )
            }
        }
    )
}
fun filterExpensesByRecurrence(expenses: List<Expense>, recurrence: Recurrence): List<Expense> {
    val today = LocalDate.now()
    return when (recurrence) {
        Recurrence.Daily -> {
            expenses.filter { it.date.toLocalDate() == today }
        }
        Recurrence.Weekly -> {
            val startOfWeek = today.minusDays(today.dayOfWeek.value.toLong() - 1)
            expenses.filter { it.date.toLocalDate().isAfter(startOfWeek.minusDays(1)) }
        }
        Recurrence.Monthly -> {
            expenses.filter { it.date.toLocalDate().month == today.month && it.date.toLocalDate().year == today.year }
        }
        Recurrence.Yearly -> {
            expenses.filter { it.date.toLocalDate().year == today.year }
        }
        else -> expenses
    }
}

@Composable
fun TransactionFilterSegmentedButton(
    selectedOption: TransactionFilterOption,
    onOptionSelected: (TransactionFilterOption) -> Unit,
    modifier: Modifier = Modifier
) {
    val options = TransactionFilterOption.entries

    SingleChoiceSegmentedButtonRow(modifier = modifier) {
        options.forEachIndexed { index, option ->
            SegmentedButton(
                selected = option == selectedOption,
                onClick = { onOptionSelected(option) },
                shape = SegmentedButtonDefaults.itemShape(index, options.size),
                icon = {
                    when (option) {
                        TransactionFilterOption.ALL -> Icon(Icons.Default.FilterList, null)
                        TransactionFilterOption.DEBITS -> Icon(Icons.Default.TrendingDown, null)
                        TransactionFilterOption.CREDITS -> Icon(Icons.Default.TrendingUp, null)
                    }
                },
                label = { Text(option.label) }
            )
        }
    }
}





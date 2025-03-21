package com.example.money.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.money.components.PickerTrigger
import com.example.money.components.expensesList.ExpensesList
import com.example.money.models.Expense
import com.example.money.models.Recurrence
import com.example.money.models.TransactionFilterOption
import com.example.money.readSmsInbox
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

    // 👇 At the top of your composable
    var selectedFilter by remember { mutableStateOf(TransactionFilterOption.ALL) }

    val allExpenses = (state.expenses + expensesList).distinctBy { it.note + it.date }

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
//        topBar = {
//            MediumTopAppBar(
//                title = { Text("Expenses") },
//                colors = TopAppBarDefaults.mediumTopAppBarColors(
//                    containerColor = TopAppBarBackground
//                )
//            )
 //       },
topBar = {
        MediumTopAppBar(
            title = { Text("Expenses", style = MaterialTheme.typography.titleLarge) },
            colors = TopAppBarDefaults.mediumTopAppBarColors(
                containerColor = TopAppBarBackground
            )
        )
    },
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
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
                                onClick = {
                                    vm.setRecurrence(recurrence)
                                    recurrenceMenuOpened = false
                                }
                            )
                        }
                    }
                }

                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Text(
                        text = "₹",
                        style = MaterialTheme.typography.titleMedium,
                        color = LabelSecondary
                    )

                    Text(
                        text = DecimalFormat("##,##,###.##").format(totalSum),
                        style = MaterialTheme.typography.titleMedium,
                        color = if (totalSum<0)  Color.Red else Color.Green
                    )
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

@OptIn(ExperimentalMaterial3Api::class)
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
                shape = SegmentedButtonDefaults.itemShape(index = index, count = options.size),
                label = { Text(option.label) }
            )
        }
    }
}





package com.example.money.pages

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.Indicator
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.money.components.custom.PickerTrigger
import com.example.money.components.expenses.ExpensesList
import com.example.money.components.navigation.Routes
import com.example.money.data.models.Recurrence
import com.example.money.data.models.TransactionFilterOption
import com.example.money.ui.theme.LabelSecondary
import com.example.money.ui.theme.TopAppBarBackground
import com.example.money.ui.theme.Typography
import com.example.money.viewmodels.ExpensesViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import kotlin.time.Duration.Companion.seconds


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Expenses(
    navController: NavController,
    vm: ExpensesViewModel = viewModel()
) {
    val state by vm.uiState.collectAsState()
    val recurrences =
        listOf(Recurrence.Daily, Recurrence.Weekly, Recurrence.Monthly, Recurrence.Yearly)

    var recurrenceMenuOpened by remember { mutableStateOf(false) }


    val isRefreshing = remember { mutableStateOf(false) }

    val pullRefreshState = rememberPullToRefreshState(

    )
    val coroutineScope =rememberCoroutineScope()
Log.d("SmsParser","${state.filteredExpenses.size}")
    Scaffold(
        topBar = {
            MediumTopAppBar(
                title = { Text("Expenses", style = MaterialTheme.typography.titleLarge) },
                colors = TopAppBarDefaults.mediumTopAppBarColors(containerColor = TopAppBarBackground)
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = { Text("Add Expense") },
                icon = { Icon(Icons.Filled.Add, contentDescription = "Add") },
                onClick = { navController.navigate(Routes.Add.route) },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        }
    ) { innerPadding ->
//        PullToRefreshBox (
//            isRefreshing = isRefreshing.value,
//            onRefresh = {
//                coroutineScope.launch {
//                    isRefreshing.value = true
//                    delay(2.seconds)
//                    vm.refresh()
//
//
//                    isRefreshing.value = false }
//
//            },
//            modifier = Modifier
//                .padding(innerPadding)
//                .fillMaxSize(),
//            state = pullRefreshState,
//
//        ) {
            Column(
                modifier = Modifier

                    .padding(horizontal = 16.dp)
                    .padding(innerPadding)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Recurrence Picker
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        "Total for:",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    PickerTrigger(
                        state.recurrence.target,
                        onClick = { recurrenceMenuOpened = !recurrenceMenuOpened })

                    DropdownMenu(
                        expanded = recurrenceMenuOpened,
                        onDismissRequest = { recurrenceMenuOpened = false }
                    ) {
                        recurrences.forEach { recurrence ->
                            DropdownMenuItem(
                                text = { Text(recurrence.target) },
                                leadingIcon = { Icon(Icons.Default.CalendarToday, null) },
                                onClick = {
                                    vm.setRecurrence(recurrence)
                                    recurrenceMenuOpened = false
                                }
                            )
                        }
                    }
                }

                // Total Amount Card
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
                                text = DecimalFormat("##,##,###.##").format(state.sumTotal),
                                style = Typography.titleMedium,
                                color = if (state.sumTotal < 0) Color(0xFFD32F2F) else Color(
                                    0xFF2E7D32
                                )
                            )
                        }
                    }
                }

                // Filter Buttons
                TransactionFilterSegmentedButton(
                    selectedOption = state.transactionFilter,
                    onOptionSelected = { vm.setTransactionFilter(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )

                // Expenses List
                ExpensesList(
                    expenses = state.filteredExpenses,
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                )
            }

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

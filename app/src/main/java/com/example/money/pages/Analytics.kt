package com.example.money.pages


import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.money.components.charts.Charts
import com.example.money.components.navigation.Routes
import com.example.money.data.models.Recurrence
import com.example.money.ui.theme.TopAppBarBackground
import com.example.money.viewmodels.AnalyticsViewModel
import com.example.money.viewmodels.ExpensesViewModel


@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun AnalyticsPage(
    navController: NavController,
    expensesViewModel: ExpensesViewModel = viewModel(),
    analyticsViewModel: AnalyticsViewModel
) {
    val analyticsState by analyticsViewModel.uiState.collectAsState()
    val expensesState by expensesViewModel.uiState.collectAsState()
    val allExpenses = expensesState.allExpenses
    val recurrenceOptions = listOf("Weekly", "Monthly", "Yearly")
    var selectedOpt by remember { mutableIntStateOf(recurrenceOptions.indexOf(analyticsState.recurrence.target)) }
    val pageCount = when (analyticsState.recurrence) {
        Recurrence.Weekly -> 53
        Recurrence.Monthly -> 12
        Recurrence.Yearly -> 1
        else -> 53
    }

    val pagerState = rememberPagerState(pageCount = { pageCount })

    val coroutineScope = rememberCoroutineScope()
    Scaffold(

        topBar = {
            MediumTopAppBar(
                title = { Text("Analytics", style = MaterialTheme.typography.titleLarge) },
                colors = TopAppBarDefaults.mediumTopAppBarColors(containerColor = TopAppBarBackground)
            )
        }, floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate(Routes.Spending.route) // Replace with actual route name if different
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.PieChart, contentDescription = "Spending Breakdown")
            }
        }
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            SingleChoiceSegmentedButtonRow {
                recurrenceOptions.forEachIndexed { index, label ->
                    SegmentedButton(
                        selected = selectedOpt == index,
                        onClick = {
                            selectedOpt = index
                            val recurrence = when (index) {
                                0 -> Recurrence.Weekly
                                1 -> Recurrence.Monthly
                                2 -> Recurrence.Yearly
                                else -> Recurrence.Weekly
                            }
                            analyticsViewModel.setRecurrence(recurrence)
                        },
                        shape = SegmentedButtonDefaults.itemShape(index, recurrenceOptions.size)
                    ) {
                        Text(text = label)
                    }
                }
            }

            HorizontalPager(state = pagerState, reverseLayout = true) { page ->
                Charts(
                    navController = navController,
                    page = page,
                    recurrence = analyticsState.recurrence,
                    allExpenses = allExpenses
                )
            }
        }

        BackHandler {
            navController.navigate(Routes.Expenses.route) {
                popUpTo("expenses") { inclusive = true }
            }
        }
    }
}

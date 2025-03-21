package com.example.money.pages



import com.example.money.R
import com.example.money.components.charts.Charts
import com.example.money.models.Recurrence
import com.example.money.viewmodels.AnalyticsViewModel
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Icon
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import com.example.money.models.Expense


@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun AnalyticsPage(
    navController: NavController,
    expenses: List<Expense>,
    viewModel: AnalyticsViewModel = viewModel()
) {
    val analyticsViewModel: AnalyticsViewModel = viewModel()
    LaunchedEffect(Unit) {
        analyticsViewModel.setAllExpenses(expenses)
    }

    val uiState = viewModel.uiState.collectAsState().value
    val recurrenceOpt = mutableListOf(
        stringResource(R.string.recurrence_weekly),
        stringResource(R.string.recurrence_monthly),
        stringResource(R.string.recurrence_yearly)
    )
    var selectedOpt by remember {
        mutableIntStateOf(0)
    }
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Analytics") },
                navigationIcon = {
                    Icon(Icons.Filled.Analytics, contentDescription = null)
                }
            )
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
                recurrenceOpt.forEachIndexed { index, it ->
                    SegmentedButton(
                        selected = selectedOpt == index,
                        onClick = {
                            selectedOpt = index
                            when(index) {
                                0 -> viewModel.setRecurrence(Recurrence.Weekly)
                                1 -> viewModel.setRecurrence(Recurrence.Monthly)
                                2 -> viewModel.setRecurrence(Recurrence.Yearly)
                                else -> viewModel.setRecurrence(Recurrence.Weekly)
                            }
                        },
                        shape = SegmentedButtonDefaults.itemShape(
                            index = index,
                            count = recurrenceOpt.size
                        )
                    ) {
                        Text(text = it)
                    }
                }
            }
            val numOfPages = when (uiState.recurrence) {
                Recurrence.Weekly -> 53
                Recurrence.Monthly -> 12
                Recurrence.Yearly -> 1
                else -> 53
            }
            val pagerState = rememberPagerState(
                pageCount = { numOfPages }
            )
            HorizontalPager(
                state = pagerState,
                reverseLayout = true
            ) {
                Charts(
                    navController = navController,
                    page = it,
                    recurrence = uiState.recurrence,
                    allExpenses = viewModel.allExpenses
                )
            }
        }
        BackHandler {
            navController.navigate("expenses") {
                popUpTo("expenses") {
                    inclusive = true
                }
            }
        }
    }
}
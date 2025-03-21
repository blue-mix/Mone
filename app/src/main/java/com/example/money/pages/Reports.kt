package com.example.money.pages

import com.example.money.R
import com.example.money.components.charts.Charts
import com.example.money.models.Recurrence
import com.example.money.viewmodels.AnalyticsViewModel


import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.viewmodel.compose.viewModel

import com.example.money.components.ReportPage

import com.example.money.ui.theme.TopAppBarBackground
import com.example.money.viewmodels.CurrencyViewModel
import com.example.money.viewmodels.ReportsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Reports(vm: ReportsViewModel = viewModel() ,currencyViewModel: CurrencyViewModel) {
    val uiState = vm.uiState.collectAsState().value

    val recurrences = listOf(
        Recurrence.Weekly,
        Recurrence.Monthly,
        Recurrence.Yearly
    )


    Scaffold(
        topBar = {
            MediumTopAppBar(
                title = { Text("Reports") },
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = TopAppBarBackground
                ),
                actions = {
                    IconButton(onClick = vm::openRecurrenceMenu) {
                        Icon(
                            painterResource(id = R.drawable.ic_today),
                            contentDescription = "Change recurrence"
                        )
                    }
                    DropdownMenu(
                        expanded = uiState.recurrenceMenuOpened,
                        onDismissRequest = vm::closeRecurrenceMenu
                    ) {
                        recurrences.forEach { recurrence ->
                            DropdownMenuItem(text = { Text(recurrence.name) }, onClick = {
                                vm.setRecurrence(recurrence)
                                vm.closeRecurrenceMenu()
                            })
                        }
                    }
                }
            )
        },
        content = { innerPadding ->
            val numOfPages = when (uiState.recurrence) {
                Recurrence.Weekly -> 53
                Recurrence.Monthly -> 12
                Recurrence.Yearly -> 1
                else -> 53
            }
            val pagerState = rememberPagerState(
                initialPage = numOfPages - 1,  // Ensure the first page is the most recent (since we are reversing)
                pageCount = { numOfPages }
            )
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.padding(innerPadding),
                reverseLayout = true // This ensures the pages are laid out in reverse order
            ) { page ->
                ReportPage(innerPadding, page, uiState.recurrence )
            }
        }
    )
}
//
//import androidx.activity.compose.BackHandler
//import androidx.compose.foundation.ExperimentalFoundationApi
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.safeDrawingPadding
//import androidx.compose.foundation.pager.rememberPagerState
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.Analytics
//import androidx.compose.material3.ExperimentalMaterial3Api
//import androidx.lifecycle.viewmodel.compose.viewModel
//import androidx.compose.material3.Scaffold
//import androidx.compose.material3.SegmentedButton
//import androidx.compose.material3.SegmentedButtonDefaults
//import androidx.compose.material3.SingleChoiceSegmentedButtonRow
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.collectAsState
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableIntStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.res.stringResource
//import androidx.compose.ui.unit.dp
//import androidx.compose.foundation.pager.HorizontalPager
//import androidx.navigation.NavController
//
//
//
//@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
//@Composable
//fun AnalyticsPage(
//    navController: NavController,
//    viewModel: AnalyticsViewModel = viewModel()
//) {
//    val uiState = viewModel.uiState.collectAsState().value
//    val recurrenceOpt = mutableListOf(
//        stringResource(R.string.recurrence_weekly),
//        stringResource(R.string.recurrence_monthly),
//        stringResource(R.string.recurrence_yearly)
//    )
//    var selectedOpt by remember {
//        mutableIntStateOf(0)
//    }
//    Scaffold(
//
//    ) { contentPadding ->
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(contentPadding),
//            horizontalAlignment = Alignment.CenterHorizontally,
//            verticalArrangement = Arrangement.spacedBy(10.dp)
//        ) {
//            SingleChoiceSegmentedButtonRow {
//                recurrenceOpt.forEachIndexed { index, it ->
//                    SegmentedButton(
//                        selected = selectedOpt == index,
//                        onClick = {
//                            selectedOpt = index
//                            when(index) {
//                                0 -> viewModel.setRecurrence(Recurrence.Weekly)
//                                1 -> viewModel.setRecurrence(Recurrence.Monthly)
//                                2 -> viewModel.setRecurrence(Recurrence.Yearly)
//                                else -> viewModel.setRecurrence(Recurrence.Weekly)
//                            }
//                        },
//                        shape = SegmentedButtonDefaults.itemShape(
//                            index = index,
//                            count = recurrenceOpt.size
//                        )
//                    ) {
//                        Text(text = it)
//                    }
//                }
//            }
//            val numOfPages = when (uiState.recurrence) {
//                Recurrence.Weekly -> 53
//                Recurrence.Monthly -> 12
//                Recurrence.Yearly -> 1
//                else -> 53
//            }
//            val pagerState = rememberPagerState(
//                pageCount = { numOfPages }
//            )
//            HorizontalPager(
//                state = pagerState,
//                reverseLayout = true
//            ) {
//                Charts(
//                    navController = navController,
//                    page = it,
//                    recurrence = uiState.recurrence
//                )
//            }
//        }
//        BackHandler {
//            navController.navigate("expenses") {
//                popUpTo("expenses") {
//                    inclusive = true
//                }
//            }
//        }
//    }
//}
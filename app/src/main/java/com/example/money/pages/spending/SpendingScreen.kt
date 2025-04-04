package com.example.money.pages.spending

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AssistChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.money.components.custom2.DonutChartContainer
import com.example.money.ui.theme.TopAppBarBackground
import com.example.money.viewmodels.SpendingViewModel
import kotlinx.coroutines.delay
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpendingBreakdownScreen(navController: NavController, viewModel: SpendingViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    // Declare states for start and end date pickers
    var showStartPicker by remember { mutableStateOf(false) }
    var showEndPicker by remember { mutableStateOf(false) }
    var startDate by remember { mutableStateOf(viewModel.uiState.value.fromDate) }
    var endDate by remember { mutableStateOf(viewModel.uiState.value.toDate) }

    var isExpanded by remember { mutableStateOf(false) } // Track expansion state

    val dateRangeText = remember(startDate, endDate) {
        if (startDate != null && endDate != null) {
            val formatterStart = DateTimeFormatter.ofPattern("dd MMM")
            val formatterEnd = DateTimeFormatter.ofPattern("dd MMM yyyy")
            "${startDate.format(formatterStart)} - ${endDate.format(formatterEnd)}"
        } else {
            "Select Date Range"
        }
    }

    if (showStartPicker) {
        DatePickerDialog(
            onDateSelected = { selected ->
                startDate = selected
                showStartPicker = false
                showEndPicker = true
            },
            onDismiss = { showStartPicker = false }
        )
    }

    // Date picker dialog for end date selection
    if (showEndPicker) {
        DatePickerDialog(
            onDateSelected = { selected ->
                endDate = selected
                startDate.let { from ->
                    viewModel.setDateRange(from, endDate) // Trigger the view model to filter data
                }
                showEndPicker = false
            },
            onDismiss = { showEndPicker = false }
        )
    }

    Scaffold(
        topBar = {
            MediumTopAppBar(
                title = { Text("Spending") },
                navigationIcon = {
                    Row(
                        modifier = Modifier
                            .clickable { navController.popBackStack() }
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = { Icon(Icons.Default.MoreVert, null) },
                colors = TopAppBarDefaults.mediumTopAppBarColors(containerColor = TopAppBarBackground)
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            // Date range buttons: When clicked, they trigger the filtering logic
            DateRangeButtons { from, to ->
                viewModel.setDateRange(from, to) // Trigger data filtering on range selection
            }

            // Filters (Date Range Picker)
            FilterChipsRow(
                datetext = dateRangeText,
                selectedType = uiState.transactionFilter,
                onDateClick = { showStartPicker = true },
                onToggleType = { viewModel.setTransactionTypeFilter(it) }
            )

            if (uiState.categories.isEmpty()) {
                var showNodataAnimation by remember { mutableStateOf(false) }
                LaunchedEffect(key1 = true, block = {
                    delay(200)
                    showNodataAnimation = true
                })
                AnimatedVisibility(
                    visible = showNodataAnimation,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    NoArchivedGoals()
                }
            } else {
                AnimatedVisibility(visible = !isExpanded) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        // Donut Chart Container
                        DonutChartContainer(
                            uiState.categories,
                            uiState.totalAmount
                        ) // Your pie chart

                        Spacer(Modifier.height(16.dp)) // Adds space between the Donut Chart and LazyRow

                        // Category Chips
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(horizontal = 16.dp) // Adds padding to avoid edges
                        ) {
                            items(uiState.categories) { cat ->
                                AssistChip(
                                    onClick = {},
                                    label = {
                                        Box(modifier = Modifier.padding(vertical = 12.dp)) {
                                            Text("${cat.name} ${(cat.percentage * 100).toInt()}%")
                                        }
                                    }
                                )
                            }
                        }
                    }
                }


                Spacer(Modifier.height(24.dp))

                // Category List with a surface
                Surface(
                    modifier = Modifier
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(topStart = 36.dp, topEnd = 36.dp),
                    tonalElevation = 4.dp,
                    shadowElevation = 2.dp,
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        HorizontalDivider(
                            modifier = Modifier
                                .width(60.dp)
                                .align(Alignment.CenterHorizontally)
                                .padding(8.dp),
                            thickness = 3.dp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Category", style = MaterialTheme.typography.titleLarge)
                            // "See All" Button
                            TextButton(onClick = { isExpanded = !isExpanded }) {
                                Text(text = if (isExpanded) "Collapse" else "See all")
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Category Breakdown Card
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 800.dp), // Limit height of the category list
                        ) {
                            itemsIndexed(uiState.categories) { index, item ->
                                CategoryRow(item, modifier = Modifier.padding(vertical = 8.dp))

                                if (index < uiState.categories.size - 1) {
                                    HorizontalDivider(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 16.dp),
                                        thickness = 0.5.dp,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
                                    )
                                }
                            }
                        }
                    }
                }
            }

        }
    }
}

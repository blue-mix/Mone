package com.example.money.components.custom2

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.money.components.charts.SmoothDonutChart
import com.example.money.data.models.CategorySpending

import com.example.money.pages.spending.toDonutChartEntries

@Composable
fun DonutChartContainer(categories: List<CategorySpending>, totalAmount: Double) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp),
        contentAlignment = Alignment.Center
    ) {
        SmoothDonutChart(
            entries = categories.toDonutChartEntries(),
            totalAmountLabel = "₹${totalAmount}",
            modifier = Modifier.fillMaxSize()
        )
    }
}

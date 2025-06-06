package com.example.money.components.charts

import com.example.money.data.models.Recurrence

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.money.components.expenses.ExpensesByPeriod
import com.example.money.data.models.Expense
import com.example.money.utils.calculateDateRange
import com.example.money.utils.formatDayForRange
import java.text.DecimalFormat

@Composable
fun Charts(
    navController: NavController,
    page: Int,
    recurrence: Recurrence,
    allExpenses: List<Expense>
) {
    val (startDate, endDate) = remember(page, recurrence) {
        calculateDateRange(recurrence, page)
    }

    val expensesInRange = remember(page, recurrence, allExpenses) {
        allExpenses.filter {
            val date = it.date.toLocalDate()
            (date.isEqual(startDate) || date.isAfter(startDate)) &&
                    (date.isEqual(endDate) || date.isBefore(endDate))
        }
    }

    val totalInRange = remember(expensesInRange) {
        expensesInRange.sumOf { it.amount }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "${startDate.atStartOfDay().formatDayForRange()} - ${endDate.atStartOfDay().formatDayForRange()}",
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                text = "₹" + DecimalFormat("#,##0.##").format(totalInRange),
                style = MaterialTheme.typography.titleMedium,
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
        ) {
            when (recurrence) {
                Recurrence.Weekly -> WeeklyChart(expenses = expensesInRange)
                Recurrence.Monthly -> MonthlyChart(expenses = expensesInRange, month = startDate)
                Recurrence.Yearly -> YearlyChart(expenses = expensesInRange)
                else -> WeeklyChart(expenses = expensesInRange)
            }
        }

        Column(
            modifier = Modifier.verticalScroll(rememberScrollState())
        ) {
            ExpensesByPeriod(
                navController = navController,
                expenses = expensesInRange,
            )
            Spacer(modifier = Modifier.height(88.dp))
        }
    }
}

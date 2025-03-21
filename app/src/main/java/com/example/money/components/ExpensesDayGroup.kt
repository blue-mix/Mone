package com.example.money.components

import android.app.Application
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.money.models.DayExpenses
import com.example.money.ui.theme.LabelSecondary
import com.example.money.ui.theme.Typography
import com.example.money.utils.formatDay
import com.example.money.viewmodels.CurrencyViewModel
import java.lang.String.format
import java.text.DecimalFormat
import java.text.NumberFormat
import java.time.LocalDate
import java.util.Currency
import java.util.Locale


@Composable
fun ExpensesDayGroup(
    date: LocalDate,
    dayExpenses: DayExpenses,
    modifier: Modifier = Modifier,

) {
    val formattedTotal = "₹${String.format("%.2f", dayExpenses.total)}"
    Column(modifier = modifier) {
        Text(
            date.formatDay(),
            style = Typography.headlineMedium,
            color = LabelSecondary
        )
        Divider(modifier = Modifier.padding(top = 10.dp, bottom = 4.dp))
        dayExpenses.expenses.forEach { expense ->
            ExpenseRow(
                expense = expense,
                modifier = Modifier.padding(top = 12.dp),
            )
        }
        Divider(modifier = Modifier.padding(top = 16.dp, bottom = 4.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Total:", style = Typography.bodyMedium, color = LabelSecondary)
            Text(
                text = formattedTotal,
                style = Typography.headlineMedium,
                color = LabelSecondary
            )
        }
    }
}
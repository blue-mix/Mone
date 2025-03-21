package com.example.money.components.expensesList

import android.app.Application
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.money.components.ExpensesDayGroup
import com.example.money.mock.mockExpenses
import com.example.money.models.Expense
import com.example.money.models.groupedByDay
import com.example.money.ui.theme.MoneyTheme
import com.example.money.viewmodels.CurrencyViewModel


@Composable
fun ExpensesList(expenses: List<Expense>, modifier: Modifier = Modifier, currencyViewModel: CurrencyViewModel) {
    val groupedExpenses = expenses.groupedByDay()

    Column(modifier = modifier) {
        if (groupedExpenses.isEmpty()) {
            Text("No data for selected date range.", modifier = Modifier.padding(top = 32.dp))
        } else {
            groupedExpenses.keys.forEach { date ->
                if (groupedExpenses[date] != null) {
                    ExpensesDayGroup(
                        date = date,
                        dayExpenses = groupedExpenses[date]!!,
                        modifier = Modifier.padding(top = 24.dp),

                    )
                }
            }
        }
    }
}

